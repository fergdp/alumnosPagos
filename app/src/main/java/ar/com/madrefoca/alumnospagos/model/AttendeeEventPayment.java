package ar.com.madrefoca.alumnospagos.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by fernando on 03/09/17.
 */
@DatabaseTable(tableName = "AttendeesEventsPayments")
public class AttendeeEventPayment {

    @DatabaseField(generatedId = true, canBeNull = false)
    private Integer idAttendeeEventPayment;

    @DatabaseField(foreign = true, columnName = "idEvent")
    private Event event;

    @DatabaseField(foreign = true, columnName = "dni")
    private Attendee attendee;

    @DatabaseField(foreign = true, columnName = "idPayment")
    private Payment payment;

    public AttendeeEventPayment() {
    }

    public AttendeeEventPayment(Event event, Attendee attendee, Payment payment) {
        this.event = event;
        this.attendee = attendee;
        this.payment = payment;
    }

    public Integer getIdAttendeeEventPayment() {
        return idAttendeeEventPayment;
    }

    public void setIdAttendeeEventPayment(Integer idAttendeeEventPayment) {
        this.idAttendeeEventPayment = idAttendeeEventPayment;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
