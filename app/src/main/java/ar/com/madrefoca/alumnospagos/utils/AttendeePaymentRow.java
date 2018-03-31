package ar.com.madrefoca.alumnospagos.utils;

import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;
import ar.com.madrefoca.alumnospagos.model.Payment;

/**
 * Created by fernando on 15/10/17.
 */

public class AttendeePaymentRow {

    private AttendeeEventPayment attendeeEventPayment;
    private Attendee attendee;
    private Payment payment;

    public AttendeeEventPayment getAttendeeEventPayment() {
        return attendeeEventPayment;
    }

    public void setAttendeeEventPayment(AttendeeEventPayment attendeeEventPayment) {
        this.attendeeEventPayment = attendeeEventPayment;
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


