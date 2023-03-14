package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Items.Order;

/**
 * This class establishes a connection to the database and performs queries.<br>
 * Additionally, it will update the Inventory automatically based on {@link Order}.
 * 
 * @since 2023-03-07
 * @version 2023-03-07
 * 
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 */
public class DatabaseConnect {
    /**
     * {@link Connection} to the server
     */
    private Connection conn;

    /**
     * {@link String} that connects to the server
     */
    private final String dbConnectionString;

    /**
     * {@link String} user name credentials
     */
    private final String username;

    /**
     * {@link String} password credentials
     */
    private final String password;

    /**
     * Construct a connection to the database
     * 
     * @param dbConnectionString connection String
     * @param username user name credentials
     * @param password password credentials
     */
    public DatabaseConnect(final String dbConnectionString, final String username,
            final String password) {
        this.dbConnectionString = dbConnectionString;
        this.username = username;
        this.password = password;
    }

    /**
     * Initialize {@link #conn} and established a connection to the database.
     */
    public void setUpDatabase() {
        try {
            this.conn = DriverManager.getConnection(dbConnectionString, username, password);
        } catch (final SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    /**
     * Execute a SQL query. This method will NOT handle UPDATE queries. Use
     * {@link #executeUpdate(String)} instead.
     * 
     * @param command query to send to database
     * @return the {@link ResultSet} of the query
     */
    public ResultSet executeQuery(final String command) {
        final Statement stmt;
        final ResultSet rs;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(command);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return rs;
    }

    /**
     * Handles the UPDATE, INSERT, and DELETE SQL query.
     * 
     * @param command update to send to the database
     * @return the result of the query
     */
    public int executeUpdate(final String command) {
        final Statement stmt;
        final int rs;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeUpdate(command);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return rs;
    }
}
