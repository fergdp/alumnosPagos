package ar.com.madrefoca.alumnospagos.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fernando on 20/01/18.
 */

public class UtilImportContacts extends AsyncTask<String, Integer, String> {

    private ContentResolver contentResolver;
    private DatabaseHelper databaseHelper;

    AttendeeType attendeeType;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;

    private int countOfContactsSaved = 0;

    ProgressBar progressBar;
    private Context context;

    private static final String FROM_PHONE = "phone";
    private static final String FROM_GOOGLE_DRIVE = "drive";

    private String jsonContacts;

    private String filterWord;

    public UtilImportContacts(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public UtilImportContacts(Context context, ProgressBar progressBar, String jsonContacts) {
        this.context = context;
        this.progressBar = progressBar;
        this.jsonContacts = jsonContacts;
    }

    private void importAllContacts(String origin) {

        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();

            attendeeType = new AttendeeType();
            attendeeType.setName("alumno");

            Map matchingFields = new HashMap();
            matchingFields.put("name", "alumno");

            try {
                //if the attendee type exist, is not created.
                if(attendeeTypeDao.queryForFieldValues(matchingFields).size() == 0) {
                    attendeeTypeDao.create(attendeeType);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            attendeeDao = databaseHelper.getAttendeeDao();
            databaseHelper.clearTable("attendees");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (origin) {
            case FROM_PHONE:
                contentResolver = context.getContentResolver();

                Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                            //apply filter
                            String contactName = cur.getString(cur.getColumnIndex(Build.VERSION.SDK_INT
                                    >= Build.VERSION_CODES.HONEYCOMB ?
                                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                                    ContactsContract.Contacts.DISPLAY_NAME));
                            if (contactName.toLowerCase().contains(this.getFilterWord().toLowerCase())) {
                                Attendee attendeeContact = new Attendee();
                                attendeeContact.setName(contactName);
                                attendeeContact.setCellphoneNumber(retrieveContactNumber(id));
                                attendeeContact.setEmail(retrieveContactEmail(id));

                                this.saveImportedContact(attendeeContact);
                                Log.d("UtilImportContacts: ", "Saved imported contact from phone: " + attendeeContact.getAlias());
                            }

                        }
                    }
                    Log.i("UtilImportContacts: ", "Count of total contacts with number: " + this.countContacts());
                    Log.i("UtilImportContacts: ", "Count of total contacts: " + cur.getCount());
                    Log.i("UtilImportContacts: ", "Count of contacts saved: " + this.countOfContactsSaved);
                }
                break;
            case FROM_GOOGLE_DRIVE:
                ArrayList<Attendee> attendeesContacts = JsonUtil.attendeesJsonToMap(jsonContacts, context);
                int countContacts = 0;
                for(Attendee attendee: attendeesContacts) {
                    this.saveImportedContact(attendee);
                    countContacts++;
                    Log.d("UtilImportContacts: ", "Saved imported contact from drive : " + attendee.getAlias() + " in database");
                }
                Log.i("UtilImportContacts: ", "Count of total contacts with number from google drive: " + countContacts);
                Log.i("UtilImportContacts: ", "Count of contacts saved: " + this.countOfContactsSaved);
                break;
            default:
                break;
        }

    }

    private int countContacts() {
        int numberOfContacts = 0;
        contentResolver = context.getContentResolver();

        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    String contactName = cur.getString(cur.getColumnIndex(Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME));
                    if (contactName.toLowerCase().contains((this.getFilterWord() != null) ?  this.getFilterWord().toLowerCase() : "")) {
                        numberOfContacts++;
                    }
                }
            }
        }
        return numberOfContacts;
    }

    private String retrieveContactNumber(String idContact) {
        String phoneNumber = "";
        // get the phone number
        // TODO: 20/01/18 creo que esto te devuelve el ultimo numero. Revisar
        Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{idContact}, null);
        if(!pCur.isAfterLast()) {
            pCur.moveToNext();
            phoneNumber = pCur.getString(
                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
        }

        pCur.close();

        return phoneNumber;
    }

    private String retrieveContactEmail(String idContact) {
        String email = "";
        Cursor emailCur = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{idContact}, null);

        if(!emailCur.isAfterLast()) {
            emailCur.moveToNext();
            email = emailCur.getString(
                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)).trim();
        }

        emailCur.close();

        return email;
    }

    private void saveImportedContact(Attendee attendeeContact) {
        // TODO: 16/09/17 agregar una validacion por si tratan de crear un alumno y no tienen cargados los tipos de alumnos.
        String attendeTypeSelected = attendeeType.getName();
        try {
            attendeeType = attendeeTypeDao.queryForEq("name", attendeTypeSelected).get(0);
        } catch (SQLException e) {
            Log.d("UtilImportContacts: ", "Cannot find attendee type: " + attendeTypeSelected + "in database");
            e.printStackTrace();
        }

        attendeeContact.setAlias();
        attendeeContact.setAttendeeType(attendeeType);
        attendeeContact.setState("active");

        try {
            attendeeDao.create(attendeeContact);
        } catch (SQLException e) {
            Log.e("UtilImportContacts: ", "Cannot save imported contact: " + attendeeContact.getAlias() + " in database");
            e.printStackTrace();
        }

        publishProgress(this.countOfContactsSaved++);

    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected String doInBackground(String... origin) {
        this.importAllContacts(origin[0]);

        return "Task Completed.";
    }

    @Override
    protected void onPreExecute() {
        progressBar.setMax(this.countContacts());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    public String getFilterWord() {
        return filterWord;
    }

    public void setFilterWord(String filterWord) {
        this.filterWord = filterWord;
    }
}


