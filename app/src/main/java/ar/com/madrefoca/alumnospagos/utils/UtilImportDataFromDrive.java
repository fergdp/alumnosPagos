package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by fernando on 18/02/18.
 */

public class UtilImportDataFromDrive extends AsyncTask<String, Integer, String> {

    private DatabaseHelper databaseHelper;

    //daos
    private Dao<Place, Integer> placeDao;
    private Dao<EventType, Integer> eventTypeDao;
    private Dao<PaymentType, Integer> paymentTypeDao;
    private Dao<AttendeeType, Integer> attendeeTypeDao;
    private Dao<Attendee, Integer> attendeeDao;
    private Dao<Event, Integer> eventDao;
    private Dao<Coupon, Integer> couponDao;
    private Dao<Payment, Integer> paymentDao;
    private Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;

    private ProgressBar progressBar;
    private Context context;

    private String jsonTables;

    private Integer countOfFieldSaved;

    public UtilImportDataFromDrive(Context context, ProgressBar progressBar, String jsonTables) {
        this.context = context;
        this.progressBar = progressBar;
        this.jsonTables = jsonTables;
        countOfFieldSaved = 0;
    }

    @Override
    protected String doInBackground(String... origin) {
        this.importAllTables();

        return "Import all tables task completed.";
    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        progressBar.setMax(this.countFields());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    private int countFields () {
        int numberOfFields = 0;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonTables);
            numberOfFields = jsonObj.getJSONArray("places").length() +
                    jsonObj.getJSONArray("eventTypes").length() +
                    jsonObj.getJSONArray("paymentTypes").length() +
                    jsonObj.getJSONArray("attendeeTypes").length() +
                    jsonObj.getJSONArray("attendees").length() +
                    jsonObj.getJSONArray("events").length() +
                    jsonObj.getJSONArray("coupons").length() +
                    jsonObj.getJSONArray("payments").length() +
                    jsonObj.getJSONArray("attendeeEventPayments").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return numberOfFields;
    }

    private void importAllTables() {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        databaseHelper.clearTables();

        UtilJsonMapper utilJsonMapper = new UtilJsonMapper(this.jsonTables, context);

        this.saveImportedPlaces(utilJsonMapper.placesJsonMapper());
        this.saveImportedEventTypes(utilJsonMapper.eventTypesJsonMapper());
        this.savePaymentTypes(utilJsonMapper.paymentTypesMapper());
        this.saveAttendeeTypes(utilJsonMapper.attendeeTypesMapper());
        this.saveAttendees(utilJsonMapper.attendeeMapper());
        this.saveEvents(utilJsonMapper.eventMapper());
        this.saveCoupons(utilJsonMapper.couponMapper());
        this.savePayments(utilJsonMapper.paymentMapper());
        this.saveAttendeeEventPayment(utilJsonMapper.attendeeEventPaymentMapper());

    }

    private void saveImportedPlaces(ArrayList<Place> placesList) {
        try {
            placeDao = databaseHelper.getPlacesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Place place: placesList) {
            try {
                placeDao.create(place);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported place from drive : " + place.getName() + " in database");
        }
    }

    private void saveImportedEventTypes(ArrayList<EventType> eventTypeArrayList) {
        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(EventType eventType: eventTypeArrayList) {
            try {
                eventTypeDao.create(eventType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported event type from drive : " + eventType.getName() + " in database");
        }
    }

    private void savePaymentTypes(ArrayList<PaymentType> paymentTypeArrayList) {
        try {
            paymentTypeDao = databaseHelper.getPaymentTypesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(PaymentType paymentType: paymentTypeArrayList) {
            try {
                paymentTypeDao.create(paymentType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported payment type from drive : " + paymentType.getName() + " in database");
        }
    }

    private void saveAttendeeTypes(ArrayList<AttendeeType> attendeeTypeArrayList) {
        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(AttendeeType attendeeType: attendeeTypeArrayList) {
            try {
                attendeeTypeDao.create(attendeeType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported atteendee type from drive : " + attendeeType.getName() + " in database");
        }
    }

    private void saveAttendees(ArrayList<Attendee> attendeeTypeArrayList) {
        try {
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Attendee attendee: attendeeTypeArrayList) {
            try {
                attendeeDao.create(attendee);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported atteendee from drive : " + attendee.getAlias() + " in database");
        }
    }

    private void saveEvents(ArrayList<Event> eventArrayList) {
        try {
            eventDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Event event: eventArrayList) {
            try {
                eventDao.create(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported event from drive : " + event.getName() + " in database");
        }
    }

    private void saveCoupons(ArrayList<Coupon> couponArrayList) {
        try {
            couponDao = databaseHelper.getCouponsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Coupon coupon: couponArrayList) {
            try {
                couponDao.create(coupon);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported coupon from drive : " + coupon.getNumber() + " in database");
        }
    }

    private void savePayments(ArrayList<Payment> paymentArrayList) {
        try {
            paymentDao = databaseHelper.getPaymentsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(Payment payment: paymentArrayList) {
            try {
                paymentDao.create(payment);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported payment from drive : " + payment.getIdPayment() + " in database");
        }
    }

    private void saveAttendeeEventPayment(ArrayList<AttendeeEventPayment> attendeeEventPaymentArrayList) {
        try {
            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(AttendeeEventPayment attendeeEventPayment: attendeeEventPaymentArrayList) {
            try {
                attendeeEventPaymentDao.create(attendeeEventPayment);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            publishProgress(this.countOfFieldSaved++);
            Log.d("UtilImpDataFromDrive: ", "Saved imported attendeeEventPayment from drive : " + attendeeEventPayment.getIdAttendeeEventPayment() + " in database");
        }
    }
}
