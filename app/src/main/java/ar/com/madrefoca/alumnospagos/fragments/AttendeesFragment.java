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
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.AttendeesDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;
import ar.com.madrefoca.alumnospagos.utils.AttendeesSimpleCallback;
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
public class AttendeesFragment extends Fragment {

    private ArrayList<Attendee> attendeesList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private AttendeesDataAdapter attendeesListAdapter;
    private View view;

    @Nullable
    @BindView(R.id.fabAttendeeTypes)
    FloatingActionButton fabAttendeeType;

    @Nullable
    @BindView(R.id.attendeesRecyclerView)
    RecyclerView attendeesRecyclerView;

    @Nullable
    @BindView(R.id.attendeeSearch)
    EditText attendeeSearch;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;
    Dao<Attendee, Integer> attendeeDao;

    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    private ItemTouchHelper itemTouchHelper;

    public AttendeesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendees, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
            attendeeDao = databaseHelper.getAttendeeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //databaseHelper.clearTables();

        //Insert some students
        //DatabasePopulatorUtil databasePopulatorUtil = new DatabasePopulatorUtil(databaseHelper);
        //databasePopulatorUtil.populate();

        this.initViews(thisFragment);

        this.populateAttendeesList(thisFragment);
        this.initSwipe(thisFragment);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        attendeesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        attendeesRecyclerView.setLayoutManager(layoutManager);
        attendeesListAdapter = new AttendeesDataAdapter(attendeesList);
        attendeesRecyclerView.setAdapter(attendeesListAdapter);
    }

    private void populateAttendeesList(View thisFragment) {
        Log.d("AttendeeFragment: ", "put the Attendees in the view...");
        attendeesList.addAll(getAllAttendeeFromDatabase());
        attendeesListAdapter.notifyDataSetChanged();
    }

    private void initSwipe(View thisFragment) {
        this.simpleItemTouchCallback = new AttendeesSimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, thisFragment.getContext(), attendeesList,
                attendeesListAdapter, thisFragment);

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(attendeesRecyclerView);
    }

    private void updateSwipe(ArrayList<Attendee> filterdAttendeesList) {
        ((AttendeesSimpleCallback) simpleItemTouchCallback).updateAttendeesLIst(filterdAttendeesList);
    }

    private List<Attendee> getAllAttendeeFromDatabase() {
        // Reading all Attendee
        Log.d("AttendeeFragment: ", "Reading all attendees from database...");
        List<Attendee> attendeesList = new ArrayList<>();
        try {
            // This is how, a reference of DAO object can be done
            attendeesList = databaseHelper.getAttendeeDao().queryForEq("state", "active");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendeesList;
    }

    @Optional
    @OnClick(R.id.fabAttendeeTypes)
    public void onClickDisplayAttendeeTypesFragment() {
        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_ATTENDEE_TYPES);

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @OnTextChanged(value = R.id.attendeeSearch,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAttendeeInputSearch(Editable editable) {
        //after the change calling the method and passing the search input
        filter(editable.toString());
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Attendee> filterdAttendeesList = new ArrayList<>();

        if(text.isEmpty() || text == "") {
            filterdAttendeesList.addAll(attendeesList);
        } else {
            //looping through existing elements
            for (Attendee attendee : attendeesList) {
                //if the existing elements contains the search input
                if (attendee.getAlias().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdAttendeesList.add(attendee);
                }
            }
        }

        //calling a method of the adapter class and passing the filtered list
        attendeesListAdapter.filterList(filterdAttendeesList);
        this.updateSwipe(filterdAttendeesList);
    }
}
