package Order;

import java.time.LocalDate;
import java.util.HashMap;

public class Order {
    private int orderId;
    private String customer_name = "";
    private LocalDate date;
    private double totalCost;
    private int employeeId;
    private HashMap<String, Integer> items = new HashMap<String, Integer>();

    public Order(int employeeId, int orderId) {
        this.employeeId = employeeId;
        this.orderId = orderId;
        date = LocalDate.now();
        System.out.println("Order Created: " + "current date: " + date + " order id: " + orderId);
        orderId++;
    }

    public void addItem(String name, double price) {
        if(items.containsKey(name)){
            items.put(name, items.get(name) + 1);
        }
        else {
            items.put(name, 1);
        }
        totalCost += price;
    }

    public void removeItem(String name, double price) {
        if (items.containsKey(name)) {
            if (items.get(name) == 1) {
                items.remove(name);
            } else {
                items.put(name, items.get(name) - 1);
            }
        }
        totalCost -= price;
    }

    public void setCustomerName(String name) {
        customer_name = name;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getOrderId() {
        return orderId;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getItemCount(String name) {
        if (items.containsKey(name)) {
            return items.get(name);
        }
        return 0;
    }

    public String getItemCount() {
        String ret = "";
        for (String key : items.keySet()) {
            ret += key + " x" + items.get(key) + "\n";
        }
        return ret;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }
}
