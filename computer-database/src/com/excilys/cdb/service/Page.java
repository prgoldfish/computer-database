package com.excilys.cdb.service;

import java.time.LocalDateTime;
import java.util.List;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

public class Page {
	
	private List<Computer> computerList;
	private int pageLength;
	private int currentPage;
	private int maxPage;
	private static String header = setHeader();
	
	/**
	 * Crée le header en haut de chaque page
	 * @return Une String repésentant le header
	 */
	private static String setHeader()
	{
		StringBuilder head = new StringBuilder("| Id\t| ");
		head.append(String.format("%1$-70s", "Nom"));
		head.append(String.format("%1$-22s", "| Date d'introduction"));
		head.append(String.format("%1$-26s", "| Date de discontinuation"));
		head.append(String.format("%1$-47s", "| Entreprise"));
		head.append("|\n");
		return head.toString();
	}
	
	public Page(List<Computer> list, int pageLength)
	{
		this.computerList = list;
		this.pageLength = pageLength;
		this.currentPage = 0;
		this.maxPage = (computerList.size() - 1) / pageLength;
	}
	
	/**
	 * Affiche chaque élément de la page actuelle sous forme de tableau
	 */
	public void printPage()
	{
		int maxIndex = Math.min(computerList.size(), (currentPage + 1) * pageLength);
		System.out.println(header);
		for(Computer c : computerList.subList(currentPage * pageLength, maxIndex))
		{
			StringBuilder outString = new StringBuilder();
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
			outString.append("|");
			System.out.println(outString);
		}
		System.out.println("\nPage " + (currentPage + 1) + "/" + (maxPage + 1));
	}
	
	/**
	 * Renvoie le nombre maximum de pages
	 * @return Le nombre max de pages (en partant de 0)
	 */
	public int getMaxPage() {
		return maxPage;
	}
	
	/**
	 * Passe à la page suivante et l'affiche 
	 */
	public void printNextPage() {
		if(currentPage + 1 > maxPage)
		{
			System.out.println("Il n'y a pas de page suivante");
		}
		else
		{
			currentPage++;
			printPage();
		}
	}
	
	/**
	 * Passe à la page précédente et l'affiche
	 */
	public void printPreviousPage()
	{
		if(currentPage - 1 < 0)
		{
			System.out.println("Il n'y a pas de page précédente");
		}
		else
		{
			currentPage--;
			printPage();
		}
	}
	
	/** 
	 * Va à la page indiquée en paramètre
	 * @param pageNumber Le numéro de page voulu
	 */
	public void gotoPage(int pageNumber)
	{
		if(pageNumber >= 0 && pageNumber <= maxPage)
		{
			currentPage = pageNumber;
		}
	}
	
	
	
	


}
