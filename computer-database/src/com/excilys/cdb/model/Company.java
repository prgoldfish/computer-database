package com.excilys.cdb.model;

public class Company {

	String nom;
	int id;
	
	public Company(int id, String name) {
		this.nom = name;
		this.id = id;
	}

	/**
	 * @return Renvoie le nom de l'entreprise
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @return Renvoie l'identifiant de l'entreprise
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Company [nom=" + nom + ", id=" + id + "]";
	}
	
	
	
	
}
