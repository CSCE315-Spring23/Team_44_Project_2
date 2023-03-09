package Utils;

import Items.Order;

import java.sql.*;

/**
 * Bundle of Information that gets passed between scenes
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
public class SessionData {
    /**
     * Connection to the database
     */
    public final DatabaseConnect database;

    /**
     * Identification number of the employee
     */
    public final int employeeId;

    /**
     * Represents the current {@link Order} being processed
     */
    public Order order;

    /**
     * Constructor
     * 
     * @param database Connection to the database
     * @param employeeId id of the employee
     * @param order current order
     */
    public SessionData(final DatabaseConnect database, int employeeId, Order order) {
        this.database = database;
        this.employeeId = employeeId;
        this.order = order;
    }

    /**
     * Determine if the user logging in is a manager
     * 
     * @return {@code true} if the user is a manger. {@code false} otherwise
     */
    public boolean isManager() {
        final String query = String.format("SELECT role FROM %s WHERE id=%d",
                DatabaseNames.EMPLOYEE_DATABASE, this.employeeId);
        try {
            final ResultSet rs = this.database.executeQuery(query);
            rs.next();
            return rs.getString("role").equals("manager");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
