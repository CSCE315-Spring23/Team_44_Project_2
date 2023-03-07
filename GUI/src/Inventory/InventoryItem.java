package Inventory;

public class InventoryItem {
    private final String name;
    private final long quantity;

    public InventoryItem(final String name, final long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public long getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return this.name + " x" + this.quantity;
    }
}
