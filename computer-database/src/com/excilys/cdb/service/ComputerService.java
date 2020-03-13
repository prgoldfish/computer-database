package com.excilys.cdb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;
import com.excilys.cdb.persistence.ComputerDAO;

public class ComputerService {
	
	private Optional<ComputerBuilder> builder;
	private boolean fromScratch;
	
	public ComputerService()
	{
		builder = Optional.empty();
		fromScratch = false;
	}
	
	public boolean canAddEndDate()
	{
		return builder != null && builder.isPresent() && builder.get().getDateIntroduction() != null;
	}
	
	public Optional<LocalDateTime> getBeginDate()
	{
		return builder.map((build) -> build.getDateIntroduction());
	}
	
	public void buildComputerFromScratch(String name) throws ComputerServiceException
	{
		if(name == null || name.equals(""))
		{
			throw new ComputerServiceException("Aucun nom n'est mis");
		}		
		long id = ComputerDAO.getMaxId() + 1;
		builder = Optional.of(new ComputerBuilder(id, name));
		fromScratch = true;
	}
	
	public void buildComputerFromComputer(Computer com) throws ComputerServiceException
	{
		if(com == null)
		{
			throw new ComputerServiceException("L'ordinateur en entrée est null");
		}
		builder = Optional.of(new ComputerBuilder(com.getId(), com.getNom()));
		builder.get().setDateIntroduction(com.getDateIntroduction());
		builder.get().setDateDiscontinuation(com.getDateDiscontinuation());
		builder.get().setEntreprise(com.getEntreprise());
		fromScratch = false;
	}
	
	public void addIntroDate(LocalDateTime time) throws ComputerServiceException
	{
		isBuildStarted();
		builder.get().setDateIntroduction(time);
	}
	
	public void addEndDate(LocalDateTime time) throws ComputerServiceException
	{
		isBuildStarted();
		if(builder.get().getDateIntroduction() == null)
		{
			throw new ComputerServiceException("Impossible de régler la date de fin si la date de début n'est pas réglée.");
		}
		else if(time != null && builder.get().getDateIntroduction().isAfter(time))
		{
			throw new ComputerServiceException("La date de fin est est avant la date de début.");
		}
		builder.get().setDateDiscontinuation(time);
	}
	
	public void addCompany(String companyName) throws ComputerServiceException
	{
		isBuildStarted();
		if(companyName != null)
		{
			Optional<Company> comp = new CompanyService().getCompanyByName(companyName);
			if(comp.isEmpty())
			{
				throw new ComputerServiceException("Le nom de l'entreprise est inconnu.");
			}
			else
			{
				builder.get().setEntreprise(comp.get());
			}
		}		
	}
	
	private void isBuildStarted() throws ComputerServiceException
	{
		if(builder.isEmpty())
		{
			throw new ComputerServiceException("Aucun buildComputer n'a été fait.");
		}
	}

	public void addComputerToDB() throws ComputerServiceException
	{
		isBuildStarted();
		if(!fromScratch)
		{
			throw new ComputerServiceException("Impossible d'ajouter un nouvel ordinateur à la base de données en partant d'un autre ordinateur");
		}
		ComputerDAO.addComputer(builder.get().build());
		builder = Optional.empty();
	}
	

	public void updateComputerToDB() throws ComputerServiceException
	{
		isBuildStarted();
		if(fromScratch)
		{
			throw new ComputerServiceException("Impossible de mettre à jour un ordinateur dans la base de données en en crééant un nouveau");
		}
		ComputerDAO.updateComputer(builder.get().build());
		builder = Optional.empty();
	}
	
	public void deleteComputer(long computerId) throws ComputerServiceException
	{
		if(ComputerDAO.getComputerById(computerId).isEmpty())
		{
			throw new ComputerServiceException("L'ordinateur ne peut ere supprimé car il n'existe pas");
		}
		ComputerDAO.deleteComputer(computerId);
	}
	
	public List<Computer> getComputerList(long startIndex, long limit) throws ComputerServiceException
	{
		if(startIndex < 0 || startIndex > ComputerDAO.getMaxId())
		{
			throw new ComputerServiceException("L'index de départ est invalide");
		}
		if(limit < 1)
		{
			throw new ComputerServiceException("Le nombre maximum de résultats doit être supérieur à 0");
		}
		return ComputerDAO.getComputerList(startIndex, limit);
	}
	
	public Optional<Computer> getComputerById(long id)
	{
		return ComputerDAO.getComputerById(id);
	}
	
	public Optional<Computer> getComputerByName(String name)
	{
		return ComputerDAO.getComputerByName(name);
	}
	
	
	
}
