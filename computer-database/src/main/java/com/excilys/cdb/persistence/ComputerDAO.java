package com.excilys.cdb.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

@Repository
public class ComputerDAO {

    private static final String SELECT_COMPUTER_LIST_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id ORDER BY ";
    private static final String SELECT_COMPUTER_BY_ID_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id WHERE computer.id=?";
    private static final String SELECT_COMPUTER_BY_COMPANY_ID_QUERY = "SELECT id FROM computer WHERE computer.company_id=?";
    private static final String SELECT_COMPUTER_BY_NAME_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id WHERE computer.name=?";
    private static final String SEARCH_COMPUTERS_BY_NAME_OR_COMPANY_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id WHERE computer.name LIKE ? OR company.name LIKE ? ORDER BY ";
    private static final String ADD_COMPUTER_QUERY = "INSERT INTO computer VALUES (?,?,?,?,?)";
    private static final String GET_MAX_ID_QUERY = "SELECT MAX(id) as idMax FROM computer";
    private static final String UPDATE_COMPUTER_QUERY = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
    private static final String DELETE_COMPUTER_QUERY = "DELETE FROM computer WHERE id = ?";
    private static final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";
    private static final Logger logger = LoggerFactory.getLogger(ComputerDAO.class);

    private static RowMapper<Computer> computerMapper = (resultSet, numRow) -> processGetComputerResults(resultSet);

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    /**
     * Fais une requête sur la base de données pour récupérer la liste des
     * ordinateurs
     *
     * @return Les ordinateurs dans une List
     */
    public List<Computer> getComputerList(long startIndex, long limit, OrderByColumn orderBy, boolean ascendentOrder) {
        String orderByColumns = processOrderBy(orderBy, ascendentOrder);
        String request = SELECT_COMPUTER_LIST_QUERY + orderByColumns + LIMIT_OFFSET;

        logger.info("Exécution de la requête \"{}\"", request);
        return jdbcTemplateObject.query(request, computerMapper, limit, startIndex);
    }

    public List<Long> getComputersIdsByCompanyId(long companyId) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_COMPANY_ID_QUERY);
        return jdbcTemplateObject.query(SELECT_COMPUTER_BY_COMPANY_ID_QUERY,
                (resultSet, numRow) -> resultSet.getLong("id"), companyId);
    }

    public long getMaxId() {
        logger.info("Exécution de la requête \"{}\"", GET_MAX_ID_QUERY);
        return jdbcTemplateObject.queryForObject(GET_MAX_ID_QUERY, Long.class);

    }

    public Optional<Computer> getComputerById(long id) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_ID_QUERY);
        try {
            Computer c = jdbcTemplateObject.queryForObject(SELECT_COMPUTER_BY_ID_QUERY, computerMapper, id);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
    }

    public Optional<Computer> getComputerByName(String name) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_NAME_QUERY);
        name = name.replace("%", "\\%");
        try {
            Computer c = jdbcTemplateObject.queryForObject(SELECT_COMPUTER_BY_NAME_QUERY, computerMapper, name);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
    }

    public List<Computer> searchComputersByName(String name, OrderByColumn orderBy, boolean ascendentOrder) {
        String request = SEARCH_COMPUTERS_BY_NAME_OR_COMPANY_QUERY + processOrderBy(orderBy, ascendentOrder);
        name = "%" + name.replace("%", "\\%") + "%";
        logger.info("Exécution de la requête \"{}\"", request);
        return jdbcTemplateObject.query(request, computerMapper, name, name);
    }

    private static Computer processGetComputerResults(ResultSet res) throws SQLException {
        Timestamp intro = res.getTimestamp("introduced");
        Timestamp discont = res.getTimestamp("discontinued");
        int idEntreprise = res.getInt("company_id");
        ComputerBuilder c = new Computer.ComputerBuilder(res.getInt("computer.id"), res.getString("computerName"));
        if (intro != null) {
            c.setDateIntroduction(intro.toLocalDateTime());
        }
        if (discont != null) {
            c.setDateDiscontinuation(discont.toLocalDateTime());
        }
        if (idEntreprise != 0) {
            String companyName = res.getString("companyName");
            c.setEntreprise(new Company(idEntreprise, companyName));
        }
        return c.build();
    }

    private static String processOrderBy(OrderByColumn orderBy, boolean ascendentOrder) {
        String orderByColumns = orderBy.getColumnName() + (ascendentOrder ? " ASC" : " DESC");
        orderByColumns += orderBy == OrderByColumn.COMPUTERID ? "" : ", computer.id asc";
        return orderByColumns;
    }

    public void addComputer(Computer c) {
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeAddComputerQuery(c, entreprise, introTimestamp, discontTimestamp);
    }

    private void executeAddComputerQuery(Computer c, Company entreprise, Timestamp introTimestamp, Timestamp discontTimestamp) {
        logger.info("Exécution de la requête \"{}\"", ADD_COMPUTER_QUERY);
        Long companyId = entreprise == null ? null : entreprise.getId();
        jdbcTemplateObject.update(ADD_COMPUTER_QUERY, c.getId(), c.getNom(), introTimestamp, discontTimestamp,
                companyId);
    }

    public void updateComputer(Computer c) {
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeUpdateComputerQuery(c, entreprise, introTimestamp, discontTimestamp);
    }

    private void executeUpdateComputerQuery(Computer c, Company entreprise, Timestamp introTimestamp, Timestamp discontTimestamp) {
        logger.info("Exécution de la requête \"{}\"", UPDATE_COMPUTER_QUERY);
        Long companyId = entreprise == null ? null : entreprise.getId();
        jdbcTemplateObject.update(UPDATE_COMPUTER_QUERY, c.getNom(), introTimestamp, discontTimestamp, companyId,
                c.getId());
    }

    public void deleteComputer(long id) {
        logger.info("Exécution de la requête \"{}\"", DELETE_COMPUTER_QUERY);
        jdbcTemplateObject.update(DELETE_COMPUTER_QUERY, id);
        logger.info("Deleted computer with id : {}", id);
    }
}
