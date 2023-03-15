package Items;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Entry for the Sales Together Report
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
public class SalesTogetherRow {
    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the Menu Item #1
     * Item
     */
    private final SimpleObjectProperty<String> menuItem1;

    /**
     * {@link SimpleObjectProperty} of {@link String} holding the name of the Menu Item #2
     */
    private final SimpleObjectProperty<String> menuItem2;

    /**
     * {@link SimpleObjectProperty} of {@link Long} holding the number of sales for that Menu Item
     */
    private final SimpleObjectProperty<Long> numSold;

    /**
     * Constructor
     * 
     * @param menuItem1 number of the item
     * @param menuItem2 of the item
     * @param numSold of the item
     */
    public SalesTogetherRow(final String menuItem1, final String menuItem2, final long numSold) {
        this.menuItem1 = new SimpleObjectProperty<>(menuItem1);
        this.menuItem2 = new SimpleObjectProperty<>(menuItem2);
        this.numSold = new SimpleObjectProperty<>(numSold);
    }

    /**
     * Gets {@link #menuItem1}
     * 
     * @return {@link #menuItem1}
     */
    public SimpleObjectProperty<String> getMenuItem1() {
        return this.menuItem1;
    }

    /**
     * Gets {@link #menuItem2}
     * 
     * @return {@link #menuItem2}
     */
    public SimpleObjectProperty<String> getMenuItem2() {
        return this.menuItem2;
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
