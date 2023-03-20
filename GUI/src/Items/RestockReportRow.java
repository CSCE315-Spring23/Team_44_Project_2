package Items;

import javafx.beans.property.SimpleObjectProperty;

public class RestockReportRow {

    private SimpleObjectProperty<Integer> id;
    private SimpleObjectProperty<String> name;
    private SimpleObjectProperty<Integer> quantity;

    public RestockReportRow(int id, String name, int quantity) {
        this.id = new SimpleObjectProperty<Integer>(id);
        this.name = new SimpleObjectProperty<String>(name);
        this.quantity = new SimpleObjectProperty<Integer>(quantity);
    }

    public SimpleObjectProperty<Integer> getId() {
        return this.id;
    }

    public SimpleObjectProperty<String> getName() {
        return this.name;
    }

    public SimpleObjectProperty<Integer> getQuantity() {
        return this.quantity;
    }
}
