package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.Place;
import ar.com.madrefoca.alumnospagos.utils.ManageFragmentsNavigation;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private AlertDialog.Builder addPlaceAndAmountDialog;
    private View view;
    private ArrayAdapter<String> dataAdapter;
    private DatabaseHelper databaseHelper = null;
    private Bundle bundle;


    @Nullable
    @BindView(R.id.place_spinner)
    Spinner placeSpinner;

    @Nullable
    @BindView(R.id.paymentAmount)
    EditText eventPaymentAmount;

    //daos
    Dao<Place, Integer> placesDao;
    Dao<Event, Integer> eventsDao;

    public TimePickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //date from datePicker
        bundle = new Bundle();
        this.bundle = this.getArguments();

        databaseHelper = OpenHelperManager.getHelper(view.getContext(),DatabaseHelper.class);

        try {
            placesDao = databaseHelper.getPlacesDao();
            eventsDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initDialog(view, inflater);

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                TimePickerFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.vibrate(true);
        tpd.enableSeconds(false);
        tpd.setTitle("Hora del evento");
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        tpd.show(getFragmentManager(), "Timepickerdialog");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";

        bundle.putInt("hour", hourOfDay);
        bundle.putInt("minutes", minute);

        addPlaceAndAmountDialog.setTitle("Lugar y monto");
        placeSpinner.setSelection(0);
        eventPaymentAmount.setText("150");
        addPlaceAndAmountDialog.show();
    }

    private void initDialog(View thisFragment, LayoutInflater inflater) {
        addPlaceAndAmountDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_place_selector,null);

        ButterKnife.bind(this, view);

        addPlaceAndAmountDialog.setView(view);

        this.populatePlaceSpinner(view);

        addPlaceAndAmountDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String placeSelected = placeSpinner.getSelectedItem().toString();
                Double amountToPay = Double.valueOf(eventPaymentAmount.getText().toString());
                Place place = null;
                try {
                    place = placesDao.queryForEq("name", placeSelected).get(0);
                } catch (SQLException e) {
                    Log.d("TimePickerFragment: ", "Cannot find place: " + placeSelected + "in database");
                    e.printStackTrace();
                }

                Event newEvent = new Event();
                String minuteString = bundle.getInt("minutes") < 10 ? "0"+bundle.getInt("minutes") : ""+
                        bundle.getInt("minutes");

                String eventName = bundle.getInt("day") + "/" +
                        bundle.getInt("month") + "/" +
                        bundle.getInt("year") + " - " +
                        bundle.getInt("hour") + ":" +
                        minuteString + " hs. - " +
                        placeSelected + " - " +
                        "($" + amountToPay + ")";

                newEvent.setName(eventName);
                newEvent.setYear(bundle.getInt("year"));
                newEvent.setMonth(bundle.getInt("month"));
                newEvent.setDay(bundle.getInt("day"));
                newEvent.setHour(bundle.getInt("hour"));
                newEvent.setMinutes(bundle.getInt("minutes"));
                newEvent.setPlace(place);
                newEvent.setPaymentAmount(amountToPay);

                Snackbar snackbar;
                try {
                    eventsDao.create(newEvent);
                    snackbar = Snackbar.make(getView(), "Evento: " + eventName + " guardado!",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.GREEN);
                    snackbar.show();
                    Log.d("TimePickerFragment: ", "Evento: " + eventName + " guardado!");
                } catch (SQLException e) {
                    Log.d("TimePickerFragment: ", "El evento: " + eventName + "no se pudo guardar!");
                    e.printStackTrace();
                    snackbar = Snackbar.make(getView(), "El evento: " + eventName + "no se pudo guardar!",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }

                dialog.dismiss();

                ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_HOME);

                // update the main content by replacing fragments
                Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    private void populatePlaceSpinner(View view) {
        //get attendees types to put into spinner
        ArrayList<String> list = getPlacesFromDatabase();

        dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeSpinner.setAdapter(dataAdapter);
    }

    private ArrayList<String> getPlacesFromDatabase() {
        List<Place> placesList = null;
        try {
            placesList = placesDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();
        for(Place place : placesList){
            list.add(place.getName());
        }
        return list;
    }

}
