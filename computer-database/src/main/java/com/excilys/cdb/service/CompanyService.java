package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;

@Service
public class CompanyService {

    @Autowired
    private CompanyDAO companyDao;
    @Autowired
    private ComputerDAO computerDao;

    private CompanyService() {
    }

    public List<Company> getCompaniesList() {
        return companyDao.getCompaniesList();
    }

    public Optional<Company> getCompanyByName(String name) {
        return companyDao.getCompanyByName(name);
    }

    public Optional<Company> getCompanyById(long id) {
        return companyDao.getCompanyById(id);
    }

    @Transactional
    public void deleteCompany(long id) {
        Optional<Company> optCompany = companyDao.getCompanyById(id);
        if (optCompany.isPresent()) {
            List<Long> idList = computerDao.getComputersIdsByCompanyId(id);
            for (long computerId : idList) {
                computerDao.deleteComputer(computerId);
            }
            companyDao.deleteCompany(id);
        }
    }

}
