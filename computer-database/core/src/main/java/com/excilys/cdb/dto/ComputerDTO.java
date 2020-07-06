package com.excilys.cdb.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class ComputerDTO {

    private String id;
    private String nom;
    private String dateIntroduction;
    private String dateDiscontinuation;
    private CompanyDTO entreprise;

    private ComputerDTO(ComputerBuilderDTO builder) {
        this.id = builder.id;
        this.nom = builder.nom;
        this.dateIntroduction = builder.dateIntroduction;
        this.dateDiscontinuation = builder.dateDiscontinuation;
        this.entreprise = builder.entreprise;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDateIntroduction() {
        return dateIntroduction;
    }

    public String getDateDiscontinuation() {
        return dateDiscontinuation;
    }

    public CompanyDTO getEntreprise() {
        return entreprise;
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
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ComputerDTO)) {
            return false;
        }
        ComputerDTO other = (ComputerDTO) obj;
        return Objects.equals(dateDiscontinuation, other.dateDiscontinuation)
                && Objects.equals(dateIntroduction, other.dateIntroduction)
                && Objects.equals(entreprise, other.entreprise) && Objects.equals(id, other.id)
                && Objects.equals(nom, other.nom);
    }

    public static class ComputerBuilderDTO {
        // Obligatoires
        @NotNull
        private String id;
        @NotNull
        private String nom;

        // Optionnels
        private String dateIntroduction;
        private String dateDiscontinuation;
        private CompanyDTO entreprise;

        public ComputerBuilderDTO(String id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public ComputerBuilderDTO setDateIntroduction(String dateIntroduction) {
            this.dateIntroduction = dateIntroduction;
            return this;
        }

        public ComputerBuilderDTO setDateDiscontinuation(String dateDiscontinuation) {
            this.dateDiscontinuation = dateDiscontinuation;
            return this;
        }

        public ComputerBuilderDTO setEntreprise(CompanyDTO entreprise) {
            this.entreprise = entreprise;
            return this;
        }

        public String getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public String getDateIntroduction() {
            return dateIntroduction;
        }

        public String getDateDiscontinuation() {
            return dateDiscontinuation;
        }

        public CompanyDTO getEntreprise() {
            return entreprise;
        }

        public ComputerDTO build() {
            return new ComputerDTO(this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateDiscontinuation, dateIntroduction, entreprise, id, nom);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ComputerBuilderDTO)) {
                return false;
            }
            ComputerBuilderDTO other = (ComputerBuilderDTO) obj;
            return Objects.equals(dateDiscontinuation, other.dateDiscontinuation)
                    && Objects.equals(dateIntroduction, other.dateIntroduction)
                    && Objects.equals(entreprise, other.entreprise) && id == other.id && Objects.equals(nom, other.nom);
        }
    }
}
