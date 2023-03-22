package Items;

import java.sql.Date;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Controller for the Z Row List screen.
 *
 * @since 2023-03-20
 * @version 2023-03-20
 *
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 */
public class ZRow {
    /**
     * {@link SimpleObjectProperty} of {@link Long} storing the identification number of the Z-row
     * table
     */
    private final SimpleObjectProperty<Long> reportID;

    /**
     * {@link SimpleObjectProperty} of {@link String} storing the total sales for that day
     */
    private final SimpleObjectProperty<String> totalSales;

    /**
     * {@link SimpleObjectProperty} of {@link String} storing the name of the Employee
     */
    private final SimpleObjectProperty<String> employee;

    /**
     * {@link SimpleObjectProperty} of {@link Long} storing the order ID
     */
    private final SimpleObjectProperty<Long> orderID;

    /**
     * {@link SimpleObjectProperty} of {@link Date storing the date the z-row entry was created
     */
    private final SimpleObjectProperty<Date> dateCreated;

    /**
     * Constructor
     * 
     * @param reportID identification number of the Z-row table
     * @param totalSales total sales made in that day
     * @param employee being the name of the employee
     * @param dateCreated being the date when z-row entry was made
     * @param orderID identifies the last order ID from the last z-report made
     */
    public ZRow(final Long reportID, final String totalSales, final String employee,
            final Long orderID, final Date dateCreated) {
        this.reportID = new SimpleObjectProperty<>(reportID);
        this.totalSales = new SimpleObjectProperty<>(totalSales);
        this.employee = new SimpleObjectProperty<>(employee);
        this.orderID = new SimpleObjectProperty<>(orderID);
        this.dateCreated = new SimpleObjectProperty<>(dateCreated);
    }

    /**
     * Gets value in {@link #reportID}
     * 
     * @return {@link #reportID}
     */

    public SimpleObjectProperty<Long> getReportID() {
        return this.reportID;
    }

    /**
     * Gets value in {@link #employee}
     * 
     * @return {@link #employee}
     */

    public SimpleObjectProperty<String> getEmployee() {
        return this.employee;
    }

    /**
     * Gets the value in {@link #totalSales}
     * 
     * @return {@link #totalSales}
     */

    public SimpleObjectProperty<String> getTotalSales() {
        return this.totalSales;
    }

    /**
     * Gets the value of {@link #dateCreated}
     * 
     * @return {@link #dateCreated}
     */

    public SimpleObjectProperty<Date> getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Gets value in {@link #orderID}
     * 
     * @return {@link #orderID}
     */

    public SimpleObjectProperty<Long> getOrderID() {
        return this.orderID;
    }
}
