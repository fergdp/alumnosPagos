package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventsDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Event;
import ar.com.madrefoca.alumnospagos.model.EventType;
import ar.com.madrefoca.alumnospagos.utils.ManageFragmentsNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    private ArrayList<Event> eventsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private EventsDataAdapter eventsListAdapter;
    private AlertDialog.Builder addEditEventDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    private ArrayAdapter<String> dataAdapter;

    @Nullable
    @BindView(R.id.fabEventTypes)
    FloatingActionButton fabEventType;

    @Nullable
    @BindView(R.id.fabAddEvent)
    FloatingActionButton fabAddEvent;

    @Nullable
    @BindView(R.id.eventsRecyclerView)
    RecyclerView eventsRecyclerView;

    @Nullable
    @BindView(R.id.dialog_event_id)
    EditText eventId;

    @Nullable
    @BindView(R.id.dialog_event_name)
    EditText eventName;

    @Nullable
    @BindView(R.id.dialog_event_paymentAmount)
    EditText eventPaymentAmount;


    //daos
    Dao<EventType, Integer> eventTypeDao;
    Dao<Event, Integer> eventDao;


    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_events, container, false);

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

        this.populateEventsList(thisFragment);
        this.initSwipe();
        this.initDialog(thisFragment, inflater);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        eventsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        eventsRecyclerView.setLayoutManager(layoutManager);
        eventsListAdapter = new EventsDataAdapter(eventsList, thisFragment.getContext());
        eventsRecyclerView.setAdapter(eventsListAdapter);

    }

    private void populateEventsList(View thisFragment) {
        Log.d("EventFragment: ", "put the Events in the view...");
        eventsList.addAll(getAllEventFromDatabase());
        eventsListAdapter.notifyDataSetChanged();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    try {
                        eventDao.deleteById(eventsList.get(position).getIdEvent());
                        Log.d("EventFragment: ", "Event: " + eventsList.get(position).getName() +" "+
                                eventsList.get(position).getName() + ", with id: " +
                                eventsList.get(position).getIdEvent() + " was deleted frm database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    eventsListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditEventDialog.setTitle("Editar clase");
                    eventId.setText(eventsList.get(position).getIdEvent().toString());
                    eventName.setText(eventsList.get(position).getName());

                    addEditEventDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() +
                                width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);

                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(),
                                (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(eventsRecyclerView);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(View thisFragment, LayoutInflater inflater) {
        addEditEventDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_events,null);

        ButterKnife.bind(this, view);

        addEditEventDialog.setView(view);

        this.populateEventTypesSpinner(view);

        addEditEventDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventType eventType = new EventType();
                // TODO: 16/09/17 agregar una validacion por si tratan de crear un alumno y no tienen cargados los tipos de alumnos.

                Event event = null;

                try {
                    if(add){
                        add =false;

                        //getting data from dialog
                        event = new Event();
                        event.setName(eventName.getText().toString());
                        event.setPaymentAmount(Double.valueOf(eventPaymentAmount.getText().toString()));

                        eventDao.create(event);
                        Log.d("EventFragment: ", "Saved event: " + event.getName());
                    }else{
                        event = eventDao.queryForId(Integer.parseInt(eventId.getText().toString()));
                        event.setName(eventName.getText().toString());
                        event.setPaymentAmount(Double.valueOf(eventPaymentAmount.getText().toString()));

                        eventDao.update(event);
                        Log.d("EventFragment: ", "Updated event: " + event.getName());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                eventsList.clear();
                eventsList.addAll(getAllEventFromDatabase());
                eventsListAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
    }

    private void populateEventTypesSpinner(View view) {
        //get events types to put into spinner
        ArrayList<String> list = getEventTypesFromDatabase();

        dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //eventTypesSpinner.setAdapter(dataAdapter);
    }

    private ArrayList<String> getEventTypesFromDatabase() {
        List<EventType> eventsTpesList = null;
        try {
            eventsTpesList = eventTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();
        for(EventType eventType : eventsTpesList){
            list.add(eventType.getName());
        }
        return list;
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
    @OnClick(R.id.fabAddEvent)
    public void onClickAddNewEvent() {
        removeView();
        add = true;
        addEditEventDialog.setTitle("Nuevo evento");
        eventName.setText("");
        addEditEventDialog.show();
    }

    @Optional
    @OnClick(R.id.fabEventTypes)
    public void onClickDisplayEventTypesFragment() {
        ManageFragmentsNavigation.setCurrentTag(ManageFragmentsNavigation.TAG_EVENT_TYPES);

        // update the main content by replacing fragments
        Fragment fragment = ManageFragmentsNavigation.getHomeFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, ManageFragmentsNavigation.navItemTag);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
