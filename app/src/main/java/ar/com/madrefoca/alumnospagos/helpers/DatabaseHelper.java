package ar.com.madrefoca.alumnospagos.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;
import ar.com.madrefoca.alumnospagos.model.Coupon;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.EventType;
import ar.com.madrefoca.alumnospagos.model.Notification;
import ar.com.madrefoca.alumnospagos.model.Payment;
import ar.com.madrefoca.alumnospagos.model.PaymentType;
import ar.com.madrefoca.alumnospagos.model.Place;

import java.sql.SQLException;

/**
 * Created by fernando on 05/09/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "AlumnosTango.db";
    private static final int DATABASE_VERSION = 11;

    private Dao<Attendee, Integer> attendeeDao;
    private Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;
    private Dao<AttendeeType, Integer> attendeeTypesDao;
    private Dao<Coupon, Integer> couponsDao;
    private Dao<Event, Integer> eventsDao;
    private Dao<EventType, Integer> eventTypesDao;
    private Dao<Notification, Integer> notificationsDao;
    private Dao<Payment, Integer> paymentsDao;
    private Dao<PaymentType, Integer> paymentTypesDao;
    private Dao<Place, Integer> placesDao;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * What to do when your database needs to be created. Usually this entails creating the tables and loading any
     * initial data.
     * <p>
     * <p>
     * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     * </p>
     *
     * @param sqliteDatabase         Database being created.
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Attendee.class);
            TableUtils.createTable(connectionSource, AttendeeEventPayment.class);
            TableUtils.createTable(connectionSource, AttendeeType.class);
            TableUtils.createTable(connectionSource, Coupon.class);
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, EventType.class);
            TableUtils.createTable(connectionSource, Notification.class);
            TableUtils.createTable(connectionSource, Payment.class);
            TableUtils.createTable(connectionSource, PaymentType.class);
            TableUtils.createTable(connectionSource, Place.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
        }
    }

    /**
     * What to do when your database needs to be updated. This could mean careful migration of old data to new data.
     * Maybe adding or deleting database columns, etc..
     * <p>
     * <p>
     * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     * </p>
     *
     * @param sqliteDatabase         Database being upgraded.
     * @param connectionSource To use get connections to the database to be updated.
     * @param oldVersion       The version of the current database so we can know what to do to the database.
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Attendee.class, true);
            TableUtils.dropTable(connectionSource, AttendeeEventPayment.class, true);
            TableUtils.dropTable(connectionSource, AttendeeType.class, true);
            TableUtils.dropTable(connectionSource, Coupon.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            TableUtils.dropTable(connectionSource, EventType.class, true);
            TableUtils.dropTable(connectionSource, Notification.class, true);
            TableUtils.dropTable(connectionSource, Payment.class, true);
            TableUtils.dropTable(connectionSource, PaymentType.class, true);
            TableUtils.dropTable(connectionSource, Place.class, true);

            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new "
                    + newVersion, e);
        }
    }

    public void clearTables() {
        try {
            TableUtils.clearTable(connectionSource, Attendee.class);
            TableUtils.clearTable(connectionSource, AttendeeEventPayment.class);
            TableUtils.clearTable(connectionSource, AttendeeType.class);
            TableUtils.clearTable(connectionSource, Coupon.class);
            TableUtils.clearTable(connectionSource, Event.class);
            TableUtils.clearTable(connectionSource, EventType.class);
            TableUtils.clearTable(connectionSource, Notification.class);
            TableUtils.clearTable(connectionSource, Payment.class);
            TableUtils.clearTable(connectionSource, PaymentType.class);
            TableUtils.clearTable(connectionSource, Place.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to clear tables", e);
        }
    }

    public void clearTable(String tableName) {

        try {
            switch (tableName) {
                case "attendees":
                    TableUtils.clearTable(connectionSource, Attendee.class);
                    Log.d("DatabaseHelper: ", "Databse table : " + tableName + " cleared!");
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            Log.e("DatabaseHelper: ", "Cannot clear table: " + tableName + " in database");
            e.printStackTrace();
        }

    }

    public Dao<Attendee, Integer> getAttendeeDao() throws SQLException {
        if (attendeeDao == null) {
            attendeeDao = getDao(Attendee.class);
        }
        return attendeeDao;
    }

    public Dao<AttendeeEventPayment, Integer> getAttendeeEventPaymentDao() throws SQLException {
        if (attendeeEventPaymentDao == null) {
            attendeeEventPaymentDao = getDao(AttendeeEventPayment.class);
        }
        return attendeeEventPaymentDao;
    }

    public Dao<AttendeeType, Integer> getAttendeeTypeDao() throws SQLException {
        if (attendeeTypesDao == null) {
            attendeeTypesDao = getDao(AttendeeType.class);
        }
        return attendeeTypesDao;
    }

    public Dao<Coupon, Integer> getCouponsDao() throws SQLException {
        if (couponsDao == null) {
            couponsDao = getDao(Coupon.class);
        }
        return couponsDao;
    }

    public Dao<Event, Integer> getEventsDao() throws SQLException {
        if (eventsDao == null) {
            eventsDao = getDao(Event.class);
        }
        return eventsDao;
    }

    public Dao<EventType, Integer> getEventTypesDao() throws SQLException {
        if (eventTypesDao == null) {
            eventTypesDao = getDao(EventType.class);
        }
        return eventTypesDao;
    }

    public Dao<Notification, Integer> getNotificationsDao() throws SQLException {
        if (notificationsDao == null) {
            notificationsDao = getDao(Notification.class);
        }
        return notificationsDao;
    }

    public Dao<Payment, Integer> getPaymentsDao() throws SQLException {
        if (paymentsDao == null) {
            paymentsDao = getDao(Payment.class);
        }
        return paymentsDao;
    }

    public Dao<PaymentType, Integer> getPaymentTypesDao() throws SQLException {
        if (paymentTypesDao == null) {
            paymentTypesDao = getDao(PaymentType.class);
        }
        return paymentTypesDao;
    }

    public Dao<Place, Integer> getPlacesDao() throws SQLException {
        if (placesDao == null) {
            placesDao = getDao(Place.class);
        }
        return placesDao;
    }

}

