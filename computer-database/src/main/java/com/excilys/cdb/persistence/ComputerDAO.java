package com.excilys.cdb.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Computer;

@Repository
public class ComputerDAO {

    private static final Logger logger = LoggerFactory.getLogger(ComputerDAO.class);

    private EntityManagerFactory emFactory;
    private EntityManager em;

    @Autowired
    public ComputerDAO(EntityManagerFactory emf) {
        emFactory = emf;
        em = emFactory.createEntityManager();

    }

    private Order[] getCriteriaOrders(CriteriaBuilder cb, Root<Computer> root, OrderByColumn orderBy,
            boolean ascendentOrder) {
        if (orderBy == null) {
            return new Order[] { cb.asc(root.get(OrderByColumn.COMPUTERID.getColumnName())) };
        }
        Function<String, Order> mkOrder = s -> ascendentOrder ? cb.asc(root.get(s)) : cb.desc(root.get(s));
        List<Order> res = new ArrayList<>();
        if (orderBy != OrderByColumn.COMPUTERID) {
            res.add(mkOrder.apply(orderBy.getColumnName()));
        }
        res.add(cb.asc(root.get(OrderByColumn.COMPUTERID.getColumnName())));
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
        cq.select(root.get("computer.id")).where(cb.equal(root.get("company.id"), companyId));
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
            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (NoResultException nre) {
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
        em.persist(c);

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Computer> cu = cb.createCriteriaUpdate(Computer.class);
        Root<Computer> root = cu.from(Computer.class);
        cu.set("name", c.getName());
        cu.set("introduced", c.getIntroduced());
        cu.set("discontinued", c.getDiscontinued());
        cu.set("company_id", c.getEntreprise() == null ? null : c.getEntreprise().getId());
        cu.where(cb.equal(root.get("id"), c.getId()));
        em.createQuery(cu).executeUpdate();

        /*
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeUpdateComputerQuery(c, entreprise, introTimestamp, discontTimestamp);*/
    }

    public void deleteComputer(long id) {
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
