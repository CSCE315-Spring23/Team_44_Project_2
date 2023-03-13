package Utils;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {
    /**
     * {@link DateTimeFormatter} to format {@link java.time.LocalDateTime}
     */
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns the last ID in a given table
     *
     * @param database {@link DatabaseConnect} to the database
     * @param table table name
     * @return the last ID in the table
     */
    public static final long getLastId(final DatabaseConnect database, final String table) {
        long ret = 0;
        final String query = String.format("SELECT MAX(id) from %s;", table);
        try {
            final ResultSet rs = database.executeQuery(query);
            if (rs.next())
                ret = rs.getLong("max");
            rs.close();
        } catch (Exception e) {
            System.out.println("Error getting last id");
            e.printStackTrace();
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
        String ret = "";
        try {
            ResultSet rs = database.executeQuery(String.format("SELECT name FROM %s WHERE id = %d",
                    DatabaseNames.EMPLOYEE_DATABASE, id));
            if (rs.next()) {
                ret = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
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
            try {
                String name;
                while (rs.next()) {
                    name = rs.getString("name");
                    if (menuItems.containsKey(name))
                        menuItems.put(name, menuItems.get(name) + 1);
                    else
                        menuItems.put(name, 1l);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        double ret = 0;
        final String query = String.format("SELECT cost FROM %s WHERE name = \'%s\'",
                DatabaseNames.MENU_ITEM_DATABASE, name);
        final ResultSet rs = database.executeQuery(query);
        try {
            if (rs.next())
                ret = rs.getDouble("cost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public DatabaseUtils() {}
}
