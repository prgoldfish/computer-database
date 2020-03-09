package com.excilys.cdb.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.excilys.cdb.model.Computer;
import com.excilys.cdb.model.Computer.ComputerBuilder;

public class ComputerDAO {

	private static final String SELECT_COMPUTER_LIST_QUERY = "SELECT id, name, introduced, discontinued, company_id FROM computer";
	private static final String SELECT_COMPUTER_BY_ID_QUERY = "SELECT id, name, introduced, discontinued, company_id FROM computer WHERE id=?";
	private static final String SELECT_COMPUTER_BY_NAME_QUERY = "SELECT id, name, introduced, discontinued, company_id FROM computer WHERE name=?";
	private static final String ADD_COMPUTER_QUERY = "INSERT INTO computer VALUES (?,?,?,?,?)";
	private static final String GET_MAX_ID_QUERY = "SELECT MAX(id) as idMax FROM computer";
	private static final String UPDATE_COMPUTER_QUERY = "UPDATE computer SET introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
	private static final String DELETE_COMPUTER_QUERY = "DELETE FROM computer WHERE id = ?";

	
	/**
	 * Fais une requête sur la base de données pour récupérer la liste des ordinateurs
	 * @return Les ordinateurs dans une List
	 */
	public static List<Computer> getComputerList()
	{
		ResultSet res = null;
		List<Computer> compList = new ArrayList<Computer>();
		try (DBConnection conn = DBConnection.getConnection();) {
			res = conn.query(SELECT_COMPUTER_LIST_QUERY);
			while(res.next())
			{
				Timestamp intro = res.getTimestamp("introduced");
				Timestamp discont = res.getTimestamp("discontinued");
				int idEntreprise = res.getInt("company_id");
				ComputerBuilder c = new Computer.ComputerBuilder(res.getInt("id"), res.getString("name"));
				if(intro != null)
				{
					c.setDateIntroduction(intro.toLocalDateTime());					
				}
				if(discont != null)
				{
					c.setDateIntroduction(discont.toLocalDateTime());
				}
				if (idEntreprise != 0)
				{
					c.setIdEntreprise(idEntreprise);
				}
				compList.add(c.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return compList;
	}
	
	public static int getMaxId()
	{
		try (DBConnection conn = DBConnection.getConnection();)
		{
			ResultSet res = conn.query(GET_MAX_ID_QUERY);
			if(res.next())
			{
				return res.getInt("idMax");
			}
			return 0; //On présume que la table est vide
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Optional<Computer> getComputerById(int id) 
	{
		DBConnection conn = DBConnection.getConnection();
		try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPUTER_BY_ID_QUERY);){
			stmt.setInt(1, id);
			ResultSet res = stmt.executeQuery();
			if(res.next())
			{
				Timestamp intro = res.getTimestamp("introduced");
				Timestamp discont = res.getTimestamp("discontinued");
				int idEntreprise = res.getInt("company_id");
				ComputerBuilder c = new Computer.ComputerBuilder(res.getInt("id"), res.getString("name"));
				if(intro != null)
				{
					c.setDateIntroduction(intro.toLocalDateTime());					
				}
				if(discont != null)
				{
					c.setDateIntroduction(discont.toLocalDateTime());
				}
				if (idEntreprise != 0)
				{
					c.setIdEntreprise(idEntreprise);
				}
				return Optional.of(c.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public static Optional<Computer> getComputerByName(String name) 
	{
		DBConnection conn = DBConnection.getConnection();
		try (PreparedStatement stmt = conn.prepareStement(SELECT_COMPUTER_BY_NAME_QUERY);) {
			stmt.setString(1, name);
			ResultSet res = stmt.executeQuery();
			if(res.next())
			{
				Timestamp intro = res.getTimestamp("introduced");
				Timestamp discont = res.getTimestamp("discontinued");
				int idEntreprise = res.getInt("company_id");
				ComputerBuilder c = new Computer.ComputerBuilder(res.getInt("id"), res.getString("name"));
				if(intro != null)
				{
					c.setDateIntroduction(intro.toLocalDateTime());					
				}
				if(discont != null)
				{
					c.setDateIntroduction(discont.toLocalDateTime());
				}
				if (idEntreprise != 0)
				{
					c.setIdEntreprise(idEntreprise);
				}
				return Optional.of(c.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	public static void addComputer(Computer c)
	{
		DBConnection conn = DBConnection.getConnection();
		LocalDateTime intro = c.getDateIntroduction();
		LocalDateTime discont = c.getDateDiscontinuation();
		Integer idEntreprise = c.getIdEntreprise();
		Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
		Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
		int idEntrepriseInt = idEntreprise == null ? 0 : idEntreprise;
		try (PreparedStatement stmt = conn.prepareStement(ADD_COMPUTER_QUERY);) {
			stmt.setInt(1, c.getId());
			stmt.setString(2, c.getNom());
			stmt.setTimestamp(3, introTimestamp);
			stmt.setTimestamp(4, discontTimestamp);
			stmt.setInt(5, idEntrepriseInt);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateComputer(Computer c)
	{
		DBConnection conn = DBConnection.getConnection();
		LocalDateTime intro = c.getDateIntroduction();
		LocalDateTime discont = c.getDateDiscontinuation();
		Integer idEntreprise = c.getIdEntreprise();
		Timestamp introTimestamp = intro == null ? null : Timestamp.valueOf(intro);
		Timestamp discontTimestamp = discont == null ? null : Timestamp.valueOf(discont);
		int idEntrepriseInt = idEntreprise == null ? 0 : idEntreprise;
		try (PreparedStatement stmt = conn.prepareStement(UPDATE_COMPUTER_QUERY);) {
			stmt.setTimestamp(1, introTimestamp);
			stmt.setTimestamp(2, discontTimestamp);
			stmt.setInt(3, idEntrepriseInt);
			stmt.setInt(4, c.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteComputer(int id)
	{
		DBConnection conn = DBConnection.getConnection();
		try (PreparedStatement stmt = conn.prepareStement(DELETE_COMPUTER_QUERY);) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		List<Computer> l = getComputerList();
		System.out.println(l);
		System.out.println(getComputerById(494));
		System.out.println(getComputerByName("Apple II"));
		System.out.println(getMaxId());
		LocalDateTime d1 = LocalDateTime.of(1996, Month.SEPTEMBER, 23, 20, 31);
		LocalDateTime d2 = LocalDateTime.now();
		Computer c = new Computer.ComputerBuilder(getMaxId() + 1, "PC de la mort qui tue").setDateIntroduction(d1).setDateDiscontinuation(d2).setIdEntreprise(7).build();
		addComputer(c);
		System.out.println(getComputerById(c.getId()));
		c.setIdEntreprise(8);
		updateComputer(c);
		System.out.println(getComputerById(c.getId()));
		deleteComputer(c.getId());
		System.out.println(getComputerById(c.getId()));
	}
}
