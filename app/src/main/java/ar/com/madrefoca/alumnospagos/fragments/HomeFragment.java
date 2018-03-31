package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventsDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.EventType;
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

    private ArrayList<Event> eventsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EventsDataAdapter eventsListAdapter;

    @Nullable
    @BindView(R.id.fabAddPaymentsEvent)
    FloatingActionButton fabAddPaymentsEvent;

    @Nullable
    @BindView(R.id.eventsPaymentsRecyclerView)
    RecyclerView eventsPaymentsRecyclerView;

    //daos
    Dao<EventType, Integer> eventTypeDao;
    Dao<Event, Integer> eventDao;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
            eventDao = databaseHelper.getEventsDao();
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
        // TODO: 25/09/17  https://github.com/wdullaer/MaterialDateTimePicker
        // TODO: 25/09/17  https://android--examples.blogspot.com.ar/2015/05/how-to-use-datepickerdialog-in-android.html

        Fragment fragment = new DatePickerFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
        fragmentTransaction.commitAllowingStateLoss();
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

}
