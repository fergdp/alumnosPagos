package ar.com.madrefoca.alumnospagos.utils;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by fernando on 27/08/17.
 */

public class DatabasePopulatorUtil {
    private DatabaseHelper databaseHelper = null;

    public DatabasePopulatorUtil() {
    }

    public DatabasePopulatorUtil(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void populate(){
        this.insertSomeStudents();
    }

    private void insertSomeStudents() {
        Log.d("DatabasePopulatorUtil: ", "insertSomeIllnessesAndSchools()");

        try {
            // This is how, a reference of DAO object can be done
            final Dao<Attendee, Integer> attendeeDao = databaseHelper.getAttendeeDao();

            AttendeeType attendeeType = new AttendeeType();
            attendeeType.setName("alumno");

            Dao<AttendeeType, Integer> attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            attendeeTypeDao.create(attendeeType);

            ArrayList<Attendee> attendeeArrayList = new ArrayList<Attendee>();
            /*attendeeArrayList.add(new Attendee("Fernando", "Gomez de Paz", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Daniel", "gdp", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Pepe", "gomez", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Eduardo", "Charrett", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Jeremy", "Zanin", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Jannett","Erazu", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Ezequiel","Fernandez", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Andrea","Genero", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("gonza", "asdf", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Daniela", "afffef", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Pepo", "gomez", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Eduarda", "Charrett", attendeeType, 234444));
            attendeeArrayList.add(new Attendee("Jero", "Zanin", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Janne","Erazu", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Pao","Fernandez", attendeeType, 33444));
            attendeeArrayList.add(new Attendee("Andres","Genero", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Fernanda", "asdffa", attendeeType, 5543222));
            attendeeArrayList.add(new Attendee("Daniele", "gdp", attendeeType, 56543333));
            attendeeArrayList.add(new Attendee("Carlos", "gomez", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Fede", "asdffff", attendeeType, 223444234));
            attendeeArrayList.add(new Attendee("Santy", "asdffa", attendeeType, 423444));
            attendeeArrayList.add(new Attendee("Lau","ffff", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Mauro","asdffee", attendeeType, 153251291));
            attendeeArrayList.add(new Attendee("Ale","asdfefefafvasad", attendeeType, 153251291));*/


            for(Attendee attendee: attendeeArrayList){
                //This is the way to insert data into a database table
                attendeeDao.create(attendee);
                String log = "Attendee " + attendee.getName() + ", saved in database. " ;
                // Writing Illness to log
                Log.d("DatabasePopulatorUtil: ", log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
