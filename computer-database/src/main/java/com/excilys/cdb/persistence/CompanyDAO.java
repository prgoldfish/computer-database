package com.excilys.cdb.persistence;

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

@Repository
public class CompanyDAO {

    private static final String SELECT_COMPANY_LIST_QUERY = "SELECT id, name FROM company";
    private static final String SELECT_COMPANY_BY_NAME_QUERY = "SELECT id, name FROM company WHERE name = ?";
    private static final String SELECT_COMPANY_BY_ID_QUERY = "SELECT id, name FROM company WHERE id = ?";
    private static final String DELETE_COMPANY_QUERY = "DELETE FROM company WHERE id = ?";

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    private static RowMapper<Company> companyMapper = (resultSet, numRow) -> new Company(resultSet.getLong("id"),
            resultSet.getString("name"));

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    /**
     * Fait une requête sur la base de données pour récupérer la liste des
     * entreprises
     *
     * @return Les entreprises dans une List
     */
    public List<Company> getCompaniesList() {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_LIST_QUERY);
        return jdbcTemplateObject.query(SELECT_COMPANY_LIST_QUERY, companyMapper);
    }

    public Optional<Company> getCompanyByName(String name) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_NAME_QUERY);
        try {
            Company c = jdbcTemplateObject.queryForObject(SELECT_COMPANY_BY_NAME_QUERY, companyMapper, name);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
    }

    public Optional<Company> getCompanyById(long id) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_ID_QUERY);
        try {
            Company c = jdbcTemplateObject.queryForObject(SELECT_COMPANY_BY_ID_QUERY, companyMapper, id);
            return Optional.of(c);
        } catch (EmptyResultDataAccessException dae) {
            return Optional.empty();
        }
    }

    public void deleteCompany(long id) {
        logger.info("Exécution de la requête \"{}\"", DELETE_COMPANY_QUERY);
        jdbcTemplateObject.update(DELETE_COMPANY_QUERY, id);
    }

    public static void main(String[] args) {
        CompanyDAO dao = new CompanyDAO();
        List<Company> l = dao.getCompaniesList();
        System.out.println(l);
    }
}
