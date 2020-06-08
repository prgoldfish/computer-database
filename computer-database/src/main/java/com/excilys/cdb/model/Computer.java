package com.excilys.cdb.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Computer {

    private long id;
    private String nom;
    private LocalDateTime dateIntroduction;
    private LocalDateTime dateDiscontinuation;
    private Company entreprise;

    private Computer(ComputerBuilder builder) {
        this.id = builder.id;
        this.nom = builder.nom;
        this.dateIntroduction = builder.dateIntroduction;
        this.dateDiscontinuation = builder.dateDiscontinuation;
        this.entreprise = builder.entreprise;
    }

    /**
     * @return Renvoie l'identifiant de l'ordinateur
     */
    public long getId() {
        return id;
    }

    /**
     * 
     * @param id Identifiant de l'ordinateur
     */
    public void setId(long id) {
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
    public Company getEntreprise() {
        return entreprise;
    }

    /**
     * 
     * @param idEntreprise Identifiant de l'entreprise associée à l'ordinateur
     */
    public void setEntreprise(Company entreprise) {
        this.entreprise = entreprise;
    }

    @Override
    public String toString() {
        String indefini = "Indefini";
        StringBuilder res = new StringBuilder("Identifiant : ");
        res.append(id);
        res.append("\nNom de l'ordinateur : ").append(nom == null ? indefini : nom);
        res.append("\nDate d'introduction : ").append(dateIntroduction == null ? indefini : dateIntroduction);
        res.append("\nDate de fin : ").append(dateDiscontinuation == null ? indefini : dateDiscontinuation);
        res.append("\nEntreprise : ").append(entreprise == null ? indefini : entreprise);
        return res.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateDiscontinuation, dateIntroduction, entreprise, id, nom);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Computer))
            return false;
        Computer other = (Computer) obj;
        return Objects.equals(dateDiscontinuation, other.dateDiscontinuation)
                && Objects.equals(dateIntroduction, other.dateIntroduction)
                && Objects.equals(entreprise, other.entreprise) && id == other.id && Objects.equals(nom, other.nom);
    }

    public static class ComputerBuilder {
        // Obligatoires
        private long id;
        private String nom;

        // Optionnels
        private LocalDateTime dateIntroduction;
        private LocalDateTime dateDiscontinuation;
        private Company entreprise;

        public ComputerBuilder(long id, String nom) {
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

        public ComputerBuilder setEntreprise(Company entreprise) {
            this.entreprise = entreprise;
            return this;
        }

        public LocalDateTime getDateIntroduction() {
            return this.dateIntroduction;
        }

        public LocalDateTime getDateDiscontinuation() {
            return this.dateDiscontinuation;
        }

        public Computer build() {
            return new Computer(this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateDiscontinuation, dateIntroduction, entreprise, id, nom);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof ComputerBuilder))
                return false;
            ComputerBuilder other = (ComputerBuilder) obj;
            return Objects.equals(dateDiscontinuation, other.dateDiscontinuation)
                    && Objects.equals(dateIntroduction, other.dateIntroduction)
                    && Objects.equals(entreprise, other.entreprise) && id == other.id && Objects.equals(nom, other.nom);
        }
    }

}
