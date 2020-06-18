package com.excilys.cdb.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.excilys.cdb.CDBConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection implements AutoCloseable {

    private static DBConnection instance;
    private HikariDataSource ds;
    private Connection conn;
    private Statement stmt;
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    /**
     * Se connecte à la base de données et stocke la connexion
     */
    private DBConnection() {
        stmt = null;
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CDBConfig.class);) {
            ds = context.getBean(HikariDataSource.class);
            this.conn = ds.getConnection();
        } catch (SQLException sqle) {
            logger.error("Erreur de connexion à la base de données", sqle);
            System.exit(-1);
        }
    }

    /**
     *
     * @return Renvoie l'unique objet de la connection vers la BDD
     */
    public static Connection getConnection() {
        if (instance == null) {
            instance = new DBConnection();
        }
        try {
            if (instance.conn == null || instance.conn.isClosed()) {
                instance.conn = instance.ds.getConnection();
            }
        } catch (SQLException sqle) {
            logger.error("Erreur de connexion à la base de données", sqle);
            System.exit(-1);
        }
        return instance.conn;
    }

    /**
     *
     * @param query La requête SQL à exécuter
     * @return Le resultat de la requête sous la forme d'un ResultSet
     * @throws SQLException
     */
    public ResultSet query(String query) throws SQLException {
        stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     *
     * @param query La requête SQL à exécuter
     * @return Le nombre de lignes modifiées
     * @throws SQLException
     */
    public int insert(String insertQuery) throws SQLException {
        stmt = conn.createStatement();
        return stmt.executeUpdate(insertQuery);
    }

    /**
     * Crée une PreparedStement sur l'objet conn et le renvoie
     *
     * @param query la requête à executer
     * @return Une preparedStatement de l'objet conn
     * @throws SQLException
     */
    public PreparedStatement prepareStement(String query) throws SQLException {
        return conn.prepareStatement(query);
    }

    /**
     * Libère les ressources pour l'objet Statement et DataSource
     *
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        if (stmt != null && !stmt.isClosed()) {
            stmt.close();
        }
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }

    }

}
