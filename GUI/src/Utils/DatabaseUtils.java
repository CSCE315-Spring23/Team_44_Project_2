package Utils;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class DatabaseUtils {
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

    public DatabaseUtils() {}
}
