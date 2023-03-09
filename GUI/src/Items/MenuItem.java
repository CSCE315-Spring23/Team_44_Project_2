package Items;

import javafx.beans.property.SimpleObjectProperty;

public class MenuItem {
    private final SimpleObjectProperty<Long> id;
    private final SimpleObjectProperty<String> name;
    private final SimpleObjectProperty<Double> price;
    private final SimpleObjectProperty<Long> numSold;

    public MenuItem(final long id, final String name, final double price, final long numSold) {
        this.id = new SimpleObjectProperty<Long>(id);
        this.name = new SimpleObjectProperty<>(name);
        this.price = new SimpleObjectProperty<>(price);
        this.numSold = new SimpleObjectProperty<>(numSold);
    }

    public SimpleObjectProperty<Long> getId() {
        return id;
    }

    public SimpleObjectProperty<String> getName() {
        return name;
    }

    public SimpleObjectProperty<Double> getPrice() {
        return price;
    }

    public SimpleObjectProperty<Long> getNumSold() {
        return numSold;
    }
}
