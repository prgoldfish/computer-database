package com.excilys.cdb.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

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

    public ComputerDAO() {

    }

    /**
     * Fais une requête sur la base de données pour récupérer la liste des
     * ordinateurs
     *
     * @return Les ordinateurs dans une List
     */
    public List<Computer> getComputerList(long startIndex, long limit, OrderByColumn orderBy, boolean ascendentOrder) {
        ResultSet res = null;
        List<Computer> compList = new ArrayList<>();
        String request = SELECT_COMPUTER_LIST_QUERY + orderBy.getColumnName() + (ascendentOrder ? " ASC" : " DESC")
                + LIMIT_OFFSET;
        logger.info("Exécution de la requête \"{}\"", request);
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(request)) {
            stmt.setLong(1, limit);
            stmt.setLong(2, startIndex);
            res = stmt.executeQuery();
            while (res.next()) {
                ComputerBuilder c = processGetComputerResults(res);
                compList.add(c.build());
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return compList;
    }

    public List<Long> getComputersByCompanyId(long companyId) {
        ResultSet res = null;
        List<Long> idList = new ArrayList<Long>();
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_COMPANY_ID_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPUTER_BY_COMPANY_ID_QUERY)) {
            stmt.setLong(1, companyId);
            res = stmt.executeQuery();
            while (res.next()) {
                idList.add(res.getLong("id"));
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return idList;
    }

    public long getMaxId() {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement();) {
            logger.info("Exécution de la requête \"{}\"", GET_MAX_ID_QUERY);
            ResultSet res = stmt.executeQuery(GET_MAX_ID_QUERY);
            if (res.next()) {
                return res.getLong("idMax");
            }
            return 0; // On présume que la table est vide

        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return 0;
    }

    public Optional<Computer> getComputerById(long id) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_ID_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPUTER_BY_ID_QUERY);) {
            stmt.setLong(1, id);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                ComputerBuilder c = processGetComputerResults(res);
                return Optional.of(c.build());
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return Optional.empty();
    }

    public Optional<Computer> getComputerByName(String name) {
        logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_NAME_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPUTER_BY_NAME_QUERY);) {
            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                ComputerBuilder c = processGetComputerResults(res);
                return Optional.of(c.build());
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return Optional.empty();
    }

    public List<Computer> searchComputersByName(String name, OrderByColumn orderBy, boolean ascendentOrder) {
        String request = SEARCH_COMPUTERS_BY_NAME_OR_COMPANY_QUERY + orderBy.getColumnName()
                + (ascendentOrder ? " ASC" : " DESC");
        logger.info("Exécution de la requête \"{}\"", request);
        List<Computer> resultList = new ArrayList<Computer>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(request);) {
            stmt.setString(1, "%" + name + "%");
            stmt.setString(2, "%" + name + "%");
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                ComputerBuilder c = processGetComputerResults(res);
                resultList.add(c.build());
            }
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
        return resultList;
    }

    private ComputerBuilder processGetComputerResults(ResultSet res) throws SQLException {
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
        return c;
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
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(ADD_COMPUTER_QUERY);) {
            stmt.setLong(1, c.getId());
            stmt.setString(2, c.getNom());
            stmt.setTimestamp(3, introTimestamp);
            stmt.setTimestamp(4, discontTimestamp);
            if (entreprise == null) {
                stmt.setNull(5, Types.BIGINT);
            } else {
                stmt.setLong(5, entreprise.getId());
            }
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
    }

    public void updateComputer(Computer c) {
        String nom = c.getNom();
        LocalDateTime intro = c.getDateIntroduction();
        LocalDateTime discont = c.getDateDiscontinuation();
        Company entreprise = c.getEntreprise();
        Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
        Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
        executeUpdateComputerQuery(c, nom, entreprise, introTimestamp, discontTimestamp);
    }

    private void executeUpdateComputerQuery(Computer c, String nom, Company entreprise, Timestamp introTimestamp, Timestamp discontTimestamp) {
        logger.info("Exécution de la requête \"{}\"", UPDATE_COMPUTER_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_COMPUTER_QUERY);) {
            stmt.setString(1, nom);
            stmt.setTimestamp(2, introTimestamp);
            stmt.setTimestamp(3, discontTimestamp);
            if (entreprise == null) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, entreprise.getId());
            }
            stmt.setLong(5, c.getId());
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
    }

    public void deleteComputer(long id) {
        logger.info("Exécution de la requête \"{}\"", DELETE_COMPUTER_QUERY);
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_COMPUTER_QUERY);) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            logger.info("Deleted computer with id : {}", id);
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
    }

    public void deleteComputer(long id, Connection conn) {
        logger.info("Exécution de la requête \"{}\"", DELETE_COMPUTER_QUERY);
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_COMPUTER_QUERY);) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
            logger.info("Deleted computer with id : {}", id);
        } catch (SQLException sqle) {
            logger.error("Erreur lors de l'exécution de la requête", sqle);
        }
    }
}
