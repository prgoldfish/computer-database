package com.excilys.cdb.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.PageException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.service.Page;

public class CLI {

    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static String header = setHeader();
    // private static final Logger logger = LoggerFactory.getLogger(CLI.class);

    /**
     * Demande un numéro de page à l'utilisateur
     *
     * @param p L'objet page contenant les informations de pagination
     */
    private static void askPage(Page<Computer> p) {
        System.out.println("Entrez un numéro de page entre 1 et " + p.getMaxPage());
        int pageNum = getIntBetween(1, p.getMaxPage());
        p.gotoPage(pageNum);
    }

    /**
     * Crée le header en haut de chaque page
     *
     * @return Une String repésentant le header
     */
    private static String setHeader() {
        StringBuilder head = new StringBuilder("| Id\t| ");
        head.append(String.format("%1$-70s", "Nom"));
        head.append(String.format("%1$-22s", "| Date d'introduction"));
        head.append(String.format("%1$-26s", "| Date de discontinuation"));
        head.append(String.format("%1$-47s", "| Entreprise"));
        head.append("|\n");
        return head.toString();
    }

    /**
     * Affiche chaque élément de la page actuelle sous forme de tableau
     *
     */
    public static void printPage(List<Computer> comList, int currentPage, int maxPage) {
        String outString = header;
        for (Computer c : comList) {
            outString += computerDetailsString(c) + '\n';
        }
        System.out.println(outString + "\nPage " + currentPage + "/" + maxPage + "\n");
    }

    private static String computerDetailsString(Computer c) {
        StringBuilder outString = new StringBuilder();
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        String introString = intro == null ? "Indefini" : intro.toString();
        String discontString = discont == null ? "Indéfini" : discont.toString();
        String nomEntreprise = entreprise == null ? "Indéfini" : entreprise.getName();
        outString.append("| ").append(c.getId());
        outString.append("\t| ").append(String.format("%1$-70s", c.getNom()));
        outString.append("| ").append(String.format("%1$-20s", introString));
        outString.append("| ").append(String.format("%1$-24s", discontString));
        outString.append("| ").append(String.format("%1$-45s", nomEntreprise));
        outString.append("|");
        return outString.toString();
    }

    protected static void printErrors(List<String> errors) {
        for (String error : errors) {
            System.err.println(error);
        }
    }

    protected static void printSingleError(String error) {
        System.err.println(error);
    }

    protected static void printString(String str) {
        System.out.println(str);
    }

    protected static String askComputerName() {
        System.out.println("Entrez le nom du nouvel ordinateur : ");
        String computerName = sc.nextLine();
        return computerName;
    }

    protected static void pageCommand(Page<Computer> page) throws ComputerServiceException {
        boolean quit = false;
        while (!quit) {
            System.out.println(
                    "Entrez \"prec\" pour voir la page précédente, \"suiv\" pour la page suivante, \"page\" pour aller à une page et \"menu\" pour retourner au menu principal.");
            try {
                quit = pageCommandSwitch(page, quit);
            } catch (PageException pae) {
                printSingleError(pae.getMessage());
            }
        }
    }

    private static boolean pageCommandSwitch(Page<Computer> page, boolean quit) throws ComputerServiceException, PageException {
        switch (sc.nextLine()) {
        case "prec":
            printPage(page.getPreviousPageContents(), page.getCurrentPage(), page.getMaxPage());
            break;

        case "suiv":
            printPage(page.getNextPageContents(), page.getCurrentPage(), page.getMaxPage());
            break;

        case "page":
            askPage(page);
            printPage(page.getPageContent(), page.getCurrentPage(), page.getMaxPage());
            break;

        case "menu":
            quit = true;
            break;

        default:
            System.out.println("Entrée invalide");
            break;
        }
        return quit;
    }

    /**
     * Pose une question à l'utilisateur et lui demande une entrée si oui
     *
     * @param question    La question à poser à l'utilisateur
     * @param actionIfYes Demande l'entrée
     * @return Un Optional contenant l'entrée de l'utilisateur s'il y en a une
     */
    protected static Optional<String> optionalActionYesNo(String question, String actionIfYes) {
        boolean ok = false;
        Optional<String> res = Optional.empty();
        while (!ok) {
            System.out.println(question + "(Y/N)");
            switch (sc.nextLine().toLowerCase()) {
            case "y":
            case "yes":
            case "o":
            case "oui":
                System.out.println(actionIfYes);
                res = Optional.of(sc.nextLine());
                ok = true;
                break;

            case "n":
            case "no":
            case "non":
                res = Optional.empty();
                ok = true;
                break;

            default:
                System.out.println("Entrée invalide. Veuillez réessayer");

            }
        }
        return res;
    }

    /**
     * Demande si l'utilisateur veut entrer une date puis demande une date à
     * l'utilisateur sous la forme DD/MM/YYYY
     *
     * @param debut   Indique si c'est la date de début ou de fin. Ca change
     *                seulement l'affichage
     * @param minDate La date entrée doit être supérieure à cette date
     * @return Un objet LocalDateTime correspondant à ce que l'utiliateur a entré
     */
    protected static Optional<LocalDateTime> askDate(boolean debut, LocalDateTime minDate) {
        String askString1 = debut ? "Voulez-vous mettre une date d'introduction ?"
                : "Voulez-vous mettre une date de fin ?";
        String askString2 = debut ? "Veuillez entrer la date d'introduction (DD/MM/YYYY) : "
                : "Veuillez entrer la date de fin (DD/MM/YYYY) : ";
        while (true) {
            Optional<String> date = optionalActionYesNo(askString1, askString2);
            if (date.isEmpty()) {
                return Optional.empty();
            } else {
                try {
                    LocalDateTime d = LocalDate.parse(date.get(), dateFormatter).atStartOfDay();
                    if (minDate == null || minDate.isBefore(d)) {
                        return Optional.of(d);
                    } else {
                        System.out.println("La date de fin est avant la date de début");
                    }
                } catch (DateTimeParseException dtpe) {
                    printSingleError("Date invalide.");
                }
            }
        }
    }

    public static int getInt() {
        return Integer.parseInt(sc.nextLine());
    }

    /**
     * Demande un entier à l'utilisateur entre min et max
     *
     * @param min Le minimum acceptable
     * @param max le maximum acceptable
     * @return Un entier entre min et max
     */
    protected static int getIntBetween(int min, int max) {
        while (true) {
            System.out.println("Veuillez entrer un nombre entre " + min + " et " + max);
            try {
                int in = getInt();
                if (in >= min && in <= max) {
                    return in;
                } else {
                    System.out.println("L'entrée doit être comprise entre " + min + " et " + max);
                }
            } catch (NumberFormatException nfe) {
                printSingleError("Entrée invalide.");
            }
        }
    }

    /**
     * Affiche le menu principal
     */
    protected static void printMenu() {
        System.out.println("\n\n\nComputer Database");
        System.out.println("1 - Afficher la liste des ordinateurs");
        System.out.println("2 - Afficher la liste des entreprises");
        System.out.println("3 - Créer un nouvel ordinateur");
        System.out.println("4 - Modifier un ordinateur");
        System.out.println("5 - Supprimer un ordinateur");
        System.out.println("6 - Afficher les détails d'un ordinateur");
        System.out.println("7 - Supprimer une entreprise");
        System.out.println("8 - Quitter\n");
    }
}
