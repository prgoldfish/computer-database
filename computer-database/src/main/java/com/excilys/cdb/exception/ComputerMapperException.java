package com.excilys.cdb.exception;

public class ComputerMapperException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8184793822494022525L;

    /**
     * Constructeur par défaut.
     */
    public ComputerMapperException() {
        super("Problème dans ComputerMapper.");
    }

    /**
     * Constructeur avec un message.
     * 
     * @param message Message de l'exception
     */
    public ComputerMapperException(String message) {
        super(message);
    }
}
