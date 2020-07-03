package com.excilys.cdb.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "computer")
public class Computer implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7047630403351541471L;

    @Id
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduced")
    private LocalDateTime introduced;

    @Column(name = "discontinued")
    private LocalDateTime discontinued;

    @ManyToOne(targetEntity = Company.class)
    @JoinColumn(name = "company_id")
    private Company company;

    public Computer() {
    }

    private Computer(ComputerBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.introduced = builder.introduced;
        this.discontinued = builder.discontinued;
        this.company = builder.company;
    }

    /**
     * @return Renvoie l'identifiant de l'ordinateur
     */
    public long getId() {
        return id;
    }

    /**
     * @return Renvoie le nom de l'ordinateur
     */
    public String getName() {
        return name;
    }

    /**
     * @return Renvoie la date d'intrduction de l'ordinateur
     */
    public LocalDateTime getIntroduced() {
        return introduced;
    }

    /**
     * @return Renvoie la date de discontinuation de l'ordinateur
     */
    public LocalDateTime getDiscontinued() {
        return discontinued;
    }

    /**
     * @return Renvoie l'identifiant de l'entreprise associée à l'ordinateur
     */
    public Company getCompany() {
        return company;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduced(LocalDateTime introduced) {
        this.introduced = introduced;
    }

    public void setDiscontinued(LocalDateTime discontinued) {
        this.discontinued = discontinued;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        String indefini = "Indefini";
        StringBuilder res = new StringBuilder("Identifiant : ");
        res.append(id);
        res.append("\nNom de l'ordinateur : ").append(name == null ? indefini : name);
        res.append("\nDate d'introduction : ").append(introduced == null ? indefini : introduced);
        res.append("\nDate de fin : ").append(discontinued == null ? indefini : discontinued);
        res.append("\nEntreprise : ").append(company == null ? indefini : company);
        return res.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(discontinued, introduced, company, id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Computer)) {
            return false;
        }
        Computer other = (Computer) obj;
        return Objects.equals(discontinued, other.discontinued) && Objects.equals(introduced, other.introduced)
                && Objects.equals(company, other.company) && id == other.id && Objects.equals(name, other.name);
    }

    public static class ComputerBuilder {
        // Obligatoires
        private long id;
        private String name;

        // Optionnels
        private LocalDateTime introduced;
        private LocalDateTime discontinued;
        private Company company;

        public ComputerBuilder(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public ComputerBuilder setIntroduced(LocalDateTime dateIntroduction) {
            this.introduced = dateIntroduction;
            return this;
        }

        public ComputerBuilder setDiscontinued(LocalDateTime dateDiscontinuation) {
            this.discontinued = dateDiscontinuation;
            return this;
        }

        public ComputerBuilder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public LocalDateTime getIntroduced() {
            return this.introduced;
        }

        public LocalDateTime getDiscontinued() {
            return this.discontinued;
        }

        public Computer build() {
            return new Computer(this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(discontinued, introduced, company, id, name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ComputerBuilder)) {
                return false;
            }
            ComputerBuilder other = (ComputerBuilder) obj;
            return Objects.equals(discontinued, other.discontinued) && Objects.equals(introduced, other.introduced)
                    && Objects.equals(company, other.company) && id == other.id && Objects.equals(name, other.name);
        }
    }

}
