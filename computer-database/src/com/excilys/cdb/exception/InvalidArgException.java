package com.excilys.cdb.exception;

public class InvalidArgException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4522743736096575943L;

	public InvalidArgException() {
		super("L'argument est invalide");
	}
	
	public InvalidArgException(String message)
	{
		super(message);
	}

}
