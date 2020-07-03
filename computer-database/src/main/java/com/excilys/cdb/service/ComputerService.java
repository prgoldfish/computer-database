package com.excilys.cdb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.persistence.OrderByColumn;

@Service
public class ComputerService {

    private Optional<ComputerBuilder> builder;
    private boolean fromScratch;
    private static final Logger logger = LoggerFactory.getLogger(ComputerService.class);

    @Autowired
    private ComputerDAO dao;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EntityManager em;

    private ComputerService() {
        builder = Optional.empty();
        fromScratch = false;
    }

    public boolean canAddEndDate() {
        return builder != null && builder.isPresent() && builder.get().getIntroduced() != null;
    }

    public Optional<LocalDateTime> getBeginDate() {
        return builder.map((build) -> build.getIntroduced());
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
        System.out.println("Nom 2 : " + com.getName());
        builder = Optional.of(new ComputerBuilder(com.getId(), com.getName()));
        builder.get().setIntroduced(com.getIntroduced());
        builder.get().setDiscontinued(com.getDiscontinued());
        builder.get().setCompany(com.getCompany());
        fromScratch = false;
    }

    public void addNewComputer(Computer com) throws ComputerServiceException {
        if (com == null) {
            logger.error("Aucun ordinateur en entrée");
            throw new ComputerServiceException("L'ordinateur en entrée est null");
        }
        buildNewComputer(com.getName());
        addIntroDate(com.getIntroduced());
        addEndDate(com.getDiscontinued());
        if (com.getCompany() != null) {
            addCompany(com.getCompany().getName());
        }
        addComputerToDB();
    }

    public void updateComputer(Computer com) throws ComputerServiceException {
        buildComputerForUpdate(com);
        updateComputerToDB();
    }

    public void addIntroDate(LocalDateTime time) throws ComputerServiceException {
        isBuildStarted();
        builder.get().setIntroduced(time);
    }

    public void addEndDate(LocalDateTime time) throws ComputerServiceException {
        isBuildStarted();
        if (builder.get().getIntroduced() == null && time != null) {
            logger.error("Tentative de réglage de la date de fin alors que la date de début n'est pas réglée");
            throw new ComputerServiceException(
                    "Impossible de régler la date de fin si la date de début n'est pas réglée.");
        } else if (time != null && builder.get().getIntroduced().isAfter(time)) {
            logger.error("Date de fin avant la date de début");
            throw new ComputerServiceException("La date de fin est est avant la date de début.");
        }
        builder.get().setDiscontinued(time);
    }

    public void addCompany(String companyName) throws ComputerServiceException {
        isBuildStarted();
        if (companyName != null) {
            Optional<Company> comp = companyService.getCompanyByName(companyName);
            if (comp.isEmpty()) {
                logger.error("Nom de l'entreprise {} inconnu", companyName);
                throw new ComputerServiceException("Le nom de l'entreprise est inconnu.");
            } else {
                builder.get().setCompany(comp.get());
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
        EntityTransaction t = em.getTransaction();
        t.begin();
        dao.deleteComputer(computerId, t);
        t.commit();
    }

    public List<Computer> getComputerList(long startIndex, long limit, OrderByColumn orderBy,
            boolean ascendentOrder) throws ComputerServiceException {
        if (startIndex < 0 || startIndex > dao.getMaxId()) {
            logger.error("Index de départ invalide. Index = {}", startIndex);
            throw new ComputerServiceException("L'index de départ est invalide");
        }
        if (limit < 1) {
            logger.error("Limite inférieure à 0. Limite = {}", limit);
            throw new ComputerServiceException("Le nombre maximum de résultats doit être supérieur à 0");
        }
        if (orderBy == null) {
            orderBy = OrderByColumn.COMPUTERID;
        }

        return dao.getComputerList(startIndex, limit, orderBy, ascendentOrder);
    }

    public Optional<Computer> getComputerById(long id) {
        return dao.getComputerById(id);
    }

    public Optional<Computer> getComputerByName(String name) {
        return dao.getComputerByName(name);
    }

    public List<Computer> searchComputersByName(String name, OrderByColumn orderBy, boolean ascendentOrder) {
        if (orderBy == null) {
            orderBy = OrderByColumn.COMPUTERID;
        }
        return dao.searchComputersByName(name, orderBy, ascendentOrder);
    }

    public long getMaxId() {
        return dao.getMaxId();
    }

}
