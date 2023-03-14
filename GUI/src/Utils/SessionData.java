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
    public final long employeeId;

    /**
     * Represents the current {@link Order} being processed
     */
    public Order order;

    /**
     * Name of the customer
     */
    public String customerName = "";

    /**
     * Constructor
     * 
     * @param database Connection to the database
     * @param employeeId id of the employee
     * @param order current order
     */
    public SessionData(final DatabaseConnect database, final long employeeId, final Order order) {
        this.database = database;
        this.employeeId = employeeId;
        this.order = order;
    }

    /**
     * Constructor
     * 
     * @param database Connection to the database
     * @param employeeId id of the employee
     * @param order current order
     * @param customerName name of the employee making the order
     */
    public SessionData(final DatabaseConnect database, final long employeeId, final Order order,
            final String customerName) {
        this.database = database;
        this.employeeId = employeeId;
        this.order = order;
        this.customerName = customerName;
    }

    /**
     * Determine if the user logging in is a manager
     * 
     * @return {@code true} if the user is a manger. {@code false} otherwise
     */
    public boolean isManager() {
        final String query = String.format("SELECT role FROM %s WHERE id=%d",
                DatabaseNames.EMPLOYEE_DATABASE, this.employeeId);
        final ResultSet rs = this.database.executeQuery(query);
        try {
            return rs.next() ? rs.getString("role").equals("manager") : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
