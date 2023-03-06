package Utils;

// Bundle of Information that gets passed between scenes
public class SessionData {
    private DatabaseConnect database;
    private int employeeId;

    SessionData(DatabaseConnect database, int employeeId) {
        this.database = database;
        this.employeeId = employeeId;
    }

    public DatabaseConnect getDatabase() {
        return database;
    }

    public int getEmployeeId() {
        return employeeId;
    }
}
