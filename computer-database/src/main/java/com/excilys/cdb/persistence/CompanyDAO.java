package com.excilys.cdb.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;

@Repository
public class CompanyDAO {

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    private EntityManager em;

    @Autowired
    public CompanyDAO(EntityManager em) {
        this.em = em;
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_LIST_QUERY);
        return jdbcTemplateObject.query(SELECT_COMPANY_LIST_QUERY, companyMapper);*/
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_NAME_QUERY);
        try {
            Company c = jdbcTemplateObject.queryForObject(SELECT_COMPANY_BY_NAME_QUERY, companyMapper, name);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }*/
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_ID_QUERY);
        try {
            Company c = jdbcTemplateObject.queryForObject(SELECT_COMPANY_BY_ID_QUERY, companyMapper, id);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
        */
    }

    public void deleteCompany(long id, EntityTransaction t) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Company> cd = cb.createCriteriaDelete(Company.class);
        Root<Company> root = cd.from(Company.class);
        cd.where(cb.equal(root.get("id"), id));
        em.createQuery(cd).executeUpdate();
        logger.info("Deleted company with id : {}", id);
    }

    public static void main(String[] args) {
        /*CompanyDAO dao = new CompanyDAO();
        List<Company> l = dao.getCompaniesList();
        System.out.println(l);
        */
        //EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties)
    }
}
