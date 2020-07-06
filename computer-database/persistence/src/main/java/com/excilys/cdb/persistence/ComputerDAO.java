package com.excilys.cdb.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;

@Repository
public class ComputerDAO {

    private static final Logger logger = LoggerFactory.getLogger(ComputerDAO.class);

    private EntityManager em;

    @Autowired
    public ComputerDAO(EntityManager em) {
        this.em = em;

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
        /*
        String orderByColumns = processOrderBy(orderBy, ascendentOrder);
        String request = SELECT_COMPUTER_LIST_QUERY + orderByColumns + LIMIT_OFFSET;
        
        logger.info("Exécution de la requête \"{}\"", request);
        return jdbcTemplateObject.query(request, computerMapper, limit, startIndex);*/
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_COMPANY_ID_QUERY);
        return jdbcTemplateObject.query(SELECT_COMPUTER_BY_COMPANY_ID_QUERY,
                (resultSet, numRow) -> resultSet.getLong("id"), companyId);*/
    }

    public long getMaxId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Computer> root = cq.from(Computer.class);
        cq.select(cb.max(root.get("id")));
        TypedQuery<Long> query = em.createQuery(cq);
        return query.getSingleResult();

        /*
        logger.info("Exécution de la requête \"{}\"", GET_MAX_ID_QUERY);
        return jdbcTemplateObject.queryForObject(GET_MAX_ID_QUERY, Long.class);
        */
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_ID_QUERY);
        try {
            Computer c = jdbcTemplateObject.queryForObject(SELECT_COMPUTER_BY_ID_QUERY, computerMapper, id);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }*/
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
        /*
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_NAME_QUERY);
        name = name.replace("%", "\\%");
        try {
            Computer c = jdbcTemplateObject.queryForObject(SELECT_COMPUTER_BY_NAME_QUERY, computerMapper, name);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
        */
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

        /*
        String request = SEARCH_COMPUTERS_BY_NAME_OR_COMPANY_QUERY + processOrderBy(orderBy, ascendentOrder);
        name = "%" + name.replace("%", "\\%") + "%";
        logger.info("Exécution de la requête \"{}\"", request);
        return jdbcTemplateObject.query(request, computerMapper, name, name);*/
    }

    public void addComputer(Computer c) {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(c);
        tr.commit();

        /*
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeAddComputerQuery(c, entreprise, introTimestamp, discontTimestamp);
        */
    }

    public void updateComputer(Computer c) {
        Optional<Computer> optOld = getComputerById(c.getId());
        if (optOld.isEmpty()) {
            throw new RuntimeException("The old computer cannot be found");
        }
        Computer old = optOld.get();
        old.setName(c.getName());
        old.setIntroduced(c.getIntroduced());
        old.setDiscontinued(c.getDiscontinued());
        old.setCompany(c.getCompany());
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Computer> cu = cb.createCriteriaUpdate(Computer.class);
        Root<Computer> root = cu.from(Computer.class);
        cu.set("name", old.getName());
        cu.set("introduced", old.getIntroduced());
        cu.set("discontinued", old.getDiscontinued());
        cu.set("company", old.getCompany());
        cu.where(cb.equal(root.get("id"), old.getId()));

        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.createQuery(cu).executeUpdate();
        em.flush();
        tr.commit();

        /*
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeUpdateComputerQuery(c, entreprise, introTimestamp, discontTimestamp);*/
    }

    public void deleteComputer(long id, EntityTransaction t) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Computer> cd = cb.createCriteriaDelete(Computer.class);
        Root<Computer> root = cd.from(Computer.class);
        cd.where(cb.equal(root.get("id"), id));
        em.createQuery(cd).executeUpdate();
        /*
        logger.info("Exécution de la requête \"{}\"", DELETE_COMPUTER_QUERY);
        jdbcTemplateObject.update(DELETE_COMPUTER_QUERY, id);*/
        logger.info("Deleted computer with id : {}", id);
    }
}
