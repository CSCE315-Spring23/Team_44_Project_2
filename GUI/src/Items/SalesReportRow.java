package Items;

import javafx.beans.property.SimpleObjectProperty;

/**
 * An individual menu item
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
public class SalesReportRow {
    /**
     * {@link SimpleObjectProperty} of {@link Long} holding the indentification number of the Menu
     * Item
     */
    private final SimpleObjectProperty<Long> id;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the Menu Item
     */
    private final SimpleObjectProperty<String> name;

    /**
     * {@link SimpleObjectProperty} of {@link Long} holding the number of sales for that Menu Item
     */
    private final SimpleObjectProperty<Long> numSold;

    /**
     * Constructor
     * 
     * @param id number of the item
     * @param name of the item
     * @param numSold of the item
     */
    public SalesReportRow(final long id, final String name, final long numSold) {
        this.id = new SimpleObjectProperty<Long>(id);
        this.name = new SimpleObjectProperty<>(name);
        this.numSold = new SimpleObjectProperty<>(numSold);
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
     * Gets {@link #numSold}
     * 
     * @return {@link #numSold}
     */
    public SimpleObjectProperty<Long> getNumSold() {
        return this.numSold;
    }
}
