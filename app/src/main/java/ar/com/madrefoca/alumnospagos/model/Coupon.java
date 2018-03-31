package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "Coupons")
public class Coupon {

    @DatabaseField(generatedId = true)
    private Integer idCoupon;

    @DatabaseField(foreign = true, columnName = "idAttendee")
    private Attendee attendee;

    @DatabaseField
    private String number;

    @DatabaseField
    private Integer remainingClasses;

    @DatabaseField
    private String description;

    @DatabaseField
    private String state;

    public Coupon() {
    }

    public Coupon(Attendee attendee, String description) {
        this.attendee = attendee;
        this.description = description;
    }

    public Integer getIdCoupon() {
        return idCoupon;
    }

    public void setIdCoupon(Integer idCoupon) {
        this.idCoupon = idCoupon;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getRemainingClasses() {
        return remainingClasses;
    }

    public void setRemainingClasses(Integer remainingClasses) {
        this.remainingClasses = remainingClasses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
