package com.excilys.cdb.exception;

public class PageException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4522743736096575943L;

    public PageException() {
        super("Problème dans Page.");
    }

    public PageException(String message) {
        super(message);
    }

}
