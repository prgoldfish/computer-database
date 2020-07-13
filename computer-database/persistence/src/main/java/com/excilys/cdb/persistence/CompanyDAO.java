package com.excilys.cdb.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Company;

@Repository
public class CompanyDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    @PersistenceContext
    private EntityManager em;

    private int numOfElementsForLastRequest = 0;

    public int getNumOfElementsForLastRequest() {
        return numOfElementsForLastRequest;
    }

    /**
     * Fait une requête sur la base de données pour récupérer la liste des
     * entreprises
     *
     * @return Les entreprises dans une List
     */
    public List<Company> getCompaniesList() {
        numOfElementsForLastRequest = 0;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root);
        TypedQuery<Company> query = em.createQuery(cq);
        List<Company> res = query.getResultList();
        numOfElementsForLastRequest = res.size();
        return res;
    }

    public Optional<Company> getCompanyByName(String name) {
        numOfElementsForLastRequest = 0;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root).where(cb.equal(root.get("name"), name));
        try {
            Optional<Company> res = Optional.of(em.createQuery(cq).getSingleResult());
            numOfElementsForLastRequest = 1;
            return res;
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public Optional<Company> getCompanyById(long id) {
        numOfElementsForLastRequest = 0;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root).where(cb.equal(root.get("id"), id));
        try {
            Optional<Company> res = Optional.of(em.createQuery(cq).getSingleResult());
            numOfElementsForLastRequest = 1;
            return res;
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCompany(long id) {
        numOfElementsForLastRequest = 0;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Company> cd = cb.createCriteriaDelete(Company.class);
        Root<Company> root = cd.from(Company.class);
        cd.where(cb.equal(root.get("id"), id));
        em.createQuery(cd).executeUpdate();
        logger.info("Deleted company with id : {}", id);
    }
}
