package ar.com.madrefoca.alumnospagos.fragments;


import android.app.Fragment;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventTypesDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.EventType;

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
public class EventTypesFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private View view;
    private AlertDialog.Builder addEditEventTypesDialog;
    private boolean add = false;
    private ArrayList<EventType> eventTypesList =  new ArrayList<>();
    private EventTypesDataAdapter eventTypesListAdapter;
    private int edit_position;
    private Paint p = new Paint();

    @Nullable
    @BindView(R.id.fabAddNewEventType)
    FloatingActionButton fabAddNewEventType;

    @Nullable
    @BindView(R.id.eventTypesRecyclerView)
    RecyclerView eventTypesRecyclerView;

    @Nullable
    @BindView(R.id.dialog_event_type_name)
    EditText eventTypeName;

    @Nullable
    @BindView(R.id.dialog_event_type_id)
    EditText eventTypeid;


    public EventTypesFragment() {
        // Required empty public constructor
    }

    //daos
    Dao<EventType, Integer> eventTypeDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_event_types, container, false);

        ButterKnife.bind(this, thisFragment);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_event_types);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            eventTypeDao = databaseHelper.getEventTypesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);
        //populate list
        this.populateEventTypesList();
        this.initSwipe();
        this.initDialog(thisFragment, inflater);

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void populateEventTypesList(){
        Log.d("EventTypesFragment: ", "put the Event types in the view...");
        eventTypesList.addAll(getAllEventTypesFromDatabase());
        eventTypesListAdapter.notifyDataSetChanged();
    }

    private void initViews(View thisFragment) {
        eventTypesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        eventTypesRecyclerView.setLayoutManager(layoutManager);
        eventTypesListAdapter = new EventTypesDataAdapter(eventTypesList);
        eventTypesRecyclerView.setAdapter(eventTypesListAdapter);
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
                        eventTypeDao.deleteById(eventTypesList.get(position).getIdEventType());
                        Log.d("EventFragment: ", "Event type: " + eventTypesList.get(position).getName() + ", with id: " +
                                eventTypesList.get(position).getIdEventType() + " was deleted from database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    eventTypesListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditEventTypesDialog.setTitle("Editar tipo de evento");
                    eventTypeid.setText(eventTypesList.get(position).getIdEventType().toString());
                    eventTypeName.setText(eventTypesList.get(position).getName());
                    addEditEventTypesDialog.show();
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
        itemTouchHelper.attachToRecyclerView(eventTypesRecyclerView);
    }

    private void initDialog(View thisFragment, LayoutInflater inflater)  {
        addEditEventTypesDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_event_types,null);
        ButterKnife.bind(this, view);
        addEditEventTypesDialog.setView(view);

        addEditEventTypesDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventType eventType = null;
                try {
                    if(add){
                        add =false;
                        //getting data from dialog
                        eventType = new EventType();
                        eventType.setName(eventTypeName.getText().toString());
                        eventTypeDao.create(eventType);
                        Log.d("EventTypeFragment: ", "Saved event type : " + eventType.getName());
                    }else{
                        eventType = eventTypeDao.queryForId(Integer.parseInt(eventTypeid.getText().toString()));
                        eventType.setName(eventTypeName.getText().toString());
                        eventTypeDao.update(eventType);
                        Log.d("EventTypeFragment: ", "Updated event type: " + eventType.getName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                eventTypesList.clear();
                eventTypesList.addAll(getAllEventTypesFromDatabase());
                eventTypesListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    @Optional
    @OnClick(R.id.fabAddNewEventType)
    public void onClickAddNewEventType() {
        removeView();
        add = true;
        addEditEventTypesDialog.setTitle("Nuevo tipo de evento");
        eventTypeName.setText("");
        addEditEventTypesDialog.show();
    }

    private List<EventType> getAllEventTypesFromDatabase() {
        // Reading all EventTypes
        Log.d("EventTypesFragment: ", "Reading all EventTypes from database...");
        List<EventType> eventTypeList = null;
        try {
            // This is how, a reference of DAO object can be done
            eventTypeList = eventTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventTypeList;
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

}
