package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.persistence.CompanyDAO;

public class CompanyService {
    
    private CompanyDAO dao;

    public CompanyService(CompanyDAO dao) {
        this.dao = dao;
    }

    public List<Company> getCompaniesList() {
        return dao.getCompaniesList();
    }

    public Optional<Company> getCompanyByName(String name) {
        return dao.getCompanyByName(name);
    }

}
