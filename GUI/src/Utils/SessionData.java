package Utils;

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
     * 
     * @see DatabaseConnect
     */
    private final DatabaseConnect database;

    /**
     * Identification number of the employee
     */
    private final int employeeId;

    /**
     * Constructor
     * 
     * @param database Connection to the database
     * @param employeeId id of the employee
     */
    SessionData(final DatabaseConnect database, final int employeeId) {
        this.database = database;
        this.employeeId = employeeId;
    }

    /**
     * Gets {@link #database}
     * 
     * @return {@link #database}
     */
    public DatabaseConnect getDatabase() {
        return this.database;
    }

    /**
     * get {@link #employeeId}
     * 
     * @return {@link #employeeId}
     */
    public int getEmployeeId() {
        return this.employeeId;
    }
}
