package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;
import ar.com.madrefoca.alumnospagos.model.Coupon;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.EventType;
import ar.com.madrefoca.alumnospagos.model.Payment;
import ar.com.madrefoca.alumnospagos.model.PaymentType;
import ar.com.madrefoca.alumnospagos.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Gomez de Paz on 1/25/18.
 */

public class JsonUtil {

    public static String attendeesToJSon(List<Attendee> attendees) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonAttendeesArray = new JSONArray();

        for(Attendee attendee : attendees) {

            try {
                // Here we convert Java Object to JSON
                JSONObject jsonAttendee = new JSONObject();
                jsonAttendee.put("attendeeId", attendee.getAttendeeId());
                jsonAttendee.put("attendeeType", attendee.getAttendeeType().getName());
                jsonAttendee.put("name", attendee.getName());
                jsonAttendee.put("lastName", attendee.getLastName());
                jsonAttendee.put("age", attendee.getAge());
                jsonAttendee.put("cellphoneNumber", attendee.getCellphoneNumber());
                jsonAttendee.put("facebookProfile", attendee.getFacebookProfile());
                jsonAttendee.put("email", attendee.getFacebookProfile());
                jsonAttendee.put("alias", attendee.getAlias());
                jsonAttendee.put("state", attendee.getState());

                //jsonObj.put(attendee.getAttendeeId().toString(), jsonAttendee);
                jsonAttendeesArray.put(jsonAttendee);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

            try {
                jsonObj.put("attendees",jsonAttendeesArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObj.toString();
    }

    public static String eventsToJSon(List<Event> events) {
        JSONObject jsonEvents = new JSONObject();
        JSONArray jsonEventsArray = new JSONArray();

        for(Event event : events) {

            try {
                // Here we convert Java Object to JSON
                JSONObject jsonEvent = new JSONObject();
                jsonEvent.put("idEvent", event.getIdEvent());
                jsonEvent.put("idPlace", event.getPlace().getIdplace());
                jsonEvent.put("name", event.getName());
                jsonEvent.put("day", event.getDay());
                jsonEvent.put("month", event.getMonth());
                jsonEvent.put("year", event.getYear());
                jsonEvent.put("hour", event.getHour());
                jsonEvent.put("minutes", event.getMinutes());
                jsonEvent.put("paymentAmount", event.getPaymentAmount());
                jsonEvent.put("state", event.getState());

                jsonEventsArray.put(jsonEvent);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

            try {
                jsonEvents.put("events",jsonEventsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonEvents.toString();
    }

    public static ArrayList<Attendee> attendeesJsonToMap(String jsonContacts, final Context context) {
        Log.i("JsonUtil", "jsonContacts string: " + jsonContacts);
        ArrayList<Attendee> attendeeContacts = new ArrayList<Attendee>();


        try {
            JSONObject jsonObj = new JSONObject(jsonContacts);

            // Getting JSON Array node
            JSONArray contacts = jsonObj.getJSONArray("attendees");

            // looping through All Contacts
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                Attendee attendee = new Attendee();
                attendee.setName(c.getString("name"));
                attendee.setCellphoneNumber(c.getString("cellphoneNumber"));
                attendee.setEmail(c.isNull("email") ? "" : c.getString("email"));
                attendee.setState(c.getString("state"));

                // adding contact to contact list
                attendeeContacts.add(attendee);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return attendeeContacts;
    }

    public static String allTablesToJSon(DatabaseHelper databaseHelper) {
        Dao<Place, Integer> placeDao;
        Dao<EventType, Integer> eventTypeDao;
        Dao<PaymentType, Integer> paymentTypeDao;
        Dao<AttendeeType, Integer> attendeeTypeDao;
        Dao<Attendee, Integer> attendeeDao;
        Dao<Event, Integer> eventDao;
        Dao<Coupon, Integer> couponDao;
        Dao<Payment, Integer> paymentDao;
        Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;

        List<Place> places = null;
        List<EventType> eventTypes = null;
        List<PaymentType> paymentTypes = null;
        List<AttendeeType> attendeeTypes = null;
        List<Attendee> attendees = null;
        List<Event> events = null;
        List<Coupon> coupons = null;
        List<Payment> payments = null;
        List<AttendeeEventPayment> attendeeEventPayments = null;

        try {
            placeDao = databaseHelper.getPlacesDao();
            places = placeDao.queryForAll();

            eventTypeDao = databaseHelper.getEventTypesDao();
            eventTypes = eventTypeDao.queryForAll();

            paymentTypeDao = databaseHelper.getPaymentTypesDao();
            paymentTypes = paymentTypeDao.queryForAll();

            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            attendeeTypes = attendeeTypeDao.queryForAll();

            attendeeDao = databaseHelper.getAttendeeDao();
            attendees = attendeeDao.queryForAll();

            eventDao = databaseHelper.getEventsDao();
            events = eventDao.queryForAll();

            couponDao = databaseHelper.getCouponsDao();
            coupons = couponDao.queryForAll();

            paymentDao = databaseHelper.getPaymentsDao();
            payments = paymentDao.queryForAll();

            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
            attendeeEventPayments = attendeeEventPaymentDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObj = new JSONObject();
        try {
            //keep the order of this json for dependencies in tables
            jsonObj.put("places", placesToJson(places));
            jsonObj.put("eventTypes", eventTypesToJson(eventTypes));
            jsonObj.put("paymentTypes", paymentTypesToJson(paymentTypes));
            jsonObj.put("attendeeTypes", attendeeTypesToJson(attendeeTypes));
            jsonObj.put("attendees", attendeesToJson(attendees));
            jsonObj.put("events", eventsToJson(events));
            jsonObj.put("coupons", couponsToJson(coupons));
            jsonObj.put("payments", paymentsToJson(payments));
            jsonObj.put("attendeeEventPayments", attendeeEventPaymentToJson(attendeeEventPayments));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public static JSONArray placesToJson (List<Place> places) {
        JSONArray jsonPlacesArray = new JSONArray();

        for(Place place : places) {
            try {
                // Here we convert Java Object to JSON
                JSONObject jsonPlace = new JSONObject();
                jsonPlace.put("idPlace", place.getIdplace());
                jsonPlace.put("name", place.getName());
                jsonPlace.put("address", place.getAddress());
                jsonPlace.put("gpsLocation", place.getGpsLocation());
                jsonPlace.put("phone", place.getPhone());
                jsonPlace.put("email", place.getEmail());
                jsonPlace.put("facebookLink", place.getFacebookLink());

                jsonPlacesArray.put(jsonPlace);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonPlacesArray;
    }

    public static JSONArray eventTypesToJson (List<EventType> eventTypes) {
        JSONArray jsonEventTypesArray = new JSONArray();

        for(EventType eventType : eventTypes) {
            try {
                JSONObject jsonEventType = new JSONObject();
                jsonEventType.put("idEventType", eventType.getIdEventType());
                jsonEventType.put("name", eventType.getName());
                jsonEventType.put("description", eventType.getDescription());

                jsonEventTypesArray.put(jsonEventType);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonEventTypesArray;
    }

    public static JSONArray paymentTypesToJson (List<PaymentType> paymentTypes) {
        JSONArray jsonPaymentTypesArray = new JSONArray();

        for(PaymentType paymentType : paymentTypes) {
            try {
                JSONObject jsonPaymentType = new JSONObject();
                jsonPaymentType.put("idPaymentType", paymentType.getIdPaymentType());
                jsonPaymentType.put("name", paymentType.getName());
                jsonPaymentType.put("description", paymentType.getDescription());

                jsonPaymentTypesArray.put(jsonPaymentType);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonPaymentTypesArray;
    }

    public static JSONArray attendeeTypesToJson (List<AttendeeType> attendeeTypes) {
        JSONArray jsonAttendeeTypesArray = new JSONArray();

        for(AttendeeType attendeeType : attendeeTypes) {
            try {
                JSONObject jsonAttendeeType = new JSONObject();
                jsonAttendeeType.put("idAttendeeType", attendeeType.getIdAttendeeType());
                jsonAttendeeType.put("name", attendeeType.getName());
                jsonAttendeeType.put("description", attendeeType.getDescription());

                jsonAttendeeTypesArray.put(jsonAttendeeType);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }

        }
        return jsonAttendeeTypesArray;
    }

    public static JSONArray attendeesToJson (List<Attendee> attendees) {
        JSONArray jsonAttendeesArray = new JSONArray();

        for(Attendee attendee : attendees) {
            try {
                JSONObject jsonAttendee = new JSONObject();
                jsonAttendee.put("attendeeId", attendee.getAttendeeId());
                jsonAttendee.put("attendeeType", attendee.getAttendeeType().getIdAttendeeType());
                jsonAttendee.put("name", attendee.getName());
                jsonAttendee.put("lastName", attendee.getLastName());
                jsonAttendee.put("age", attendee.getAge());
                jsonAttendee.put("cellphoneNumber", attendee.getCellphoneNumber());
                jsonAttendee.put("facebookProfile", attendee.getFacebookProfile());
                jsonAttendee.put("email", attendee.getFacebookProfile());
                jsonAttendee.put("alias", attendee.getAlias());
                jsonAttendee.put("state", attendee.getState());

                jsonAttendeesArray.put(jsonAttendee);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonAttendeesArray;
    }

    public static JSONArray eventsToJson (List<Event> events) {
        JSONArray jsonEventsArray = new JSONArray();

        for(Event event : events) {
            try {
                JSONObject jsonEvent = new JSONObject();
                jsonEvent.put("idEvent", event.getIdEvent());
                jsonEvent.put("idPlace", event.getPlace().getIdplace());
                jsonEvent.put("name", event.getName());
                jsonEvent.put("day", event.getDay());
                jsonEvent.put("month", event.getMonth());
                jsonEvent.put("year", event.getYear());
                jsonEvent.put("hour", event.getHour());
                jsonEvent.put("minutes", event.getMinutes());
                jsonEvent.put("paymentAmount", event.getPaymentAmount());
                jsonEvent.put("state", event.getState());

                jsonEventsArray.put(jsonEvent);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonEventsArray;
    }

    public static JSONArray couponsToJson (List<Coupon> coupons) {
        JSONArray jsonCouponsArray = new JSONArray();

        for(Coupon coupon : coupons) {
            try {
                JSONObject jsonCoupon = new JSONObject();
                jsonCoupon.put("idCoupon", coupon.getIdCoupon());
                jsonCoupon.put("attendee", coupon.getAttendee().getAttendeeId());
                jsonCoupon.put("number", coupon.getNumber());
                jsonCoupon.put("description", coupon.getDescription());
                jsonCoupon.put("state", coupon.getState());

                jsonCouponsArray.put(jsonCoupon);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonCouponsArray;
    }

    public static JSONArray paymentsToJson (List<Payment> payments) {
        JSONArray jsonPaymentsArray = new JSONArray();

        for(Payment payment : payments) {
            try {
                JSONObject jsonPayment = new JSONObject();
                jsonPayment.put("idPayment", payment.getIdPayment());
                if(payment.getCoupon() != null)
                    jsonPayment.put("coupon", payment.getCoupon().getIdCoupon());
                if(payment.getPaymentType() != null)
                    jsonPayment.put("paymentType", payment.getPaymentType().getIdPaymentType());
                if(payment.getAmount() != null)
                    jsonPayment.put("amount", payment.getAmount());

                jsonPaymentsArray.put(jsonPayment);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonPaymentsArray;
    }

    public static JSONArray attendeeEventPaymentToJson (List<AttendeeEventPayment> attendeeEventPayments) {
        JSONArray jsonCouponsArray = new JSONArray();

        for(AttendeeEventPayment attendeeEventPayment : attendeeEventPayments) {
            try {
                JSONObject jsonAttendeeEventPayment = new JSONObject();
                jsonAttendeeEventPayment.put("idAttendeeEventPayment", attendeeEventPayment.getIdAttendeeEventPayment());
                jsonAttendeeEventPayment.put("event", attendeeEventPayment.getEvent().getIdEvent());
                jsonAttendeeEventPayment.put("attendee", attendeeEventPayment.getAttendee().getAttendeeId());
                jsonAttendeeEventPayment.put("payment", attendeeEventPayment.getPayment().getIdPayment());

                jsonCouponsArray.put(jsonAttendeeEventPayment);
            }
            catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonCouponsArray;
    }
}
