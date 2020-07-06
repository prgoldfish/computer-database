package com.excilys.cdb.exception;

public class ComputerServiceException extends Exception {
    private static final long serialVersionUID = 4522743736096575943L;

    /**
     * Constructeur par défaut.
     */
    public ComputerServiceException() {
        super("Problème dans ComputerService.");
    }

    /**
     * Constructeur avec un message.
     * 
     * @param message Message de l'exception
     */
    public ComputerServiceException(String message) {
        super(message);
    }
}
