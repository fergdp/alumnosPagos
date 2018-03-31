package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventsPaymentsDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.utils.EventsPaymentsSimpleCallback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentsFragment extends Fragment {

    private ArrayList<Event> eventsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EventsPaymentsDataAdapter eventsPaymentsListAdapter;

    @Nullable
    @BindView(R.id.eventsToPaymentsRecyclerView)
    RecyclerView eventsToPaymentsRecyclerView;

    //daos
    Dao<Event, Integer> eventDao;

    public PaymentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_payments, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            eventDao = databaseHelper.getEventsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);

        this.populateEventsList();
        this.initSwipe(thisFragment);

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        eventsToPaymentsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        eventsToPaymentsRecyclerView.setLayoutManager(layoutManager);
        eventsPaymentsListAdapter = new EventsPaymentsDataAdapter(eventsList, thisFragment.getContext());
        eventsToPaymentsRecyclerView.setAdapter(eventsPaymentsListAdapter);
    }

    private void initSwipe(View thisFragment) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new EventsPaymentsSimpleCallback(0,
                ItemTouchHelper.LEFT, thisFragment.getContext(), eventsList,
                eventsPaymentsListAdapter, thisFragment);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(eventsToPaymentsRecyclerView);
    }

    private void populateEventsList() {
        Log.d("EventsPaymentsFrag: ", "put the Events in the view...");
        eventsList.addAll(getAllEventFromDatabase());
        eventsPaymentsListAdapter.notifyDataSetChanged();
    }

    private List<Event> getAllEventFromDatabase() {
        // Reading all Event
        Log.d("EventsPaymentsFrag: ", "Reading all events from database...");
        List<Event> eventsList = null;
        try {
            // This is how, a reference of DAO object can be done
            eventsList = databaseHelper.getEventsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }

    @OnTextChanged(value = R.id.eventsPaymentsSearch,
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
        eventsPaymentsListAdapter.filterList(filterdEventList);
    }

}
