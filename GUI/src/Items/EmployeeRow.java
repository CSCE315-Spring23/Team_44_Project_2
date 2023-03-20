package Items;

import Utils.EmployeeRole;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Controller for the Employee List screen.
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
public class EmployeeRow {
    /**
     * {@link SimpleObjectProperty} of {@link Long} storing the identification number of the
     * Employee
     */
    private final SimpleObjectProperty<Long> randomID;

    /**
     * {@link SimpleObjectProperty} of {@link String} storing the name of the Employee
     */
    private final SimpleObjectProperty<String> employeeName;

    /**
     * {@link SimpleObjectProperty} of {@link String} storing the role of the Employee
     */
    private final SimpleObjectProperty<EmployeeRole> role;

    /**
     * {@link SimpleObjectProperty} of {@link Integer} storing the pin of the Employee
     */
    private final SimpleObjectProperty<Integer> employeePin;

    /**
     * Constructor
     * 
     * @param randomID identification number of the employee
     * @param employeeName name of the employee
     * @param role of the employee
     * @param employeePin pin of the employee
     */
    public EmployeeRow(final long randomID, final String employeeName, final EmployeeRole role,
            final int employeePin) {
        this.randomID = new SimpleObjectProperty<>(randomID);
        this.employeeName = new SimpleObjectProperty<>(employeeName);
        this.role = new SimpleObjectProperty<>(role);
        this.employeePin = new SimpleObjectProperty<>(employeePin);
    }

    /**
     * Gets value in {@link #randomID}
     * 
     * @return {@link #randomID}
     */
    public long getRandomID() {
        return this.randomID.get();
    }

    /**
     * Gets the {@link SimpleObjectProperty} of {@link #randomID}
     *
     * @return {@link #randomID}
     */
    public SimpleObjectProperty<Long> randomIDProperty() {
        return this.randomID;
    }

    /**
     * Gets value in {@link #employeeName}
     * 
     * @return {@link #employeeName}
     */
    public String getEmployeeName() {
        return this.employeeName.get();
    }

    /**
     * Gets the {@link SimpleObjectProperty} of {@link #employeeName}
     *
     * @return {@link #employeeName}
     */
    public SimpleObjectProperty<String> employeeNameProperty() {
        return this.employeeName;
    }

    /**
     * Gets the value in {@link #role}
     * 
     * @return {@link #role}
     */
    public EmployeeRole getRole() {
        return this.role.get();
    }

    /**
     * Gets the {@link SimpleObjectProperty} of {@link #role}
     *
     * @return {@link #role}
     */
    public SimpleObjectProperty<EmployeeRole> roleProperty() {
        return this.role;
    }

    /**
     * Gets the value of {@link #employeePin}
     * 
     * @return {@link #employeePin}
     */
    public int getEmployeePin() {
        return this.employeePin.get();
    }

    /**
     * Gets the {@link SimpleObjectProperty} of {@link #employeePin}
     * @return {@link #employeePin}
     */
    public SimpleObjectProperty<Integer> employeePinProperty() {
        return this.employeePin;
    }
}
