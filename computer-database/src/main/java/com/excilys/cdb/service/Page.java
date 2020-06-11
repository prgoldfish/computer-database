package com.excilys.cdb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.PageException;

public class Page<T> {

    private int pageLength;
    private int currentPage;
    private int maxPage;
    private List<T> list;
    //private static String header = setHeader();
    private static Logger logger = LoggerFactory.getLogger(Page.class);

    /**
     * Crée le header en haut de chaque page
     *
     * @return Une String repésentant le header
     */
    /*private static String setHeader() {
        StringBuilder head = new StringBuilder("| Id\t| ");
        head.append(String.format("%1$-70s", "Nom"));
        head.append(String.format("%1$-22s", "| Date d'introduction"));
        head.append(String.format("%1$-26s", "| Date de discontinuation"));
        head.append(String.format("%1$-47s", "| Entreprise"));
        head.append("|\n");
        return head.toString();
    }*/

    public Page(List<T> list, int pageLength) {
        this.list = list;
        this.pageLength = pageLength;
        this.currentPage = 0;
        this.maxPage = (list.size() - 1) / pageLength;
    }

    /**
     * Affiche chaque élément de la page actuelle sous forme de tableau
     *
     * @throws ComputerServiceException
     */
    /*public String getPageContent() throws ComputerServiceException {
        List<Computer> computerList = new ComputerService(new ComputerDAO()).getComputerList(currentPage * pageLength,
                pageLength);
        String outString = header;
        for (Computer c : computerList) {
            outString += computerDetailsString(c) + '\n';
        }
        return outString + "\nPage " + (currentPage + 1) + "/" + (maxPage + 1) + "\n";
    }*/
    public List<T> getPageContent() {
        return list.subList(currentPage * pageLength, Math.min(list.size(), (currentPage + 1) * pageLength));
    }

    /*private String computerDetailsString(Computer c) {
        StringBuilder outString = new StringBuilder();
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        String introString = intro == null ? "Indefini" : intro.toString();
        String discontString = discont == null ? "Indéfini" : discont.toString();
        String nomEntreprise = entreprise == null ? "Indéfini" : entreprise.getNom();
        outString.append("| ").append(c.getId());
        outString.append("\t| ").append(String.format("%1$-70s", c.getNom()));
        outString.append("| ").append(String.format("%1$-20s", introString));
        outString.append("| ").append(String.format("%1$-24s", discontString));
        outString.append("| ").append(String.format("%1$-45s", nomEntreprise));
        outString.append("|");
        return outString.toString();
    }*/

    /**
     * Renvoie le nombre maximum de pages
     *
     * @return Le nombre max de pages (en partant de 0)
     */
    public int getMaxPage() {
        return maxPage + 1;
    }

    public int getCurrentPage() {
        return currentPage + 1;
    }

    public int getElementCount() {
        return list.size();
    }

    /**
     * Passe à la page suivante et l'affiche
     *
     * @return
     * @throws ComputerServiceException
     * @throws PageException
     */
    public List<T> getNextPageContents() throws PageException {
        if (currentPage + 1 > maxPage) {
            logger.error("Tentative d'accès à la page suivante alors qu'elle n'existe pas.");
            throw new PageException("Il n'y a pas de page suivante");
        }
        currentPage++;
        return getPageContent();
    }

    /**
     * Passe à la page précédente et l'affiche
     *
     * @return
     * @throws ComputerServiceException
     * @throws PageException
     */
    public List<T> getPreviousPageContents() throws PageException {
        if (currentPage - 1 < 0) {
            logger.error("Tentative d'accès à la page précédente alors qu'elle n'existe pas.");
            throw new PageException("Il n'y a pas de page précédente");
        }
        currentPage--;
        return getPageContent();
    }

    /**
     * Va à la page indiquée en paramètre
     *
     * @param pageNumber Le numéro de page voulu
     */
    public void gotoPage(int pageNumber) {
        pageNumber--;
        if (pageNumber >= 0 && pageNumber <= maxPage) {
            currentPage = pageNumber;
        } else {
            logger.error("Tentative d'accès à une page inexistante.");
        }
    }

}
