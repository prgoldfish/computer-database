package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.persistence.CompanyDAO;

public class CompanyService {

    public CompanyService() {

    }

    public List<Company> getCompaniesList() {
        return new CompanyDAO().getCompaniesList();
    }

    public Optional<Company> getCompanyByName(String name) {
        return new CompanyDAO().getCompanyByName(name);
    }

}
