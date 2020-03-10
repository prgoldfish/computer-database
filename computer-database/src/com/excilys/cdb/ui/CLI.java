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

	public final static Scanner sc = new Scanner(System.in);
	public final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public static void showComputerList()
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
	
	public static void showCompaniesList()
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
	
	public static Optional<String> optionalActionYesNo(String question, String actionIfYes)
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
	
	private static Computer askComputer()
	{
		while(true)
		{
			System.out.println("Entrez l'identifiant de l'ordinateur à modifier : ");
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
	
	public static void createNewComputer()
	{
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
	
	public static void updateComputer()
	{
		Computer toUpdate = askComputer();
		Optional<LocalDateTime> dateDebut = askDate(true, null);
		if(dateDebut.isPresent())
		{
			toUpdate.setDateIntroduction(dateDebut.get());
		}
		if(toUpdate.getDateIntroduction() != null)
		{
			Optional<LocalDateTime> dateFin = askDate(false, toUpdate.getDateDiscontinuation());
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
	
	
	
	
	public static void main(String[] args) {
		showComputerList();
		//System.out.println(LocalDate.parse("23/09/1996", dateFormatter).atStartOfDay());
		//showCompaniesList();
		//System.out.println(optionalActionYesNo("Voulez-vous écrire quelque chose ?", "Ecrivez quelque chose"));
		//createNewComputer();
		updateComputer();
	}

}
