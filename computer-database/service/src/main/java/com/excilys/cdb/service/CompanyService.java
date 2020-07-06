package com.excilys.cdb.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.persistence.CompanyDAO;
import com.excilys.cdb.persistence.ComputerDAO;

@Service
public class CompanyService {

    @Autowired
    private CompanyDAO companyDao;
    @Autowired
    private ComputerDAO computerDao;

    @Autowired
    private EntityManager em;

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

    public void deleteCompany(long id) {
        EntityTransaction t = em.getTransaction();
        Optional<Company> optCompany = companyDao.getCompanyById(id);
        if (optCompany.isPresent()) {
            List<Long> idList = computerDao.getComputersIdsByCompanyId(id);
            try {
                t.begin();
                for (long computerId : idList) {
                    computerDao.deleteComputer(computerId, t);
                }
                companyDao.deleteCompany(id, t);
                t.commit();
            } catch (RuntimeException re) {
                t.rollback();
            }
        }
    }

}
