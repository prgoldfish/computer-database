package com.excilys.cdb.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.exception.ComputerServiceException;
import com.excilys.cdb.exception.PageException;
import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.service.ComputerService;
import com.excilys.cdb.service.Page;

public class CLIController {

	/**
	 * Demande des informations à l'utilisateur pour mettre à jour un ordinateur déjà existant
	 */
	private static void updateComputer()
	{
		System.out.println("Modification d'un ordinateur\n");
		Computer toUpdate = CLI.askComputer();
		ComputerService comService = new ComputerService();
		try {
			comService.buildComputerFromComputer(toUpdate);
			setDateComputer(comService);
			setCompanyComputer(comService);
			comService.updateComputerToDB();
		} catch (ComputerServiceException e) {
			CLI.printSingleError(e.getMessage());
		}
		System.out.println("Ordinateur mis à jour");		
	}
	
	private static void setDateComputer(ComputerService service) throws ComputerServiceException {
		Optional<LocalDateTime> dateDebut = CLI.askDate(true, null);
		if(dateDebut.isPresent())
		{
			service.addIntroDate(dateDebut.get());
		}
		if(service.canAddEndDate())
		{
			Optional<LocalDateTime> dateFin = CLI.askDate(false, dateDebut.get());
			if(dateFin.isPresent())
			{
				service.addEndDate(dateFin.get());
			}
		}
	}

	private static void setCompanyComputer(ComputerService service) {
		boolean loop = true;
		while(loop)
		{
			Optional<String> entreprise = CLI.optionalActionYesNo("Voulez-vous mettre un fabricant ?", "Veuillez entrer le nom du fabricant : ");
			if(entreprise.isPresent())
			{
				try {
					service.addCompany(entreprise.get());
					loop = false;
				} catch (ComputerServiceException e) {
					CLI.printSingleError(e.getMessage());
				}
			}
			else
			{
				loop = false;
			}
		}
		
	}
	
	
	/**
	 * Demande des informations à l'utilisateur et crée un nouvel ordinateur puis l'ajoute dans la base de données
	 */
	private static void createNewComputer()
	{
		CLI.printString("Création d'un nouvel ordinateur\n");
		String computerName = CLI.askComputerName();
		ComputerService comService = new ComputerService();
		try {
			comService.buildComputerFromScratch(computerName);
			setDateComputer(comService);
			setCompanyComputer(comService);
			comService.addComputerToDB();
		} catch (ComputerServiceException e) {
			CLI.printSingleError(e.getMessage());
		}
		CLI.printString("Ordinateur ajouté");
		
	}
	
	
	/**
	 * Affiche la liste des entreprises sous la forme d'un tableau
	 */
	private static void showCompaniesList()
	{
		List<Company> compList = CompanyDAO.getCompaniesList();
		StringBuilder outString = new StringBuilder("| Id\t| ");
		outString.append(String.format("%1$-70s", "Nom"));
		outString.append("|\n");
		for(Company c : compList)
		{
			outString.append("| ").append(c.getId());
			outString.append("\t| ").append(String.format("%1$-70s", c.getNom()));
			outString.append("|\n");
		}
		CLI.printString(outString.toString());
	}
	
	/**
	 * Affiche la liste des ordinateurs avec un système de pagination
	 */
	private static void showComputerList()
	{
		Page page = new Page(20);
		try {
			CLI.printString(page.getPageContent());
			CLI.pageCommand(page);
		} catch (ComputerServiceException | PageException e) {
			CLI.printSingleError(e.getMessage());
		}
	}
	
	/**
	 * Affiche le menu principal, récupère l'entrée de l'utilisateur et fait l'action correspondante
	 */
	public static void menu()
	{
		while(true)
		{
			CLI.printMenu();
			switch (CLI.getIntBetween(1,  7)) {
			case 1:
				showComputerList();
				break;

			case 2:
				showCompaniesList();
				break;
				
			case 3:
				createNewComputer();
				break;

			case 4:
				updateComputer();
				break;

			case 5:
				//TODO deleteComputer();
				break;

			case 6:
				//TODO showComputerDetails();
				break;

			case 7:
				return;

			default:
				System.err.println("Endroit inatteignable");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		menu();

	}

}
