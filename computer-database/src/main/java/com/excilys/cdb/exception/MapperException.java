package com.excilys.cdb.exception;

public class MapperException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8184793822494022525L;

    /**
     * Constructeur par défaut.
     */
    public MapperException() {
        super("Problème dans ComputerMapper.");
    }

    /**
     * Constructeur avec un message.
     * 
     * @param message Message de l'exception
     */
    public MapperException(String message) {
        super(message);
    }
}
