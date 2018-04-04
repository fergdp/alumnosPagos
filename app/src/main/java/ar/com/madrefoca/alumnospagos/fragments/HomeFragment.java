package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventsDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.EventType;
import ar.com.madrefoca.alumnospagos.model.Place;
import ar.com.madrefoca.alumnospagos.utils.HomeSimpleCallback;
import ar.com.madrefoca.alumnospagos.utils.ManageFragmentsNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "Home fragment";
    private ArrayList<Event> eventsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EventsDataAdapter eventsListAdapter;
    private AlertDialog.Builder createPlaceDialog;
    private AlertDialog.Builder addPlaceDialog;

    @Nullable
    @BindView(R.id.fabAddPaymentsEvent)
    FloatingActionButton fabAddPaymentsEvent;

    @Nullable
    @BindView(R.id.eventsPaymentsRecyclerView)
    RecyclerView eventsPaymentsRecyclerView;

    @Nullable
    @BindView(R.id.dialog_place_name)
    EditText placeName;

    @Nullable
    @BindView(R.id.dialog_place_address)
    EditText placeAddress;

    @Nullable
    @BindView(R.id.dialog_place_phone)
    EditText placePhone;

    @Nullable
    @BindView(R.id.dialog_place_facebook)
    EditText placeFacebook;

    @Nullable
    @BindView(R.id.dialog_place_email)
    EditText placeEmail;

    //daos
    Dao<EventType, Integer> eventTypeDao;
    Dao<Event, Integer> eventDao;
    Dao<Place, Integer> placeDao;

    private View thisFragment;

    private View view;
    private View createFirstPlaceView = null;
    private View dialogPlacesView = null;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragment = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
            eventDao = databaseHelper.getEventsDao();
            placeDao = databaseHelper.getPlacesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //databaseHelper.clearTables();

        //Insert some students
        //DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        //databasePopulatorUtil.populate();

        this.initViews(thisFragment);

        this.populateEventsList();
        this.initSwipe(thisFragment);
        this.initDialogs(thisFragment, inflater);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        eventsPaymentsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        eventsPaymentsRecyclerView.setLayoutManager(layoutManager);
        eventsListAdapter = new EventsDataAdapter(eventsList, thisFragment.getContext());
        eventsPaymentsRecyclerView.setAdapter(eventsListAdapter);
    }

    private void initSwipe(View thisFragment) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new HomeSimpleCallback(0,
                ItemTouchHelper.LEFT, thisFragment.getContext(), eventsList,
                eventsListAdapter, thisFragment);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(eventsPaymentsRecyclerView);
    }

    private void initDialogs(final View thisFragment, LayoutInflater inflater) {
        createPlaceDialog = new AlertDialog.Builder(thisFragment.getContext());
        createFirstPlaceView = inflater.inflate(R.layout.dialog_create_first_place,null);

        ButterKnife.bind(this, createFirstPlaceView);

        createPlaceDialog.setView(createFirstPlaceView);

        createPlaceDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addPlaceDialog.show();
            }
        });

        createPlaceDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "No event created because no place was added.");
                Toast.makeText(thisFragment.getContext(),
                        R.string.do_not_create_event_without_place,
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        addPlaceDialog = new AlertDialog.Builder(thisFragment.getContext());
        dialogPlacesView = inflater.inflate(R.layout.dialog_places,null);

        ButterKnife.bind(this, dialogPlacesView);

        addPlaceDialog.setView(dialogPlacesView);

        addPlaceDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Place place = null;

                try {
                    //getting data from dialog
                    place = new Place();
                    place.setName(placeName.getText().toString());
                    place.setAddress(placeAddress.getText().toString());
                    place.setPhone(placePhone.getText().toString());
                    place.setFacebookLink(placeFacebook.getText().toString());
                    place.setEmail(placeEmail.getText().toString());

                    placeDao.create(place);
                    Log.d(TAG, "Saved place: " + place.getName());

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

                Fragment fragment = new DatePickerFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
                fragmentTransaction.commitAllowingStateLoss();

            }
        });
    }

    private void populateEventsList() {
        Log.d("EventFragment: ", "put the Events in the view...");
        eventsList.addAll(getAllEventFromDatabase());
        eventsListAdapter.notifyDataSetChanged();
    }

    private List<Event> getAllEventFromDatabase() {
        // Reading all Event
        Log.d("EventFragment: ", "Reading all events from database...");
        List<Event> eventsList = null;
        try {
            // This is how, a reference of DAO object can be done
            eventsList = databaseHelper.getEventsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    @Optional
    @OnClick(R.id.fabAddPaymentsEvent)
    public void onClickAddNewEvent() {
        removeView();
        try {
            if(placeDao.queryForAll().size() <= 0) {
                createPlaceDialog.setTitle(R.string.dialog_title_new_place);
                createPlaceDialog.setMessage(R.string.dialog_create_new_place);
                createPlaceDialog.show();
            } else {
                Fragment fragment = new DatePickerFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Optional
    @OnClick(R.id.fabPaymentsEventTypes)
    public void onClickDisplayEventTypesFragment() {
        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_EVENT_TYPES);

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Optional
    @OnTextChanged(value = R.id.eventSearch,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAttendeeInputSearch(Editable editable) {
        //after the change calling the method and passing the search input
        filter(editable.toString());
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Event> filterdEventList = new ArrayList<>();

        if(text.isEmpty() || text == "") {
            filterdEventList.addAll(eventsList);
        } else {
            //looping through existing elements
            for (Event event : eventsList) {
                //if the existing elements contains the search input
                if (event.getName().contains(text)) {
                    //adding the element to filtered list
                    filterdEventList.add(event);
                }
            }
        }

        //calling a method of the adapter class and passing the filtered list
        eventsListAdapter.filterList(filterdEventList);
    }

    private void removeView(){
        if(createFirstPlaceView.getParent()!=null) {
            ((ViewGroup) createFirstPlaceView.getParent()).removeView(createFirstPlaceView);
        }

        if(dialogPlacesView.getParent()!=null) {
            ((ViewGroup) dialogPlacesView.getParent()).removeView(dialogPlacesView);
        }
    }

}
