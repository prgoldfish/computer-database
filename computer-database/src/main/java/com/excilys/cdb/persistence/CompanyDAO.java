package com.excilys.cdb.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private static final Logger logger = LoggerFactory.getLogger(CompanyDAO.class);

    public CompanyDAO() {

    }

    /**
     * Fais une requête sur la base de données pour récupérer la liste des
     * entreprises
     * 
     * @return Les entreprises dans une List
     */
    public List<Company> getCompaniesList() {
        List<Company> compList = new ArrayList<>();
        try (DBConnection conn = DBConnection.getConnection();) {
            logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_LIST_QUERY);
            ResultSet res = conn.query(SELECT_COMPANY_LIST_QUERY);
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
        DBConnection conn = DBConnection.getConnection();
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_NAME_QUERY);
        try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPANY_BY_NAME_QUERY);) {
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
        DBConnection conn = DBConnection.getConnection();
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPANY_BY_ID_QUERY);
        try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPANY_BY_ID_QUERY);) {
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

    public static void main(String[] args) {
        CompanyDAO dao = new CompanyDAO();
        List<Company> l = dao.getCompaniesList();
        System.out.println(l);
    }
}
