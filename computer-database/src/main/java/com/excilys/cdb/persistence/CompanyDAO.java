package com.excilys.cdb.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.model.Company;

public class CompanyDAO {

    private static final String SELECT_COMPANY_LIST_QUERY = "SELECT id, name FROM company";
    private static final String SELECT_COMPANY_BY_NAME_QUERY = "SELECT id, name FROM company WHERE name = ?";
    private static final String SELECT_COMPANY_BY_ID_QUERY = "SELECT id, name FROM company WHERE id = ?";
    private static final String DELETE_COMPANY_QUERY = "DELETE FROM company WHERE id = ?";

    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    public CompanyDAO() {

    }

    /**
     * Fait une requête sur la base de données pour récupérer la liste des
     * entreprises
     *
     * @return Les entreprises dans une List
     */
    public List<Company> getCompaniesList() {
        List<Company> compList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement();) {
            logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_LIST_QUERY);
            ResultSet res = stmt.executeQuery(SELECT_COMPANY_LIST_QUERY);
            while (res.next()) {
                Company c = new Company(res.getLong("id"), res.getString("name"));
                compList.add(c);
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return compList;
    }

    public Optional<Company> getCompanyByName(String name) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_NAME_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPANY_BY_NAME_QUERY);) {
            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                return Optional.of(new Company(res.getLong("id"), res.getString("name")));
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return Optional.empty();
    }

    public Optional<Company> getCompanyById(long id) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_ID_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPANY_BY_ID_QUERY);) {
            stmt.setLong(1, id);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                return Optional.of(new Company(res.getLong("id"), res.getString("name")));
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return Optional.empty();
    }

    public void deleteCompany(long id) {
        Optional<Company> optCompany = getCompanyById(id);
        if (optCompany.isPresent()) {
            ComputerDAO computerDao = new ComputerDAO();
            List<Long> idList = computerDao.getComputersByCompanyId(id);
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_COMPANY_QUERY)) {
                conn.setAutoCommit(false);
                for (long computerId : idList) {
                    computerDao.deleteComputer(computerId, conn);
                }
                stmt.setLong(1, id);
                stmt.executeUpdate();
                conn.commit();

            } catch (SQLException sqle) {
                logger.error("Error when deleting company : ", sqle);
                try {
                    conn.rollback();
                } catch (SQLException sqle2) {
                    logger.error("Error during rollback : ", sqle2);
                }
            } finally {
                try {
                    conn.close();
                } catch (SQLException sqle) {
                    logger.error("Error when closing connection : ", sqle);
                }
            }
        }

    }

    public static void main(String[] args) {
        CompanyDAO dao = new CompanyDAO();
        List<Company> l = dao.getCompaniesList();
        System.out.println(l);
    }
}
