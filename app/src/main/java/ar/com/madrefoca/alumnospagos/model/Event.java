package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */

@DatabaseTable(tableName = "Events")
public class Event {

    @DatabaseField(generatedId = true, canBeNull = false)
    private Integer idEvent;
    // TODO: 30/09/17 agregar de nuevo el tipo de evento
    //@DatabaseField(foreign = true, columnName = "idEventType")
    //private EventType eventType;

    @DatabaseField(foreign = true, columnName = "idPlace")
    private Place place;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private Integer day;

    @DatabaseField
    private Integer month;

    @DatabaseField
    private Integer year;

    @DatabaseField
    private Integer hour;

    @DatabaseField
    private Integer minutes;

    @DatabaseField
    private Double paymentAmount;

    @DatabaseField
    private String state;

    public Event() {
    }

    public Event(Place place, String name, Integer day, Integer month,
                 Integer year, Integer hour) {
        this.place = place;
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
    }

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
