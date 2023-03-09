package Items;

import javafx.beans.property.SimpleObjectProperty;

public class OrderRow {
    private final SimpleObjectProperty<Long> orderID;
    private final SimpleObjectProperty<String> customerName;
    private final SimpleObjectProperty<String> orderDate;
    private final SimpleObjectProperty<Double> orderTotal;
    private final SimpleObjectProperty<String> employeeName;

    public OrderRow(long orderID, String customerName, String orderDate, double orderTotal,
            String employeeName) {
        this.orderID = new SimpleObjectProperty<>(orderID);
        this.customerName = new SimpleObjectProperty<>(customerName);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.orderTotal = new SimpleObjectProperty<>(orderTotal);
        this.employeeName = new SimpleObjectProperty<>(employeeName);
    }

    public long getOrderID() {
        return orderID.get();
    }

    public SimpleObjectProperty<Long> orderIDProperty() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID.set(orderID);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleObjectProperty<String> customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public String getOrderDate() {
        return orderDate.get();
    }

    public SimpleObjectProperty<String> orderDateProperty() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate.set(orderDate);
    }

    public String getOrderTotal() {
        return String.format("%.2f", orderTotal.get());
    }

    public SimpleObjectProperty<String> orderTotalProperty() {
        SimpleObjectProperty<String> ret = new SimpleObjectProperty<String>(getOrderTotal());
        return ret;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal.set(orderTotal);
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

    @Override
    public String toString() {
        return "OrderRow{" + "orderID=" + orderID.get() + ", customerName=" + customerName.get()
                + ", orderDate=" + orderDate.get() + ", orderTotal=" + orderTotal.get()
                + ", employeeName=" + employeeName.get() + '}';
    }
}
