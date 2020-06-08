package com.excilys.cdb.dto;

import java.util.Objects;


public class CompanyDTO {
    
    String nom;
    String id;

    public CompanyDTO(String id, String name) {
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
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return nom + "(" + id + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CompanyDTO))
            return false;
        CompanyDTO other = (CompanyDTO) obj;
        return id.equals(other.id) && Objects.equals(nom, other.nom);
    }

}
