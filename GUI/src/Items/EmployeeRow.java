package Items;

import javafx.beans.property.SimpleObjectProperty;

public class EmployeeRow {
    private final SimpleObjectProperty<Integer> randomID;
    private final SimpleObjectProperty<String> employeeName;
    private final SimpleObjectProperty<String> role;
    private final SimpleObjectProperty<Integer> employeePin;

    public EmployeeRow(Integer randomID, String employeeName, String role, Integer employeePin) {
        this.randomID = new SimpleObjectProperty<>(randomID);
        this.employeeName = new SimpleObjectProperty<>(employeeName);
        this.role = new SimpleObjectProperty<>(role);
        this.employeePin = new SimpleObjectProperty<>(employeePin);
    }

    public Integer getRandomID() {
        return randomID.get();
    }

    public SimpleObjectProperty<Integer> randomIDProperty() {
        return randomID;
    }

    public void setOrderID(Integer randomID) {
        this.randomID.set(randomID);
    }

    public String getEmployeeName() {
        return employeeName.get();
    }

    public SimpleObjectProperty<String> employeeNameProperty() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName.set(employeeName);
    }

    public String getRole() {
        return role.get();
    }

    public SimpleObjectProperty<String> roleProperty() {
        return role;
    }

    public void setOrderDate(String role) {
        this.role.set(role);
    }

    public Integer getEmployeePin() {
        return employeePin.get();
    }

    public SimpleObjectProperty<Integer> employeePinProperty() {
        return employeePin;
    }

    public void setEmployeePin(Integer employeePin) {
        this.employeePin.set(employeePin);
    }

}
