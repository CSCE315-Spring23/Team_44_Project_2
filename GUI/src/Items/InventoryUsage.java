package Items;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Internal class representing an item in the inventory
 */
public class InventoryUsage {
    /**
     * {@link SimpleObjectProperty} of {@link Long} holding the identification number
     */
    private final SimpleObjectProperty<Long> id;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the item
     */
    private final SimpleObjectProperty<String> name;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the usage of the item
     */
    private final SimpleObjectProperty<String> quantity;

    /**
     * Constructor
     * 
     * @param id identification number
     * @param name of the item as {@link String}
     * @param percent amount usage
     */
    public InventoryUsage(final long id, final String name, final double percent) {
        this.id = new SimpleObjectProperty<>(id);
        this.name = new SimpleObjectProperty<>(name);
        this.quantity = new SimpleObjectProperty<>(String.format("%.5f%%", percent * 100));
    }

    /**
     * Gets {@link #id}
     * 
     * @return {@link #id}
     */
    public SimpleObjectProperty<Long> getId() {
        return this.id;
    }

    /**
     * Gets {@link #name}
     * 
     * @return {@link #name}
     */
    public SimpleObjectProperty<String> getName() {
        return this.name;
    }

    /**
     * Gets {@link #quantity}
     * 
     * @return {@link #quantity}
     */
    public SimpleObjectProperty<String> getQuantity() {
        return quantity;
    }
}
