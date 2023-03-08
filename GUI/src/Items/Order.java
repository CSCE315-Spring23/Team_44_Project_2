package Items;

import java.time.LocalDate;
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
    private int orderId;

    /**
     * {@link String} holding the name of the customer who made the order
     */
    private String customerName = "";

    /**
     * {@link LocalDate} holding the current date
     */
    private final LocalDate date;

    /**
     * Total cost of the order
     */
    private double totalCost;

    /**
     * Identification number of the employee who created the order
     */
    private int employeeId;

    /**
     * {@link HashMap} holding each item and its coresponding price.
     */
    private HashMap<String, Integer> items = new HashMap<String, Integer>();

    /**
     * Construct an Order
     * 
     * @param employeeId
     */
    public Order(final int employeeId) {
        this.employeeId = employeeId;
        this.orderId = -1;
        date = LocalDate.now();
        System.out.println("Order Created:\tcurrent date: " + date + " order id: " + orderId);
    }

    /**
     * Construct an Order
     * 
     * @param employeeId
     * @param orderId
     */
    public Order(final int employeeId, final int orderId) {
        this.employeeId = employeeId;
        this.orderId = orderId;
        date = LocalDate.now();
        System.out.println("Order Created:\tcurrent date: " + date + " order id: " + orderId);
    }

    /**
     * Add an item to the order
     * 
     * @param name  of the item to add as a {@link String}
     * @param price of the item
     */
    public void addItem(final String name, final double price) {
        items.put(name, items.containsKey(name) ? items.get(name) + 1 : 1);
        this.totalCost += price;
    }

    /**
     * Removes an item from the order
     * 
     * @param name  of the item to remove as a {@link String}
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
     * @return
     */
    public int getEmployeeId() {
        return this.employeeId;
    }

    /**
     * Sets {@link #orderId}
     * 
     * @return {@link #orderId}
     */
    public void setOrderId(final int orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets {@link #orderId}
     * 
     * @return {@link #orderId}
     */
    public int getOrderId() {
        return this.orderId;
    }

    /**
     * Gets {@link #date}
     * 
     * @return {@link #date}
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Gets {@link #totalCost}
     * 
     * @return
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
