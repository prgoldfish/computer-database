package com.excilys.cdb.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Company;

@Repository
public class CompanyDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    private EntityManager em;

    @Autowired
    public CompanyDAO(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    /**
     * Fait une requête sur la base de données pour récupérer la liste des
     * entreprises
     *
     * @return Les entreprises dans une List
     */
    public List<Company> getCompaniesList() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root);
        TypedQuery<Company> query = em.createQuery(cq);
        return query.getResultList();
    }

    public Optional<Company> getCompanyByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root).where(cb.equal(root.get("name"), name));
        try {
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public Optional<Company> getCompanyById(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);
        cq.select(root).where(cb.equal(root.get("id"), id));
        try {
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCompany(long id) {
        if (!em.isJoinedToTransaction()) {
            throw new TransactionRequiredException();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Company> cd = cb.createCriteriaDelete(Company.class);
        Root<Company> root = cd.from(Company.class);
        cd.where(cb.equal(root.get("id"), id));
        em.createQuery(cd).executeUpdate();
        logger.info("Deleted company with id : {}", id);
    }
}
