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

public class CLI {

	private final static Scanner sc = new Scanner(System.in);
	private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	/**
	 * Affiche la liste des ordinateurs sous forme de tableau
	 */
	private static void showComputerList()
	{
		List<Computer> compList = ComputerDAO.getComputerList();
		StringBuilder outString = new StringBuilder("| Id\t| ");
		outString.append(String.format("%1$-70s", "Nom"));
		outString.append(String.format("%1$-22s", "| Date d'introduction"));
		outString.append(String.format("%1$-26s", "| Date de discontinuation"));
		outString.append(String.format("%1$-47s", "| Entreprise"));
		outString.append("|\n");
		for(Computer c : compList)
		{
			LocalDateTime intro = c.getDateIntroduction();
			LocalDateTime discont = c.getDateDiscontinuation();
			Company entreprise = c.getEntreprise();
			String introString = intro == null ? "Indefini" : intro.toString();
			String discontString = discont == null ? "Indéfini" : discont.toString();
			String nomEntreprise = entreprise == null ? "Indéfini" : entreprise.getNom();
			outString.append("| ").append(c.getId());
			outString.append("\t| ").append(String.format("%1$-70s", c.getNom()));
			outString.append("| ").append(String.format("%1$-20s", introString));
			outString.append("| ").append(String.format("%1$-24s", discontString));
			outString.append("| ").append(String.format("%1$-45s", nomEntreprise));
			outString.append("|\n");
		}
		System.out.println(outString);
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
		Optional<Company> entreprise = askCompany();
		if(entreprise.isPresent())
		{
			builder.setEntreprise(entreprise.get());
		}
		ComputerDAO.addComputer(builder.build());
		System.out.println("Ordinateur ajouté");
		
	}

	/**
	 * Demande des informations à l'utilisateur pour mettre à jour un ordinateur déjà existant
	 */
	private static void updateComputer()
	{
		System.out.println("Modification d'un ordinateur\n");
		Computer toUpdate = askComputer();
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
		Optional<Company> entreprise = askCompany();
		if(entreprise.isPresent())
		{
			toUpdate.setEntreprise(entreprise.get());
		}
		ComputerDAO.updateComputer(toUpdate);
		System.out.println("Ordinateur mis à jour");		
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

			case 7:
				System.exit(0);

			default:
				System.err.println("Endroit inatteignable");
				break;
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		//showComputerList();
		//System.out.println(LocalDate.parse("23/09/1996", dateFormatter).atStartOfDay());
		//showCompaniesList();
		//System.out.println(optionalActionYesNo("Voulez-vous écrire quelque chose ?", "Ecrivez quelque chose"));
		//createNewComputer();
		//updateComputer();
		//deleteComputer();
		menu();
	}

}
