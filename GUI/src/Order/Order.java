package Order;

import java.time.LocalDate;
import java.util.HashMap;

public class Order {
    private static int orderId = 96845;
    private String customer_name;
    private LocalDate date;
    private double totalPrice;
    private String employeeId;
    private HashMap<String, Integer> items = new HashMap<String, Integer>();

    public Order(String employeeId){
        this.employeeId = employeeId;
        date = LocalDate.now();
        orderId++;
        System.out.println("Order Created: "+"current date: "+date + " order id: "+orderId);
    }

    public void addItem(String name, double price){
        if(items.containsKey(name)){
            items.put(name, items.get(name)+1);
        }
        else{
            items.put(name, 1);
        }
        totalPrice += price;
    }

    public void removeItem(String name, double price){
        if(items.containsKey(name)){
            if(items.get(name) == 1){
                items.remove(name);
            }
            else{
                items.put(name, items.get(name)-1);
            }
        }
        totalPrice -= price;
    }

    public void setCustomerName(String name){
        customer_name = name;
    }

    public String getCustomerName(){
        return customer_name;
    }

    public String getEmployeeId(){
        return employeeId;
    }

    public int getOrderId(){
        return orderId;
    }

    public LocalDate getDate(){
        return date;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    public int getItemCount(String name){
        if(items.containsKey(name)){
            return items.get(name);
        }
        return 0;
    }

    public String getItemCount(){
        String ret = "";
        for(String key : items.keySet()){
            ret += key + " x" + items.get(key) + "\n";
        }
        return ret;
    }

    public HashMap<String, Integer> getItems(){
        return items;
    }





}
