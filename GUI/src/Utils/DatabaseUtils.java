package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.StringConverter;

/**
 * Utilities for the database. Included common queries and standardizes formats
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
public class DatabaseUtils {
    /**
     * {@link DateTimeFormatter} to format {@link java.time.LocalDateTime}
     */
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * {@link DateTimeFormatter} to format {@link java.time.LocalDateTime}
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * {@link StringConverter} of {@link LocalDate} to format the date
     */
    public static final StringConverter<LocalDate> CONVERTER = new StringConverter<LocalDate>() {
        @Override
        public LocalDate fromString(final String string) {
            if (string == null)
                return null;
            if (string.isEmpty())
                return null;
            return LocalDate.parse(string, DatabaseUtils.DATE_FORMAT);
        }

        @Override
        public String toString(final LocalDate date) {
            return date == null ? new String() : date.format(DatabaseUtils.DATE_FORMAT);
        }
    };

    /**
     * Check if an item exists with in the database
     * 
     * @param database {@link DatabaseConnect} to the database
     * @param itemID identification number of the item to search for
     * @param table name of the table to query
     * @return {@code true} if the item exists. {@code false} otherwise.
     */
    public static final boolean hasItem(final DatabaseConnect database, final long itemID,
            final String table) {
        final String query =
                String.format("SELECT EXISTS(SELECT * FROM %s WHERE id=%d)", table, itemID);
        final ResultSet rs = database.executeQuery(query);
        final boolean result;
        try {
            result = rs.next() ? rs.getBoolean("exists") : false;
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }

        return result;
    }

    /**
     * Returns the last ID in a given table
     *
     * @param database {@link DatabaseConnect} to the database
     * @param table table name
     * @return the last ID in the table
     */
    public static final long getLastId(final DatabaseConnect database, final String table) {
        final String query = String.format("SELECT MAX(id) from %s;", table);
        final long ret;
        final ResultSet rs = database.executeQuery(query);
        try {
            ret = rs.next() ? rs.getLong("max") : -1l;
            rs.close();
        } catch (final SQLException e) {
            System.out.println("Error getting last id");
            e.printStackTrace();
            return -1l;
        }
        return ret;
    }

    /**
     * Gets the employee name from the database based on the employee ID
     * 
     * @param database {@link DatabaseConnect} to the database
     * @param id identification number of the employee to find
     * @return {@link String} of the employee name
     */
    public static final String getEmployeeName(final DatabaseConnect database, final long id) {
        final String name;
        final ResultSet rs = database.executeQuery(String
                .format("SELECT name FROM %s WHERE id = %d", DatabaseNames.EMPLOYEE_DATABASE, id));
        try {
            name = rs.next() ? rs.getString("name") : new String();
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
            return new String();
        }
        return name;
    }

    /**
     * Gets the menu items from the database based on Menu IDs
     * 
     * @param database {@link DatabaseConnect} to the database
     * @param menuIds {@link List} of {@link Long} holding all Menu IDs to look up
     * @return {@link HashMap} of {@link String} and {@link Long} of the menu items
     */
    public static final Map<String, Long> getMenuItems(final DatabaseConnect database,
            final List<Long> menuIds) {
        final HashMap<String, Long> menuItems = new HashMap<>();
        for (final long menuID : menuIds) {
            final String query = String.format("SELECT name FROM %s WHERE id = %d",
                    DatabaseNames.MENU_ITEM_DATABASE, menuID);
            final ResultSet rs = database.executeQuery(query);
            String name;
            try {
                while (rs.next()) {
                    name = rs.getString("name");
                    if (menuItems.containsKey(name))
                        menuItems.put(name, menuItems.get(name) + 1);
                    else
                        menuItems.put(name, 1l);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new HashMap<>();
            }
        }

        return menuItems;
    }

    /**
     * Gets the Menu IDs from the database based on the order ID
     * 
     * @param database {@link DatabaseConnect} to the database
     * @param orderID identification number of the order
     * @return {@link ArrayList} of {@link Long} of the menu IDs
     */
    public static final List<Long> getMenuId(final DatabaseConnect database, final long orderID) {
        final List<Long> menuIds = new ArrayList<>();
        final String query = String.format("SELECT menuid FROM %s WHERE orderid = %d",
                DatabaseNames.SOLD_ITEM_DATABASE, orderID);
        final ResultSet rs = database.executeQuery(query);
        try {
            while (rs.next())
                menuIds.add(rs.getLong("menuid"));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return menuIds;
    }

    /**
     * Gets the menu cost from the database based on the menu name
     * 
     * @param database {@link DatabaseConnect} to the database
     * @param name of the customer making the order
     * @return {@link Double} of the menu cost
     */
    public static final double getMenuCost(final DatabaseConnect database, final String name) {
        final String query = String.format("SELECT cost FROM %s WHERE name = \'%s\'",
                DatabaseNames.MENU_ITEM_DATABASE, name);
        final ResultSet rs = database.executeQuery(query);
        final double cost;
        try {
            cost = rs.next() ? rs.getDouble("cost") : Double.NaN;
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }

        return cost;
    }

    /**
     * Constructor
     */
    public DatabaseUtils() {}
}
