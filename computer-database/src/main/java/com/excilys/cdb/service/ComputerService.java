package com.excilys.cdb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;

public class ComputerService {

    private Optional<ComputerBuilder> builder;
    private boolean fromScratch;
    private static final Logger logger = LoggerFactory.getLogger(ComputerService.class);
    private ComputerDAO dao;

    public ComputerService(ComputerDAO dao) {
        builder = Optional.empty();
        fromScratch = false;
        this.dao = dao;
    }

    public boolean canAddEndDate() {
        return builder != null && builder.isPresent() && builder.get().getDateIntroduction() != null;
    }

    public Optional<LocalDateTime> getBeginDate() {
        return builder.map((build) -> build.getDateIntroduction());
    }

    public void buildNewComputer(String name) throws ComputerServiceException {
        if (name == null || name.equals("")) {
            logger.error("Aucun nom en entrée");
            throw new ComputerServiceException("Aucun nom n'est mis");
        }
        long id = dao.getMaxId() + 1;
        builder = Optional.of(new ComputerBuilder(id, name));
        fromScratch = true;
    }

    public void buildComputerForUpdate(Computer com) throws ComputerServiceException {
        if (com == null) {
            logger.error("Aucun ordinateur en entrée");
            throw new ComputerServiceException("L'ordinateur en entrée est null");
        }
        builder = Optional.of(new ComputerBuilder(com.getId(), com.getNom()));
        builder.get().setDateIntroduction(com.getDateIntroduction());
        builder.get().setDateDiscontinuation(com.getDateDiscontinuation());
        builder.get().setEntreprise(com.getEntreprise());
        fromScratch = false;
    }

    public void addIntroDate(LocalDateTime time) throws ComputerServiceException {
        isBuildStarted();
        builder.get().setDateIntroduction(time);
    }

    public void addEndDate(LocalDateTime time) throws ComputerServiceException {
        isBuildStarted();
        if (builder.get().getDateIntroduction() == null && time != null) {
            logger.error("Tentative de réglage de la date de fin alors que la date de début n'est pas réglée");
            throw new ComputerServiceException(
                    "Impossible de régler la date de fin si la date de début n'est pas réglée.");
        } else if (time != null && builder.get().getDateIntroduction().isAfter(time)) {
            logger.error("Date de fin avant la date de début");
            throw new ComputerServiceException("La date de fin est est avant la date de début.");
        }
        builder.get().setDateDiscontinuation(time);
    }

    public void addCompany(String companyName) throws ComputerServiceException {
        isBuildStarted();
        if (companyName != null) {
            Optional<Company> comp = new CompanyService(new CompanyDAO()).getCompanyByName(companyName);
            if (comp.isEmpty()) {
                logger.error("Nom de l'entreprise {} inconnu", companyName);
                throw new ComputerServiceException("Le nom de l'entreprise est inconnu.");
            } else {
                builder.get().setEntreprise(comp.get());
            }
        }
    }

    private void isBuildStarted() throws ComputerServiceException {
        if (builder.isEmpty()) {
            logger.error("Tentative de modification d'un builder sans l'avoir initialisé avec un buildComputer");
            throw new ComputerServiceException("Aucun buildComputer n'a été fait.");
        }
    }

    public void addComputerToDB() throws ComputerServiceException {
        isBuildStarted();
        if (!fromScratch) {
            logger.error("Tentative d'ajout d'un nouvel ordinateur à la DB en partant d'un ordinateur existant");
            throw new ComputerServiceException(
                    "Impossible d'ajouter un nouvel ordinateur à la base de données en partant d'un autre ordinateur");
        }
        dao.addComputer(builder.get().build());
        builder = Optional.empty();
    }

    public void updateComputerToDB() throws ComputerServiceException {
        isBuildStarted();
        if (fromScratch) {
            logger.error("Tentative de mise à jour d'un ordinateur à la DB en partant d'un nouvel ordinateur");
            throw new ComputerServiceException(
                    "Impossible de mettre à jour un ordinateur dans la base de données en en crééant un nouveau");
        }
        dao.updateComputer(builder.get().build());
        builder = Optional.empty();
    }

    public void deleteComputer(long computerId) throws ComputerServiceException {
        if (dao.getComputerById(computerId).isEmpty()) {
            logger.error("Tentative de suppression d'un ordinateur qui n'existe pas. Id = {}", computerId);
            throw new ComputerServiceException("L'ordinateur ne peut être supprimé car il n'existe pas");
        }
        dao.deleteComputer(computerId);
    }

    public List<Computer> getComputerList(long startIndex, long limit) throws ComputerServiceException {
        if (startIndex < 0 || startIndex > dao.getMaxId()) {
            logger.error("Index de départ invalide. Index = {}", startIndex);
            throw new ComputerServiceException("L'index de départ est invalide");
        }
        if (limit < 1) {
            logger.error("Limite inférieure à 0. Limite = {}", limit);
            throw new ComputerServiceException("Le nombre maximum de résultats doit être supérieur à 0");
        }
        return dao.getComputerList(startIndex, limit);
    }

    public Optional<Computer> getComputerById(long id) {
        return dao.getComputerById(id);
    }

    public Optional<Computer> getComputerByName(String name) {
        return dao.getComputerByName(name);
    }    
    
    public List<Computer> searchComputersByName(String name) {
        return dao.searchComputersByName(name);
    }

}
