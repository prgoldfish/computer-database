package com.excilys.cdb.model;

public class Company {

	String nom;
	long id;
	
	public Company(long id, String name) {
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
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return nom + "(" + id + ")";
	}
	
	
	
	
}
