package com.excilys.cdb.model;

import java.time.LocalDateTime;

public class Computer {
	
	private int id;
	private String nom;
	private LocalDateTime dateIntroduction;
	private LocalDateTime dateDiscontinuation;
	private Integer idEntreprise;
	
	
	private Computer(ComputerBuilder builder) {
		this.id = builder.id;
		this.nom = builder.nom;
		this.dateIntroduction = builder.dateIntroduction;
		this.dateDiscontinuation = builder.dateDiscontinuation;
		this.idEntreprise = builder.idEntreprise;
	}
	
	/**
	 * @return Renvoie l'identifiant de l'ordinateur
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id Identifiant de l'ordinateur
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return Renvoie le nom de l'ordinateur
	 */	
	public String getNom() {
		return nom;
	}
	
	/**
	 * 
	 * @param nom Nom de l'ordinateur
	 */	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/**
	 * @return Renvoie la date d'intrduction de l'ordinateur
	 */	
	public LocalDateTime getDateIntroduction() {
		return dateIntroduction;
	}
	
	/**
	 * 
	 * @param dateIntroduction Date d'introduction de l'ordinateur
	 */
	public void setDateIntroduction(LocalDateTime dateIntroduction) {
		this.dateIntroduction = dateIntroduction;
	}
	
	/**
	 * @return Renvoie la date de discontinuation de l'ordinateur
	 */	
	public LocalDateTime getDateDiscontinuation() {
		return dateDiscontinuation;
	}
	
	/**
	 * 
	 * @param dateDiscontinuation Date de discontinuation de l'ordinateur
	 */	
	public void setDateDiscontinuation(LocalDateTime dateDiscontinuation) {
		this.dateDiscontinuation = dateDiscontinuation;
	}
	
	/**
	 * @return Renvoie l'identifiant de l'entreprise associée à l'ordinateur
	 */
	public Integer getIdEntreprise() {
		return idEntreprise;
	}
	
	/**
	 * 
	 * @param idEntreprise Identifiant de l'entreprise associée à l'ordinateur
	 */
	public void setIdEntreprise(Integer idEntreprise) {
		this.idEntreprise = idEntreprise;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("Computer [id=");
		res.append(id);
		res.append(", nom=").append(nom == null ? "Indefini" : nom);
		res.append(", dateIntroduction=").append(dateIntroduction == null ? "Indefini" : dateIntroduction);
		res.append(", dateDiscontinuation=").append(dateDiscontinuation == null ? "Indefini" : dateDiscontinuation);
		res.append(", idEntreprise=").append(idEntreprise).append("]");
		return res.toString();
	}
	
	public static class ComputerBuilder {
		//Obligatoires
		private int id;
		private String nom;
		
		//Optionnels
		private LocalDateTime dateIntroduction;
		private LocalDateTime dateDiscontinuation;
		private Integer idEntreprise;
		
		
		public ComputerBuilder(int id, String nom) {
			this.id = id;
			this.nom = nom;
		}


		public ComputerBuilder setDateIntroduction(LocalDateTime dateIntroduction) {
			this.dateIntroduction = dateIntroduction;
			return this;
		}


		public ComputerBuilder setDateDiscontinuation(LocalDateTime dateDiscontinuation) {
			this.dateDiscontinuation = dateDiscontinuation;
			return this;
		}


		public ComputerBuilder setIdEntreprise(Integer idEntreprise) {
			this.idEntreprise = idEntreprise;
			return this;
		}
		
		public Computer build()
		{
			return new Computer(this);
		}		
	}
	
	
	
	
}
