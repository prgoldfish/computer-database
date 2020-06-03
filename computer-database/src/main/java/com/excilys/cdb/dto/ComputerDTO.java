package com.excilys.cdb.dto;

import java.util.Objects;


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
    public void setId(String id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDateIntroduction() {
        return dateIntroduction;
    }
    public void setDateIntroduction(String dateIntroduction) {
        this.dateIntroduction = dateIntroduction;
    }
    public String getDateDiscontinuation() {
        return dateDiscontinuation;
    }
    public void setDateDiscontinuation(String dateDiscontinuation) {
        this.dateDiscontinuation = dateDiscontinuation;
    }
    public CompanyDTO getEntreprise() {
        return entreprise;
    }
    public void setEntreprise(CompanyDTO entreprise) {
        this.entreprise = entreprise;
    }
    
    public static class ComputerBuilderDTO {
        // Obligatoires
        private String id;
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
        
        public ComputerDTO build() {
            return new ComputerDTO(this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateDiscontinuation, dateIntroduction, entreprise, id, nom);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof ComputerBuilderDTO))
                return false;
            ComputerBuilderDTO other = (ComputerBuilderDTO) obj;
            return Objects.equals(dateDiscontinuation, other.dateDiscontinuation)
                    && Objects.equals(dateIntroduction, other.dateIntroduction)
                    && Objects.equals(entreprise, other.entreprise) && id == other.id && Objects.equals(nom, other.nom);
        }
    }
}
