package com.excilys.cdb.model;

import java.util.Objects;

public class Company implements Comparable<Company> {

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

    @Override
    public int hashCode() {
        return Objects.hash(id, nom);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Company)) {
            return false;
        }
        Company other = (Company) obj;
        return id == other.id && Objects.equals(nom, other.nom);
    }

    @Override
    public int compareTo(Company o) {
        long otherId = o == null ? 0 : o.id;
        return (int) (id - otherId);
    }

}
