package com.excilys.cdb.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.Page;

public class CLIController {

    /**
     * Demande un identifiant à l'utilisateur et renvoie l'ordianteur associé
     * 
     * @return L'ordinateur avec l'identifiant rentré
     */
    protected static Computer askComputer() {
        ComputerService computerService = new ComputerService(new ComputerDAO());
        while (true) {
            CLI.printString("Entrez l'identifiant de l'ordinateur : ");
            try {
                int cId = CLI.getInt();
                Optional<Computer> c = computerService.getComputerById(cId);
                if (c.isPresent()) {
                    return c.get();
                } else {
                    CLI.printSingleError("L'identifiant n'existe pas");
                }
            } catch (NumberFormatException nfe) {
                CLI.printSingleError("Entrée invalide");
            }
        }
    }

    private static void showComputerDetails() {
        Computer c = askComputer();
        CLI.printString("Details de l'ordinateur : ");
        CLI.printString(c.toString());
    }

    /**
     * Supprime un ordinateur de la base de données
     */
    private static void deleteComputer() {
        CLI.printString("Suppression d'un ordinateur\n");
        Computer toDelete = askComputer();
        ComputerService comService = new ComputerService(new ComputerDAO());
        try {
            comService.deleteComputer(toDelete.getId());
            CLI.printString("Ordinateur supprimé");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }

    }

    /**
     * Demande des informations à l'utilisateur pour mettre à jour un ordinateur
     * déjà existant
     */
    private static void updateComputer() {
        CLI.printString("Modification d'un ordinateur\n");
        Computer toUpdate = askComputer();
        ComputerService comService = new ComputerService(new ComputerDAO());
        try {
            comService.buildComputerForUpdate(toUpdate);
            setDateComputer(comService);
            setCompanyComputer(comService);
            comService.updateComputerToDB();
            CLI.printString("Ordinateur mis à jour");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }
    }

    private static void setDateComputer(ComputerService service) throws ComputerServiceException {
        Optional<LocalDateTime> dateDebut = CLI.askDate(true, null);
        if (dateDebut.isPresent()) {
            service.addIntroDate(dateDebut.get());
        }
        if (service.getBeginDate().isPresent()) {
            Optional<LocalDateTime> dateFin = CLI.askDate(false, service.getBeginDate().get());
            if (dateFin.isPresent()) {
                service.addEndDate(dateFin.get());
            }
        }
    }

    private static void setCompanyComputer(ComputerService service) {
        boolean loop = true;
        while (loop) {
            Optional<String> entreprise = CLI.optionalActionYesNo("Voulez-vous mettre un fabricant ?",
                    "Veuillez entrer le nom du fabricant : ");
            if (entreprise.isPresent()) {
                try {
                    service.addCompany(entreprise.get());
                    loop = false;
                } catch (ComputerServiceException cse) {
                    CLI.printSingleError(cse.getMessage());
                }
            } else {
                loop = false;
            }
        }

    }

    /**
     * Demande des informations à l'utilisateur et crée un nouvel ordinateur puis
     * l'ajoute dans la base de données
     */
    private static void createNewComputer() {
        CLI.printString("Création d'un nouvel ordinateur\n");
        String computerName = CLI.askComputerName();
        ComputerService comService = new ComputerService(new ComputerDAO());
        try {
            comService.buildNewComputer(computerName);
            setDateComputer(comService);
            setCompanyComputer(comService);
            comService.addComputerToDB();
            CLI.printString("Ordinateur ajouté");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }

    }

    /**
     * Affiche la liste des entreprises sous la forme d'un tableau
     */
    private static void showCompaniesList() {
        CompanyService comService = new CompanyService(new CompanyDAO());
        List<Company> compList = comService.getCompaniesList();
        StringBuilder outString = new StringBuilder("| Id\t| ");
        outString.append(String.format("%1$-70s", "Nom"));
        outString.append("|\n");
        for (Company c : compList) {
            outString.append("| ").append(c.getId());
            outString.append("\t| ").append(String.format("%1$-70s", c.getNom()));
            outString.append("|\n");
        }
        CLI.printString(outString.toString());
    }

    /**
     * Affiche la liste des ordinateurs avec un système de pagination
     */
    private static void showComputerList() {
        Page page = new Page(20);
        try {
            CLI.printString(page.getPageContent());
            CLI.pageCommand(page);
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }
    }

    /**
     * Affiche le menu principal, récupère l'entrée de l'utilisateur et fait
     * l'action correspondante
     */
    public static void menu() {
        while (true) {
            CLI.printMenu();
            switch (CLI.getIntBetween(1, 7)) {
            case 1:
                showComputerList();
                break;

            case 2:
                showCompaniesList();
                break;

            case 3:
                createNewComputer();
                break;

            case 4:
                updateComputer();
                break;

            case 5:
                deleteComputer();
                break;

            case 6:
                showComputerDetails();
                break;

            case 7:
                return;

            default:
                CLI.printSingleError("Endroit inatteignable");
                break;
            }
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(CLIController.class);
        logger.info("Hello World");
        logger.info("HELLO HELLO");
        menu();

    }

}
