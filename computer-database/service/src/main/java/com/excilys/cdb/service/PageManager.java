package com.excilys.cdb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.exception.PageException;

public class PageManager {

    private int length;
    private int currentPage;
    private int numOfElements;
    private static Logger logger = LoggerFactory.getLogger(PageManager.class);

    /**
     * Default Constructor with default value for page number (1)
     */
    public PageManager(int length) {
        this(1, length);
    }

    public PageManager(int pageNum, int length) {
        if (length < 1 || pageNum < 1) {
            throw new IllegalArgumentException("Length and pageNum must be >= 1");
        }
        this.length = length;
        this.currentPage = pageNum - 1;
        this.numOfElements = 1;
    }

    public int getOffset() {
        return currentPage * length;
    }

    public int getLength() {
        return length;
    }

    public <T> List<T> getSubList(List<T> list) {
        int page = Math.min(getMaxPage(list.size()), currentPage);
        return list.subList(page * length, Math.min(list.size(), (currentPage + 1) * length));
    }

    /**
     * Renvoie le nombre maximum de pages
     *
     * @return Le nombre max de pages
     */
    public int getMaxPage(int numberOfElements) {
        if (numberOfElements < 0) {
            throw new IllegalArgumentException("numberOfElements must be positive");
        }
        this.numOfElements = numberOfElements;
        return getMaxPage();
    }

    /**
     * Renvoie le nombre maximum de pages
     *
     * @return Le nombre max de pages
     */
    public int getMaxPage() {
        if (numOfElements == 0) {
            return 1;
        }
        return ((numOfElements - 1) / length) + 1;
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
     * Passe à la page suivante et renvoie son contenu
     *
     * @return Le contenu de la page suivante
     * @throws PageException
     */
    public int getNextPageOffset() {
        currentPage++;
        return getOffset();
    }

    /**
     * Passe à la page précédente et renvoie son contenu
     *
     * @return Le contenu de la page précédente
     * @throws PageException
     */
    public int getPreviousPageOffset() throws PageException {
        if (currentPage - 1 < 0) {
            logger.error("Trying to access previous page but it does not exist.");
            throw new PageException("There are no previous page");
        }
        currentPage--;
        return getOffset();
    }

    /**
     * Va à la page indiquée en paramètre
     *
     * @param pageNumber Le numéro de page voulu
     */
    public PageManager gotoPage(int pageNumber) {
        pageNumber--;
        if (pageNumber >= 0) {
            currentPage = pageNumber;
        } else {
            logger.error("Trying to access an invalid page. pageNumber = " + pageNumber);
            throw new IllegalArgumentException("pageNumber must be positive");
        }
        return this;
    }

}
