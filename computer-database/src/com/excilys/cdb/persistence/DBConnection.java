package com.excilys.cdb.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection implements AutoCloseable {
	
	private static DBConnection instance;
	private Connection conn;
	private Statement stmt;
	
	private DBConnection() {
		String url = "jdbc:mysql://localhost:3306/";
		String db = "computer-database-db";
		String user = "admincdb";
		String pass = "qwerty1234";
		
		stmt = null;
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
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
	 * @param query La requête SQLà exécuter
	 * @return Le resultat de la requête sous la forme d'un ResultSet
	 * @throws SQLException
	 */
	public ResultSet query(String query) throws SQLException
	{
		stmt = conn.createStatement();
		return stmt.executeQuery(query);
	}
	
	/**
	 * 
	 * @param query La requête SQL à exécuter
	 * @return Le nombre de lignes modifiées
	 * @throws SQLException
	 */
	public int insert(String insertQuery) throws SQLException
	{
		stmt = conn.createStatement();
		return stmt.executeUpdate(insertQuery);
	}
	
	public PreparedStatement prepareStement(String query) throws SQLException
	{
		return conn.prepareStatement(query);
	}
	
	
	public static void main(String[] args) {
		System.err.println("Erreur 1");
		System.err.println("Erreur 2");
	}

	/**
	 * Libère les ressources pour l'objet Statement
	 * @throws SQLException
	 */
	@Override
	public void close() throws SQLException {
		if(stmt != null)
		{
			stmt.close();
		}
		
	}

}
