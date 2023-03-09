package Utils;

/**
 * Holds information to connect to the database
 */
public final class DatabaseLoginInfo {
    /**
     * This will establish a connection to the dabase
     */
    public static final String dbConnectionString =
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_team_44";

    /**
     * The user name credentials
     */
    public static final String username = "csce315331_team_44_master";

    /**
     * The password credentials
     */
    public static final String password = "ShreemanLikesDeepWork";

    /**
     * Constructor
     */
    public DatabaseLoginInfo() {}
}
