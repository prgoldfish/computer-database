package com.excilys.cdb.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	
	private static DBConnection instance;
	private Connection conn;
	
	private DBConnection() {
		String url = "jdbc:mysql://localhost:3306/";
		String db = "computer-database-db";
		String user = "admincdb";
		String pass = "qwerty1234";
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.conn = (Connection) DriverManager.getConnection(url + db , user, pass);
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * 
	 * @return Renvoie l'unique objet de la connection vers la BDD
	 */
	public static DBConnection getConnection()
	{
		if(instance == null)
		{
			instance = new DBConnection();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param query La requête SQL
	 * @return
	 * @throws SQLException
	 */
	public ResultSet query(String query) throws SQLException
	{
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(query);
	}
	
	public int insert(String insertQuery) throws SQLException
	{
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(insertQuery);
	}
	
	public static void main(String[] args) {
		System.err.println("Erreur 1");
		System.err.println("Erreur 2");
	}

}
