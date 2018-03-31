package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
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

/**
 * Created by fernando on 18/02/18.
 */

public class UtilJsonMapper {

    private JSONObject jsonObj;
    private Context context;
    private DatabaseHelper databaseHelper;
    private Dao<AttendeeType, Integer> attendeeTypeDao;
    private Dao<Place, Integer> placeDao;
    private Dao<Attendee, Integer> attendeeDao;
    private Dao<Coupon, Integer> couponDao;
    private Dao<PaymentType, Integer> paymentTypeDao;
    private Dao<Event, Integer> eventDao;
    private Dao<Payment, Integer> paymentDao;

    public UtilJsonMapper(String jsonTables, Context context) {
        try {
            this.jsonObj = new JSONObject(jsonTables);
            this.context = context;

            databaseHelper = OpenHelperManager.getHelper(this.context, DatabaseHelper.class);
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            placeDao = databaseHelper.getPlacesDao();
            attendeeDao = databaseHelper.getAttendeeDao();
            couponDao = databaseHelper.getCouponsDao();
            paymentTypeDao = databaseHelper.getPaymentTypesDao();
            eventDao = databaseHelper.getEventsDao();
            paymentDao = databaseHelper.getPaymentsDao();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Place> placesJsonMapper() {
        ArrayList<Place> placesList = new ArrayList<Place>();

        try {
            // Getting JSON Array node
            JSONArray placesJson = jsonObj.getJSONArray("places");

            // looping through All places
            for (int i = 0; i < placesJson.length(); i++) {
                JSONObject c = placesJson.getJSONObject(i);
                Place place = new Place();
                place.setId(c.getInt("idPlace"));
                place.setName(c.getString("name"));
                place.setAddress( c.getString("address"));

                // adding contact to contact list
                placesList.add(place);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return placesList;
    }

    public ArrayList<EventType> eventTypesJsonMapper() {
        ArrayList<EventType> eventTypeArrayList = new ArrayList<EventType>();

        try {
            // Getting JSON Array node
            JSONArray eventTypesJson = jsonObj.getJSONArray("eventTypes");

            // looping through All eventTypes
            for (int i = 0; i < eventTypesJson.length(); i++) {
                JSONObject c = eventTypesJson.getJSONObject(i);
                EventType eventType = new EventType();
                eventType.setIdEventType(c.getInt("idEventType"));
                eventType.setName(c.getString("name"));

                eventTypeArrayList.add(eventType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return eventTypeArrayList;
    }

    public ArrayList<PaymentType> paymentTypesMapper() {
        ArrayList<PaymentType> paymentTypeArrayList = new ArrayList<PaymentType>();

        try {
            // Getting JSON Array node
            JSONArray paymentTypesJson = jsonObj.getJSONArray("paymentTypes");

            // looping through All paymentTypesJson
            for (int i = 0; i < paymentTypesJson.length(); i++) {
                JSONObject c = paymentTypesJson.getJSONObject(i);
                PaymentType paymentType = new PaymentType();
                paymentType.setIdPaymentType(c.getInt("idPaymentType"));
                paymentType.setName(c.getString("name"));
                paymentType.setDescription(c.getString("description"));

                paymentTypeArrayList.add(paymentType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return paymentTypeArrayList;
    }

    public ArrayList<AttendeeType> attendeeTypesMapper() {
        ArrayList<AttendeeType> attendeeTypeArrayList = new ArrayList<AttendeeType>();

        try {
            // Getting JSON Array node
            JSONArray attendeeTypesJson = jsonObj.getJSONArray("attendeeTypes");

            // looping through All attendeeTypesJson
            for (int i = 0; i < attendeeTypesJson.length(); i++) {
                JSONObject c = attendeeTypesJson.getJSONObject(i);
                AttendeeType attendeeType = new AttendeeType();
                attendeeType.setIdAttendeeType(c.getInt("idAttendeeType"));
                attendeeType.setName(c.getString("name"));

                attendeeTypeArrayList.add(attendeeType);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        }

        return attendeeTypeArrayList;
    }

    public ArrayList<Attendee> attendeeMapper() {
        ArrayList<Attendee> attendeeArrayList = new ArrayList<Attendee>();

        try {
            // Getting JSON Array node
            JSONArray attendeeJson = jsonObj.getJSONArray("attendees");

            // looping through All attendeeTypesJson
            for (int i = 0; i < attendeeJson.length(); i++) {
                JSONObject c = attendeeJson.getJSONObject(i);
                Attendee attendee = new Attendee();
                attendee.setAttendeeId(c.getInt("attendeeId"));
                attendee.setName(c.getString("name"));
                attendee.setAttendeeType(attendeeTypeDao.queryForId(Integer.valueOf(c.getString("attendeeType"))));
                attendee.setName(c.getString("name"));
                if (c.has("lastName")) attendee.setLastName(c.getString("lastName"));
                if (c.has("age")) attendee.setAge(c.getInt("age"));
                if (c.has("cellphoneNumber")) attendee.setCellphoneNumber(c.getString("cellphoneNumber"));
                if (c.has("facebookProfile")) attendee.setFacebookProfile(c.getString("facebookProfile"));
                if (c.has("email")) attendee.setEmail(c.getString("email"));
                attendee.setAlias();
                attendee.setState(c.getString("state"));

                attendeeArrayList.add(attendee);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendeeArrayList;
    }

    public ArrayList<Event> eventMapper() {
        ArrayList<Event> eventArrayList = new ArrayList<Event>();

        try {
            JSONArray eventJson = jsonObj.getJSONArray("events");

            for (int i = 0; i < eventJson.length(); i++) {
                JSONObject c = eventJson.getJSONObject(i);
                Event event = new Event();
                event.setIdEvent(c.getInt("idEvent"));
                event.setPlace(placeDao.queryForId(Integer.valueOf(c.getString("idPlace"))));
                event.setName(c.getString("name"));
                event.setDay(c.getInt("day"));
                event.setMonth(c.getInt("month"));
                event.setYear(c.getInt("year"));
                event.setHour(c.getInt("hour"));
                event.setMinutes(c.getInt("minutes"));
                event.setPaymentAmount(c.getDouble("paymentAmount"));
                // TODO: 2/20/18 agregar state cuando creo evento
                if(c.has("state")) event.setState(c.getString("state"));

                eventArrayList.add(event);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventArrayList;
    }

    public ArrayList<Coupon> couponMapper() {
        ArrayList<Coupon> couponArrayList = new ArrayList<Coupon>();

        try {
            JSONArray couponJson = jsonObj.getJSONArray("coupons");

            for (int i = 0; i < couponJson.length(); i++) {
                JSONObject c = couponJson.getJSONObject(i);
                Coupon coupon = new Coupon();
                coupon.setIdCoupon(c.getInt("idCoupon"));
                coupon.setAttendee(attendeeDao.queryForId(c.getInt("attendee")));
                coupon.setNumber(c.getString("number"));
                coupon.setState(c.getString("state"));

                couponArrayList.add(coupon);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return couponArrayList;
    }

    public ArrayList<Payment> paymentMapper() {
        ArrayList<Payment> paymentArrayList = new ArrayList<Payment>();

        try {
            JSONArray paymentJson = jsonObj.getJSONArray("payments");

            for (int i = 0; i < paymentJson.length(); i++) {
                JSONObject c = paymentJson.getJSONObject(i);
                Payment payment = new Payment();
                payment.setIdPayment(c.getInt("idPayment"));
                if(c.has("coupon"))
                    payment.setCoupon(couponDao.queryForId(c.getInt("coupon")));
                if(c.has("paymentType"))
                    payment.setPaymentType(paymentTypeDao.queryForId(c.getInt("paymentType")));
                payment.setAmount(c.getDouble("amount"));

                paymentArrayList.add(payment);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paymentArrayList;
    }

    public ArrayList<AttendeeEventPayment> attendeeEventPaymentMapper() {
        ArrayList<AttendeeEventPayment> attendeeEventPaymentArrayList = new ArrayList<AttendeeEventPayment>();

        try {
            JSONArray attendeeEventPaymentJson = jsonObj.getJSONArray("attendeeEventPayments");

            for (int i = 0; i < attendeeEventPaymentJson.length(); i++) {
                JSONObject c = attendeeEventPaymentJson.getJSONObject(i);
                AttendeeEventPayment attendeeEventPayment = new AttendeeEventPayment();
                attendeeEventPayment.setIdAttendeeEventPayment(c.getInt("idAttendeeEventPayment"));
                attendeeEventPayment.setEvent(eventDao.queryForId(c.getInt("event")));
                attendeeEventPayment.setAttendee(attendeeDao.queryForId(c.getInt("attendee")));
                attendeeEventPayment.setPayment(paymentDao.queryForId(c.getInt("payment")));

                attendeeEventPaymentArrayList.add(attendeeEventPayment);
            }
        } catch (final JSONException e) {
            Log.e("JsonUtil", "Json parsing error: " + e.getMessage());
            Toast.makeText(this.context,
                    "Json parsing error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendeeEventPaymentArrayList;
    }
}
