package Items;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import Utils.DatabaseUtils;

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
     * {@link Map} holding each item's name as a key and its coresponding quantity as value.
     */
    private Map<String, Long> items = new HashMap<>();

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
        this.date = LocalDateTime.now();
        final String debug = String.format("Order created on %s with ID %d",
                this.date.format(DatabaseUtils.DATE_TIME_FORMAT), this.orderID);
        System.out.println(debug);
    }

    /**
     * Add an item to the order
     * 
     * @param name of the item to add as a {@link String}
     * @param price of the item
     */
    public void addItem(final String name, final double price) {
        this.items.put(name, items.containsKey(name) ? items.get(name) + 1l : 1l);
        this.totalCost += price;
    }

    /**
     * Removes an item from the order
     * 
     * @param name of the item to remove as a {@link String}
     * @param price of the item
     */
    public void removeItem(String name, double price) {
        if (!this.items.containsKey(name))
            return;
        if (this.items.get(name) == 1)
            this.items.remove(name);
        else
            this.items.put(name, this.items.get(name) - 1);

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
    public long getItemCount(String name) {
        return items.containsKey(name) ? items.get(name) : 0l;
    }

    /**
     * Update GUI to current item count
     * 
     * @return updated count as {@link String}
     */
    public String getItemCount() {
        StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, Long> entry : items.entrySet())
            sb.append(entry.getKey() + " x" + entry.getValue() + '\n');
        return sb.toString();
    }

    /**
     * Gets {@link #items}
     * 
     * @return {@link #items}
     */
    public Map<String, Long> getItems() {
        return this.items;
    }
}
