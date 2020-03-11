package com.excilys.cdb.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;
import com.excilys.cdb.service.Page;

public class CLI {

	private static final Scanner sc = new Scanner(System.in);
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	/**
	 * Demande un numéro de page à l'utilisateur
	 * @param p L'objet page contenant les informations de pagination
	 */
	private static void askPage(Page p) {
		System.out.println("Entrez un numéro de page entre 1 et " + (p.getMaxPage() + 1));
		int pageNum = getIntBetween(1, p.getMaxPage() + 1);
		p.gotoPage(pageNum - 1);
	}
	
	/**
	 * Affiche la liste des ordinateurs avec un système de pagination
	 */
	private static void showComputerList()
	{
		Page page = new Page(20);
		page.printPage();
		boolean quit = false;
		while(!quit)
		{
			System.out.println("Entrez \"prec\" pour voir la page précédente, \"suiv\" pour la page suivante, \"page\" pour aller à une page et \"menu\" pour retourner au menu principal.");
			quit = pageCommandSwitch(page, quit);
		}
	}

	private static boolean pageCommandSwitch(Page page, boolean quit) {
		switch (sc.nextLine()) {
		case "prec":
			page.printPreviousPage();
			break;
			
		case "suiv":
			page.printNextPage();
			break;
			
		case "page":
			askPage(page);
			page.printPage();
			break;
			
		case "menu":
			quit = true;
			break;

		default:
			System.out.println("Entrée invalide");
			break;
		}
		return quit;
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
		System.out.println(outString);
	}
	
	/**
	 * Pose une question à l'utilisateur et lui demande une entrée si oui
	 * @param question La question à poser à l'utilisateur
	 * @param actionIfYes Demande l'entrée
	 * @return Un Optional contenant l'entrée de l'utilisateur s'il y en a une
	 */
	private static Optional<String> optionalActionYesNo(String question, String actionIfYes)
	{
		boolean ok= false;
		Optional<String> res = Optional.empty();
		while(!ok)
		{
			System.out.println(question + "(Y/N)");
			switch(sc.nextLine().toLowerCase())
			{
				case "y":
				case "yes":
				case "o":
				case "oui":
					System.out.println(actionIfYes);
					res = Optional.of(sc.nextLine());
					ok = true;
					break;
					
				case "n":
				case "no":
				case "non":
					res = Optional.empty();
					ok = true;
					break;
					
				default:
					System.out.println("Entrée invalide. Veuillez réessayer");
					
			}
		}
		return res;
	}
	/**
	 * Demande si l'utilisateur veut entrer une date puis demande une date à l'utilisateur sous la forme DD/MM/YYYY
	 * @param debut Indique si c'est la date de début ou de fin. Ca change seulement l'affichage
	 * @param minDate La date entrée doit être supérieure à cette date
	 * @return Un objet LocalDateTime correspondant à ce que l'utiliateur a entré
	 */
	private static Optional<LocalDateTime> askDate(boolean debut, LocalDateTime minDate)
	{
		String askString1 = debut ? "Voulez-vous mettre une date d'introduction ?" : "Voulez-vous mettre une date de fin ?";
		String askString2 = debut ? "Veuillez entrer la date d'introduction (DD/MM/YYYY) : " : "Veuillez entrer la date de fin (DD/MM/YYYY) : ";
		while(true)
		{
			Optional<String> date = optionalActionYesNo(askString1, askString2);
			if(date.isEmpty())
			{
				return Optional.empty();
			}
			else
			{
				try
				{
					LocalDateTime d = LocalDate.parse(date.get(), dateFormatter).atStartOfDay();
					if(minDate == null || minDate.isBefore(d))
					{
						return Optional.of(d);
					}
					else
					{
						System.out.println("La date de fin est avant la date de début");
					}
				} catch (DateTimeParseException e) {
					System.out.println("Date invalide.");
				}
			}
		}
	}
	
	/**
	 * Demande si l'utilisateur veut entrer le nom d'une entreprise puis demande le nom d'un fabricant et renvoie l'objet Company associé
	 * @return Un optional contenant un objet Company si présent
	 */
	private static Optional<Company> askCompany()
	{
		while(true)
		{
			Optional<String> entreprise = optionalActionYesNo("Voulez-vous mettre un fabricant ?", "Veuillez entrer le nom du fabricant : ");
			if(entreprise.isEmpty())
			{
				return Optional.empty();
			}
			else
			{
				Optional<Company> comp = CompanyDAO.getCompanyByName(entreprise.get());
				if(comp.isEmpty())
				{
					System.out.println("Ce nom est inconnu.");
				}
				else
				{
					return comp;
				}
			}
		}
	}
	
	/**
	 * Demande un identifiant à l'utilisateur et renvoie l'ordianteur associé
	 * @return L'ordinateur avec l'identifiant rentré
	 */
	private static Computer askComputer()
	{
		while(true)
		{
			System.out.println("Entrez l'identifiant de l'ordinateur : ");
			try
			{
				int cId = Integer.parseInt(sc.nextLine());
				Optional<Computer> c = ComputerDAO.getComputerById(cId);
				if(c.isPresent())
				{
					return c.get();
				}
				else
				{
					System.out.println("L'identifiant n'existe pas");
				}
			} catch (NumberFormatException e) {
				System.out.println("Entrée invalide");
			}
		}
	}
	
	private static void showComputerDetails()
	{
		Computer c = askComputer();
		System.out.println("Details de l'ordinateur : ");
		System.out.println(c);
	}
	
	/**
	 * Demande des informations à l'utilisateur et crée un nouvel ordinateur puis l'ajoute dans la base de données
	 */
	private static void createNewComputer()
	{
		System.out.println("Création d'un nouvel ordinateur\n");
		System.out.println("Entrez le nom du nouvel ordinateur : ");
		String computerName = sc.nextLine();
		ComputerBuilder builder = new Computer.ComputerBuilder(ComputerDAO.getMaxId() + 1, computerName);
		setDateComputer(builder);
		setCompanyComputer(builder);
		ComputerDAO.addComputer(builder.build());
		System.out.println("Ordinateur ajouté");
		
	}

	private static void setCompanyComputer(ComputerBuilder builder) {
		Optional<Company> entreprise = askCompany();
		if(entreprise.isPresent())
		{
			builder.setEntreprise(entreprise.get());
		}
	}

	private static void setDateComputer(ComputerBuilder builder) {
		Optional<LocalDateTime> dateDebut = askDate(true, null);
		if(dateDebut.isPresent())
		{
			builder.setDateIntroduction(dateDebut.get());
			Optional<LocalDateTime> dateFin = askDate(false, dateDebut.get());
			if(dateFin.isPresent())
			{
				builder.setDateDiscontinuation(dateFin.get());
			}
		}
	}

	/**
	 * Demande des informations à l'utilisateur pour mettre à jour un ordinateur déjà existant
	 */
	private static void updateComputer()
	{
		System.out.println("Modification d'un ordinateur\n");
		Computer toUpdate = askComputer();
		updateComputerDates(toUpdate);
		updateComputerCompany(toUpdate);
		ComputerDAO.updateComputer(toUpdate);
		System.out.println("Ordinateur mis à jour");		
	}

	private static void updateComputerCompany(Computer toUpdate) {
		Optional<Company> entreprise = askCompany();
		if(entreprise.isPresent())
		{
			toUpdate.setEntreprise(entreprise.get());
		}
	}

	private static void updateComputerDates(Computer toUpdate) {
		Optional<LocalDateTime> dateDebut = askDate(true, null);
		if(dateDebut.isPresent())
		{
			toUpdate.setDateIntroduction(dateDebut.get());
		}
		if(toUpdate.getDateIntroduction() != null)
		{
			Optional<LocalDateTime> dateFin = askDate(false, toUpdate.getDateIntroduction());
			if(dateFin.isPresent())
			{
				toUpdate.setDateDiscontinuation(dateFin.get());
			}
		}
	}
	
	/**
	 * Supprime un ordinateur de la base de données
	 */
	private static void deleteComputer()
	{
		System.out.println("Suppression d'un ordinateur\n");
		Computer toDelete = askComputer();
		ComputerDAO.deleteComputer(toDelete.getId());
		System.out.println("Ordinateur supprimé");
		
	}
	
	/**
	 * Demande un entier à l'utilisateur entre min et max
	 * @param min Le minimum acceptable
	 * @param max le maximum acceptable
	 * @return Un entier entre min et max
	 */
	private static int getIntBetween(int min, int max)
	{
		while(true)
		{
			System.out.println("Veuillez entrer un nombre entre " + min + " et " + max );
			try
			{
				int in = Integer.parseInt(sc.nextLine());
				if(in >= min && in <= max)
				{
					return in;
				}
				else
				{
					System.out.println("L'entrée doit être comprise entre " + min + " et " + max);
				}
			} catch (NumberFormatException e) {
				System.out.println("Entrée invalide.");
			}
		}
	}
	
	/**
	 * Affiche le menu principal
	 */
	private static void printMenu()
	{
		System.out.println("\n\n\nComputer Database");
		System.out.println("1 - Afficher la liste des ordinateurs");
		System.out.println("2 - Afficher la liste des entreprises");
		System.out.println("3 - Créer un nouvel ordinateur");
		System.out.println("4 - Modifier un ordinateur");
		System.out.println("5 - Supprimer un ordinateur");
		System.out.println("6 - Afficher les détails d'un ordinateur");
		System.out.println("7 - Quitter\n");
	}
	
	/**
	 * Affiche le menu principal, récupère l'entrée de l'utilisateur et fait l'action correspondante
	 */
	public static void menu()
	{
		while(true)
		{
			printMenu();
			switch (getIntBetween(1,  7)) {
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
				deleteComputer();
				break;

			case 6:
				showComputerDetails();
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
