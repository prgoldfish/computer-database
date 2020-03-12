package com.excilys.cdb.service;

import com.excilys.cdb.exception.InvalidArgException;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;
import com.excilys.cdb.persistence.ComputerDAO;

public class ComputerService {
	
	private ComputerBuilder builder;
	
	public ComputerService()
	{
		builder = null;
	}
	
	public void buildComputerFromScratch(String name) throws InvalidArgException
	{
		if(name == null || name.equals(""))
		{
			throw new InvalidArgException("Aucun nom n'est mis");
		}		
		int id = ComputerDAO.getMaxId() + 1;
		builder = new ComputerBuilder(id, name);
	}
	
	public void buildComputerFromComputer(Computer c) throws InvalidArgException
	{
		if(c == null)
		{
			throw new InvalidArgException("L'ordinateur en entrée est null");
		}
		builder = new ComputerBuilder(c.getId(), c.getNom());
	}
}
