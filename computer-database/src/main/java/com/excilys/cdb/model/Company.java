package com.excilys.cdb.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name")
    private String name;

    public Company() {
    }

    public Company(long id, String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * @return Renvoie le nom de l'entreprise
     */
    public String getName() {
        return name;
    }

    /**
     * @return Renvoie l'identifiant de l'entreprise
     */
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
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
        return id == other.id && Objects.equals(name, other.name);
    }

}
