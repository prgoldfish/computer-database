package com.excilys.cdb.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.PageException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.OrderByColumn;
import com.excilys.cdb.service.CompanyService;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.PageManager;
import com.excilys.cdb.springconfig.ConsoleConfig;

@Controller
public class CLIController {

    @Autowired
    private ComputerService computerService;
    @Autowired
    private CompanyService companyService;

    /**
     * Demande un identifiant à l'utilisateur et renvoie l'ordianteur associé
     *
     * @return L'ordinateur avec l'identifiant rentré
     */
    private Computer askComputer() {
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

    private long askCompany() {
        while (true) {
            CLI.printString("Entrez l'identifiant de entreprise : ");
            try {
                int cId = CLI.getInt();
                Optional<Company> c = companyService.getCompanyById(cId);
                if (c.isPresent()) {
                    return cId;
                } else {
                    CLI.printSingleError("L'identifiant n'existe pas");
                }
            } catch (NumberFormatException nfe) {
                CLI.printSingleError("Entrée invalide");
            }
        }
    }

    private void showComputerDetails() {
        Computer c = askComputer();
        CLI.printString("Details de l'ordinateur : ");
        CLI.printString(c.toString());
    }

    /**
     * Supprime un ordinateur de la base de données
     */
    private void deleteComputer() {
        CLI.printString("Suppression d'un ordinateur\n");
        Computer toDelete = askComputer();
        try {
            computerService.deleteComputer(toDelete.getId());
            CLI.printString("Ordinateur supprimé");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }

    }

    /**
     * Demande des informations à l'utilisateur pour mettre à jour un ordinateur
     * déjà existant
     */
    private void updateComputer() {
        CLI.printString("Modification d'un ordinateur\n");
        Computer toUpdate = askComputer();
        try {
            computerService.buildComputerForUpdate(toUpdate);
            setDateComputer();
            setCompanyComputer();
            computerService.updateComputerToDB();
            CLI.printString("Ordinateur mis à jour");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }
    }

    private void setDateComputer() throws ComputerServiceException {
        Optional<LocalDateTime> dateDebut = CLI.askDate(true, null);
        if (dateDebut.isPresent()) {
            computerService.addIntroDate(dateDebut.get());
        }
        if (computerService.getBeginDate().isPresent()) {
            Optional<LocalDateTime> dateFin = CLI.askDate(false, computerService.getBeginDate().get());
            if (dateFin.isPresent()) {
                computerService.addEndDate(dateFin.get());
            }
        }
    }

    private void setCompanyComputer() {
        boolean loop = true;
        while (loop) {
            Optional<String> entreprise = CLI.optionalActionYesNo("Voulez-vous mettre un fabricant ?",
                    "Veuillez entrer le nom du fabricant : ");
            if (entreprise.isPresent()) {
                try {
                    computerService.addCompany(entreprise.get());
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
    private void createNewComputer() {
        CLI.printString("Création d'un nouvel ordinateur\n");
        String computerName = CLI.askComputerName();
        try {
            computerService.buildNewComputer(computerName);
            setDateComputer();
            setCompanyComputer();
            computerService.addComputerToDB();
            CLI.printString("Ordinateur ajouté");
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }

    }

    /**
     * Affiche la liste des entreprises sous la forme d'un tableau
     */
    private void showCompaniesList() {
        List<Company> compList = companyService.getCompaniesList();
        StringBuilder outString = new StringBuilder("| Id\t| ");
        outString.append(String.format("%1$-70s", "Nom"));
        outString.append("|\n");
        for (Company c : compList) {
            outString.append("| ").append(c.getId());
            outString.append("\t| ").append(String.format("%1$-70s", c.getName()));
            outString.append("|\n");
        }
        CLI.printString(outString.toString());
    }

    /**
     * Affiche la liste des ordinateurs avec un système de pagination
     */
    private void showComputerList() {
        PageManager page = new PageManager(20);
        try {
            List<Computer> comList = computerService.getComputerList(page.getOffset(), page.getLength(),
                    OrderByColumn.COMPUTERID, true);
            CLI.printPage(comList, page.getCurrentPage(),
                    page.getMaxPage(computerService.getNumOfElementsForLastRequest()));
            pageLoop(page);
        } catch (ComputerServiceException cse) {
            CLI.printSingleError(cse.getMessage());
        }
    }

    private void pageLoop(PageManager page) throws ComputerServiceException {
        while (true) {
            try {
                String input = CLI.askCommand(page);
                int offset = 0;
                switch (input) {
                case "page":
                    offset = page.getOffset();
                    break;
                case "prec":
                    offset = page.getPreviousPageOffset();
                    break;
                case "suiv":
                    offset = page.getNextPageOffset();
                    break;

                case "menu":
                default:
                    return;
                }
                List<Computer> comList = computerService.getComputerList(offset, page.getLength(),
                        OrderByColumn.COMPUTERID, true);
                CLI.printPage(comList, page.getCurrentPage(),
                        page.getMaxPage(computerService.getNumOfElementsForLastRequest()));
            } catch (PageException pae) {
                CLI.printSingleError(pae.getMessage());
            }
        }
    }

    private void deleteCompany() {
        CLI.printString("Suppression d'une entreprise.");
        long companyId = askCompany();
        companyService.deleteCompany(companyId);

    }

    /**
     * Affiche le menu principal, récupère l'entrée de l'utilisateur et fait
     * l'action correspondante
     */
    public void menu() {
        while (true) {
            CLI.printMenu();
            switch (CLI.getIntBetween(1, 8)) {
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
                deleteCompany();
                break;

            case 8:
                return;

            default:
                CLI.printSingleError("Endroit inatteignable");
                break;
            }
        }
    }

    public static void main(String[] args) {
        ConsoleConfig.getContext().getBean(CLIController.class).menu();

    }

}
