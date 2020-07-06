package com.excilys.cdb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exception.PageException;

public class Page<T> {

    private int pageLength;
    private int currentPage;
    private int maxPage;
    private List<T> list;
    private static Logger logger = LoggerFactory.getLogger(Page.class);

    public Page(List<T> list, int pageLength) {
        this.list = list;
        this.pageLength = pageLength;
        this.currentPage = 0;
        this.maxPage = (list.size() - 1) / pageLength;
    }

    /**
     * Renvoie le contenu de la page actuelle
     *
     * @return Une liste des éléments de la page
     */
    public List<T> getPageContent() {
        return list.subList(currentPage * pageLength, Math.min(list.size(), (currentPage + 1) * pageLength));
    }

    /**
     * Renvoie le nombre maximum de pages
     *
     * @return Le nombre max de pages
     */
    public int getMaxPage() {
        return maxPage + 1;
    }

    /**
     * Renvoie la page actuelle
     *
     * @return Le numéro de page actuel
     */
    public int getCurrentPage() {
        return currentPage + 1;
    }

    /**
     * Renvoie le nombre d'éléments total de la liste
     *
     * @return le nombre d'éléments
     */
    public int getElementCount() {
        return list.size();
    }

    /**
     * Passe à la page suivante et renvoie son contenu
     *
     * @return Le contenu de la page suivante
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
     * Passe à la page précédente et renvoie son contenu
     *
     * @return Le contenu de la page précédente
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
