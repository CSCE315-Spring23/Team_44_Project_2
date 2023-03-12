package Items;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the Order Item.
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
public class Order {
    /**
     * Indentification number of the order
     */
    private long orderID;

    /**
     * {@link String} holding the name of the customer who made the order
     */
    private String customerName = "";

    /**
     * {@link LocalDateTime} holding the current date
     */
    private final LocalDateTime date;

    /**
     * Total cost of the order
     */
    private double totalCost;

    /**
     * Identification number of the employee who created the order
     */
    private long employeeId;

    /**
     * {@link HashMap} holding each item and its coresponding price.
     */
    private HashMap<String, Integer> items = new HashMap<String, Integer>();

    /**
     * Construct an Order
     */
    public Order() {
        this(-1l, -1l);
    }

    /**
     * Construct an Order
     * 
     * @param employeeId ID number of the Employee completing the order
     */
    public Order(final long employeeId) {
        this(employeeId, -1l);
    }

    /**
     * Construct an Order
     * 
     * @param employeeId ID number of the Employee completing the order
     * @param orderId ID number of the Order
     */
    public Order(final long employeeId, final long orderId) {
        this.employeeId = employeeId;
        this.orderID = orderId;
        date = LocalDateTime.now();
        System.out.println("Order Created on "
                + this.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Add an item to the order
     * 
     * @param name of the item to add as a {@link String}
     * @param price of the item
     */
    public void addItem(final String name, final double price) {
        items.put(name, items.containsKey(name) ? items.get(name) + 1 : 1);
        this.totalCost += price;
    }

    /**
     * Removes an item from the order
     * 
     * @param name of the item to remove as a {@link String}
     * @param price of the item
     */
    public void removeItem(String name, double price) {
        if (items.containsKey(name)) {
            if (items.get(name) == 1) {
                items.remove(name);
            } else {
                items.put(name, items.get(name) - 1);
            }
        }

        this.totalCost -= price;
    }

    /**
     * Sets {@link #customerName}
     * 
     * @param name new {@link #customerName}
     */
    public void setCustomerName(final String name) {
        this.customerName = name;
    }

    /**
     * Gets {@link #customerName}
     * 
     * @return {@link #customerName}
     */
    public String getCustomerName() {
        return this.customerName;
    }

    /**
     * Gets {@link #employeeId}
     * 
     * @return {@link #employeeId}
     */
    public long getEmployeeId() {
        return this.employeeId;
    }

    /**
     * Sets {@link #orderID}
     * 
     * @param orderID new identification number of the order
     */
    public void setOrderId(final long orderID) {
        this.orderID = orderID;
    }

    /**
     * Gets {@link #orderID}
     * 
     * @return {@link #orderID}
     */
    public long getOrderID() {
        return this.orderID;
    }

    /**
     * Gets {@link #date}
     * 
     * @return {@link #date}
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Gets {@link #totalCost}
     * 
     * @return {@link #totalCost}
     */
    public double getTotalCost() {
        return this.totalCost;
    }

    /**
     * Gets the number of an item within {@link #items}
     * 
     * @param name of the item as a {@link String}
     * @return amount of the item
     */
    public int getItemCount(String name) {
        return items.containsKey(name) ? items.get(name) : 0;
    }

    /**
     * Update GUI to current item count
     * 
     * @return updated count as {@link String}
     */
    public String getItemCount() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            sb.append(entry.getKey() + " x" + entry.getValue() + '\n');
        }
        return sb.toString();
    }

    /**
     * Gets {@link #items}
     * 
     * @return {@link #items}
     */
    public HashMap<String, Integer> getItems() {
        return this.items;
    }
}
