package Items;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Handle displaying the {@link Order} in the GUI
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
public class OrderRow {
    /**
     * {@link SimpleObjectProperty} of {@link Long} holding the identification number of the order.
     */
    private final SimpleObjectProperty<Long> orderID;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the customer making the
     * order.
     */
    private final SimpleObjectProperty<String> customerName;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the date of when the order was made.
     */
    private final SimpleObjectProperty<String> orderDate;

    /**
     * {@link SimpleObjectProperty} of {@link Double} holding the total price of the order.
     */
    private final SimpleObjectProperty<Double> orderTotal;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the employee completing
     * the order
     */
    private final SimpleObjectProperty<String> employeeName;

    /**
     * Constructor
     * 
     * @param orderID id number of the order
     * @param customerName name of the customer
     * @param orderDate date the order was made
     * @param orderTotal total cost of the order
     * @param employeeName name of the employee completing the order
     */
    public OrderRow(final long orderID, final String customerName, final String orderDate,
            final double orderTotal, final String employeeName) {
        this.orderID = new SimpleObjectProperty<>(orderID);
        this.customerName = new SimpleObjectProperty<>(customerName);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.orderTotal = new SimpleObjectProperty<>(orderTotal);
        this.employeeName = new SimpleObjectProperty<>(employeeName);
    }

    /**
     * Gets {@link #orderID}
     * 
     * @return {@link #orderID}
     */
    public long getOrderID() {
        return this.orderID.get();
    }

    /**
     * Gets {@link #orderID}
     * @return {@link #orderID}
     */
    public SimpleObjectProperty<Long> orderIDProperty() {
        return orderID;
    }

    /**
     * Gets {@link #customerName}
     * 
     * @return {@link #customerName}
     */
    public String getCustomerName() {
        return this.customerName.get();
    }

    /**
     * Gets {@link #customerName}
     * @return {@link #customerName}
     */
    public SimpleObjectProperty<String> customerNameProperty() {
        return customerName;
    }

    /**
     * Gets {@link #orderDate}
     * 
     * @return {@link #orderDate}
     */
    public String getOrderDate() {
        return this.orderDate.get();
    }

    /**
     * Gets {@link #orderDate}
     * @return {@link #orderDate}
     */
    public SimpleObjectProperty<String> orderDateProperty() {
        return this.orderDate;
    }

    /**
     * Gets {@link #orderTotal}
     * 
     * @return {@link #orderTotal}
     */
    public String getOrderTotal() {
        return String.format("%.2f", this.orderTotal.get());
    }

    /**
     * Gets {@link #orderTotal}
     * @return {@link #orderTotal}
     */
    public SimpleObjectProperty<String> orderTotalProperty() {
        return new SimpleObjectProperty<>(this.getOrderTotal());
    }

    /**
     * Gets {@link #employeeName}
     * 
     * @return {@link #employeeName}
     */
    public String getEmployeeName() {
        return this.employeeName.get();
    }

    /**
     * Gets {@link #employeeName}
     * @return {@link #employeeName}
     */
    public SimpleObjectProperty<String> employeeNameProperty() {
        return this.employeeName;
    }
}
