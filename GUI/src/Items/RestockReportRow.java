package Items;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Handle displaying the {@link Controller.Reports.RestockReport} in the GUI
 *
 * @since 2023-03-07
 * @version 2023-03-07
 *
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 * 
 * @deprecated
 */
public class RestockReportRow {
    /**
     * {@link SimpleObjectProperty} of {@link Integer} holding the identification number of the
     * item.
     */
    private SimpleObjectProperty<Integer> id;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the item.
     */
    private SimpleObjectProperty<String> name;

    /**
     * {@link SimpleObjectProperty} of {@link Integer} holding the quantity of the item.
     */
    private SimpleObjectProperty<Integer> quantity;

    /**
     * Constructor
     *
     * @param id id number of the item
     * @param name name of the item
     * @param quantity quantity of the item
     */
    public RestockReportRow(int id, String name, int quantity) {
        this.id = new SimpleObjectProperty<Integer>(id);
        this.name = new SimpleObjectProperty<String>(name);
        this.quantity = new SimpleObjectProperty<Integer>(quantity);
    }

    /**
     * Gets {@link #id}
     *
     * @return {@link #id}
     */
    public SimpleObjectProperty<Integer> getId() {
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
    public SimpleObjectProperty<Integer> getQuantity() {
        return this.quantity;
    }
}
