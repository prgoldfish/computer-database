package com.excilys.cdb.exception;

import java.util.Collections;
import java.util.List;

public class MapperException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8184793822494022525L;
    private List<String> errorList;

    /**
     * Constructeur par défaut.
     */
    public MapperException() {
        super("Problème dans ComputerMapper.");
        errorList = Collections.emptyList();
    }

    /**
     * Constructeur avec un message.
     * 
     * @param message Message de l'exception
     */
    public MapperException(String message) {
        super(message);
        errorList = Collections.emptyList();
    }

    /**
     * Constructeur avec un message.
     * 
     * @param message Message de l'exception
     */
    public MapperException(String message, List<String> errors) {
        super(message);
        errorList = errors;
    }

    public List<String> getErrorList() {
        return errorList;
    }
}
