package com.excilys.cdb.exception;

public class ComputerServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4522743736096575943L;

	public ComputerServiceException() {
		super("Problème dans ComputerService.");
	}
	
	public ComputerServiceException(String message)
	{
		super(message);
	}

}
