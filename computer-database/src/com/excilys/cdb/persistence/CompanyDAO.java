package com.excilys.cdb.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.excilys.cdb.model.Company;

public class CompanyDAO {
	
	private static final String SELECT_COMPUTER_LIST_QUERY = "SELECT id, name FROM company";
	
	/**
	 * Fais une requête sur la base de données pour récupérer la liste des entreprises
	 * @return Les entreprises dans une List
	 */
	public static List<Company> getCompaniesList()
	{
		List<Company> compList = new ArrayList<Company>();
		try (DBConnection conn = DBConnection.getConnection();) {
			ResultSet res = conn.query(SELECT_COMPUTER_LIST_QUERY);
			while(res.next())
			{
				Company c = new Company(res.getInt("id"), res.getString("name"));
				compList.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return compList;
	}
	
	public static void main(String[] args) {
		List<Company> l = getCompaniesList();
		System.out.println(l);
	}
}
