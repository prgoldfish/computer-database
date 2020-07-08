package com.excilys.cdb.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@Repository
public class ComputerDAO {

    private static final Logger logger = LoggerFactory.getLogger(ComputerDAO.class);

    private EntityManager em;

    @Autowired
    public ComputerDAO(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();

    }

    private Order[] getCriteriaOrders(CriteriaBuilder cb, Root<Computer> root, OrderByColumn orderBy,
            boolean ascendentOrder) {
        if (orderBy == null) {
            return new Order[] { cb.asc(root.get(OrderByColumn.COMPUTERID.getColumnName())) };
        }
        Join<Computer, Company> join = root.join(
                em.getMetamodel().entity(Computer.class).getDeclaredSingularAttribute("company", Company.class),
                JoinType.LEFT);
        List<Order> res = new ArrayList<>();
        switch (orderBy) {
        case COMPUTERNAME:
        case COMPUTERINTRO:
        case COMPUTERDISCONT:
        case COMPUTERID:
            res.add(ascendentOrder ? cb.asc(root.get(orderBy.getColumnName()))
                    : cb.desc(root.get(orderBy.getColumnName())));
            break;
        case COMPANYID:
        case COMPANYNAME:
            res.add(ascendentOrder ? cb.asc(join.get(orderBy.getColumnName()))
                    : cb.desc(join.get(orderBy.getColumnName())));
            break;
        }

        if (orderBy != OrderByColumn.COMPUTERID) {
            res.add(cb.asc(root.get(OrderByColumn.COMPUTERID.getColumnName())));
        }
        return res.toArray(new Order[0]);

    }

    /**
     * Fais une requête sur la base de données pour récupérer la liste des
     * ordinateurs
     *
     * @return Les ordinateurs dans une List
     */
    public List<Computer> getComputerList(long startIndex, long limit, OrderByColumn orderBy, boolean ascendentOrder) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Computer> cq = cb.createQuery(Computer.class);
        Root<Computer> root = cq.from(Computer.class);

        cq.select(root).orderBy(getCriteriaOrders(cb, root, orderBy, ascendentOrder));
        TypedQuery<Computer> query = em.createQuery(cq).setFirstResult((int) startIndex).setMaxResults((int) limit);
        return query.getResultList();
    }

    public List<Long> getComputersIdsByCompanyId(long companyId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Computer> root = cq.from(Computer.class);
        Join<Computer, Company> join = root.join(
                em.getMetamodel().entity(Computer.class).getDeclaredSingularAttribute("company", Company.class),
                JoinType.LEFT);
        cq.select(root.get("id")).where(cb.equal(join.get("id"), companyId));
        TypedQuery<Long> query = em.createQuery(cq);
        return query.getResultList();
    }

    public long getMaxId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Computer> root = cq.from(Computer.class);
        cq.select(cb.max(root.get("id")));
        TypedQuery<Long> query = em.createQuery(cq);
        return query.getSingleResult();
    }

    public Optional<Computer> getComputerById(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Computer> cq = cb.createQuery(Computer.class);
        Root<Computer> root = cq.from(Computer.class);
        cq.select(root).where(cb.equal(root.get("id"), id));
        try {
            return Optional.of(em.createQuery(cq).getResultList().get(0));
        } catch (IndexOutOfBoundsException ioobe) {
            return Optional.empty();
        }
    }

    public Optional<Computer> getComputerByName(String name) {
        name = "%" + name.replace("%", "\\%") + "%";
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Computer> cq = cb.createQuery(Computer.class);
        Root<Computer> root = cq.from(Computer.class);
        cq.select(root).where(cb.like(root.get("name"), name));
        try {
            return Optional.of(em.createQuery(cq).getResultList().get(0));
        } catch (IndexOutOfBoundsException ioobe) {
            return Optional.empty();
        }
    }

    public List<Computer> searchComputersByName(String name, OrderByColumn orderBy, boolean ascendentOrder) {
        name = "%" + name.replace("%", "\\%") + "%";
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Computer> cq = cb.createQuery(Computer.class);
        Root<Computer> root = cq.from(Computer.class);

        cq.select(root).where(cb.like(root.get("name"), name))
                .orderBy(getCriteriaOrders(cb, root, orderBy, ascendentOrder));
        TypedQuery<Computer> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addComputer(Computer c) {
        em.persist(c);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateComputer(Computer c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Computer> cu = cb.createCriteriaUpdate(Computer.class);
        Root<Computer> root = cu.from(Computer.class);
        cu.set("name", c.getName());
        cu.set("introduced", c.getIntroduced());
        cu.set("discontinued", c.getDiscontinued());
        cu.set("company", c.getCompany());
        cu.where(cb.equal(root.get("id"), c.getId()));

        em.createQuery(cu).executeUpdate();
        em.flush();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteComputer(long id) {
        if (!em.isJoinedToTransaction()) {
            throw new TransactionRequiredException();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Computer> cd = cb.createCriteriaDelete(Computer.class);
        Root<Computer> root = cd.from(Computer.class);
        cd.where(cb.equal(root.get("id"), id));
        em.createQuery(cd).executeUpdate();
        logger.info("Deleted computer with id : {}", id);
    }
}
