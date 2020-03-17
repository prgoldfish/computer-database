package com.excilys.cdb.persistence;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.Month;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.cdb.model.Company;
import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

public class ComputerDAO {

	private static final String SELECT_COMPUTER_LIST_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id LIMIT ? OFFSET ?";
	private static final String SELECT_COMPUTER_BY_ID_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id WHERE computer.id=?";
	private static final String SELECT_COMPUTER_BY_NAME_QUERY = "SELECT computer.id, computer.name as computerName, introduced, discontinued, company_id, company.name as companyName FROM computer LEFT JOIN company ON computer.company_id = company.id WHERE computer.name=?";
	private static final String ADD_COMPUTER_QUERY = "INSERT INTO computer VALUES (?,?,?,?,?)";
	private static final String GET_MAX_ID_QUERY = "SELECT MAX(id) as idMax FROM computer";
	private static final String UPDATE_COMPUTER_QUERY = "UPDATE computer SET introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
	private static final String DELETE_COMPUTER_QUERY = "DELETE FROM computer WHERE id = ?";
	private static final Logger logger = LoggerFactory.getLogger(ComputerDAO.class);

	
	/**
	 * Fais une requête sur la base de données pour récupérer la liste des ordinateurs
	 * @return Les ordinateurs dans une List
	 */
	public static List<Computer> getComputerList(long startIndex, long limit)
	{
		DBConnection conn = DBConnection.getConnection();
		ResultSet res = null;
		List<Computer> compList = new ArrayList<>();
		logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_LIST_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPUTER_LIST_QUERY)) {
			stmt.setLong(1, limit);
			stmt.setLong(2, startIndex);
			res = stmt.executeQuery();
			while(res.next())
			{
				ComputerBuilder c = processGetComputerResults(res);
				compList.add(c.build());
			}
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
		return compList;
	}
	
	public static long getMaxId()
	{
		try (DBConnection conn = DBConnection.getConnection();)
		{
			logger.info("Exécution de la requête \"{}\"", GET_MAX_ID_QUERY);
			ResultSet res = conn.query(GET_MAX_ID_QUERY);
			if(res.next())
			{
				return res.getLong("idMax");
			}
			return 0; //On présume que la table est vide
			
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
		return 0;
	}
	
	public static Optional<Computer> getComputerById(long id) 
	{
		DBConnection conn = DBConnection.getConnection();
		logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_ID_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPUTER_BY_ID_QUERY);){
			stmt.setLong(1, id);
			ResultSet res = stmt.executeQuery();
			if(res.next())
			{
				ComputerBuilder c = processGetComputerResults(res);
				return Optional.of(c.build());
			}
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
		return Optional.empty();
	}
	
	public static Optional<Computer> getComputerByName(String name) 
	{
		DBConnection conn = DBConnection.getConnection();
		logger.info("Exécution de la requête \"{}\"", SELECT_COMPUTER_BY_NAME_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPUTER_BY_NAME_QUERY);) {
			stmt.setString(1, name);
			ResultSet res = stmt.executeQuery();
			if(res.next())
			{
				ComputerBuilder c = processGetComputerResults(res);
				return Optional.of(c.build());
			}
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
		return Optional.empty();
	}

	private static ComputerBuilder processGetComputerResults(ResultSet res) throws SQLException {
		Timestamp intro = res.getTimestamp("introduced");
		Timestamp discont = res.getTimestamp("discontinued");
		int idEntreprise = res.getInt("company_id");
		ComputerBuilder c = new Computer.ComputerBuilder(res.getInt("computer.id"), res.getString("computerName"));
		if(intro != null)
		{
			c.setDateIntroduction(intro.toLocalDateTime());					
		}
		if(discont != null)
		{
			c.setDateDiscontinuation(discont.toLocalDateTime());
		}
		if (idEntreprise != 0)
		{
			String companyName = res.getString("companyName");
			c.setEntreprise(new Company(idEntreprise, companyName));
		}
		return c;
	}
	
	public static void addComputer(Computer c)
	{
		DBConnection conn = DBConnection.getConnection();
		LocalDateTime intro = c.getDateIntroduction();
		LocalDateTime discont = c.getDateDiscontinuation();
		Company entreprise = c.getEntreprise();
		Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
		Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
		executeAddComputerQuery(c, conn, entreprise, introTimestamp, discontTimestamp);
	}

	private static void executeAddComputerQuery(Computer c, DBConnection conn, Company entreprise, Timestamp introTimestamp, Timestamp discontTimestamp) {
		logger.info("Exécution de la requête \"{}\"", ADD_COMPUTER_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(ADD_COMPUTER_QUERY);) {
			stmt.setLong(1, c.getId());
			stmt.setString(2, c.getNom());
			stmt.setTimestamp(3, introTimestamp);
			stmt.setTimestamp(4, discontTimestamp);
			if(entreprise == null)
			{
				stmt.setNull(5, Types.BIGINT);
			}
			else
			{
				stmt.setLong(5, entreprise.getId());
			}
			stmt.executeUpdate();
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
	}
	
	public static void updateComputer(Computer c)
	{
		DBConnection conn = DBConnection.getConnection();
		LocalDateTime intro = c.getDateIntroduction();
		LocalDateTime discont = c.getDateDiscontinuation();
		Company entreprise = c.getEntreprise();
		Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
		Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
		executeUpdateComputerQuery(c, conn, entreprise, introTimestamp, discontTimestamp);
	}

	private static void executeUpdateComputerQuery(Computer c, DBConnection conn, Company entreprise, Timestamp introTimestamp, Timestamp discontTimestamp) {
		logger.info("Exécution de la requête \"{}\"", UPDATE_COMPUTER_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(UPDATE_COMPUTER_QUERY);) {
			stmt.setTimestamp(1, introTimestamp);
			stmt.setTimestamp(2, discontTimestamp);
			if(entreprise == null)
			{
				stmt.setNull(3, Types.BIGINT);
			}
			else
			{
				stmt.setLong(3, entreprise.getId());
			}			
			stmt.setLong(4, c.getId());
			stmt.executeUpdate();
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
	}
	
	public static void deleteComputer(long id)
	{
		DBConnection conn = DBConnection.getConnection();
		logger.info("Exécution de la requête \"{}\"", DELETE_COMPUTER_QUERY);
		try (PreparedStatement stmt = conn.prepareStement(DELETE_COMPUTER_QUERY);) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException sqle) {
			logger.error("Erreur lors de l'exécution de la requête", sqle);
		}
	}

	private static void testDAO() {
		LocalDateTime d1 = LocalDateTime.of(1996, Month.SEPTEMBER, 23, 20, 31);
		LocalDateTime d2 = LocalDateTime.now();
		Computer c = new Computer.ComputerBuilder(getMaxId() + 1, "SuperPc").setDateIntroduction(d1).build();
		addComputer(c);
		System.out.println(getComputerById(c.getId()));
		c.setDateDiscontinuation(d2);
		c.setEntreprise(new Company(8, "BBB"));
		updateComputer(c);
		System.out.println(getComputerById(c.getId()));
		deleteComputer(c.getId());
		System.out.println(getComputerById(c.getId()));
	}
	
	public static void main(String[] args) {
		List<Computer> l = getComputerList(0, 10000);
		System.out.println(l);
		System.out.println(getComputerById(494));
		System.out.println(getComputerByName("Apple II"));
		System.out.println(getMaxId());
		testDAO();
	}
}
