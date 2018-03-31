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
import ar.com.madrefoca.alumnospagos.adapters.AttendeeTypesDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;

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
public class AttendeeTypesFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private View view;
    private AlertDialog.Builder addEditAttendeeTypeDialog;
    private boolean add = false;
    private ArrayList<AttendeeType> attendeeTypesList =  new ArrayList<>();
    private AttendeeTypesDataAdapter attendeeTypesListAdapter;
    private int edit_position;
    private Paint p = new Paint();

    @Nullable
    @BindView(R.id.fabAddNewAteendeeType)
    FloatingActionButton fabAddNewAttendeeType;

    @Nullable
    @BindView(R.id.attendeeTypesRecyclerView)
    RecyclerView attendeeTypesRecyclerView;

    @Nullable
    @BindView(R.id.dialog_attendee_type_name)
    EditText attendeeTypeName;

    @Nullable
    @BindView(R.id.dialog_attendee_type_id)
    EditText attendeeTypeid;

    //daos
    Dao<AttendeeType, Integer> attendeeTypeDao;

    public AttendeeTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_attendee_types, container, false);

        ButterKnife.bind(this, thisFragment);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_attendees_types);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            attendeeTypeDao = databaseHelper.getAttendeeTypeDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);
        //populate list
        this.populateAttendeeTypesList();
        this.initSwipe();
        this.initDialog(thisFragment, inflater);

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void populateAttendeeTypesList(){
        Log.d("AttendeeTypesFragment: ", "put the AttendeeTypes in the view...");
        attendeeTypesList.addAll(getAllAttendeeTypesFromDatabase());
        attendeeTypesListAdapter.notifyDataSetChanged();
    }

    private void initViews(View thisFragment) {
        attendeeTypesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        attendeeTypesRecyclerView.setLayoutManager(layoutManager);
        attendeeTypesListAdapter = new AttendeeTypesDataAdapter(attendeeTypesList);
        attendeeTypesRecyclerView.setAdapter(attendeeTypesListAdapter);
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
                        attendeeTypeDao.deleteById(attendeeTypesList.get(position).getIdAttendeeType());
                        Log.d("AttendeeFragment: ", "Attendee type: " + attendeeTypesList.get(position).getName() + ", with id: " +
                                attendeeTypesList.get(position).getIdAttendeeType() + " was deleted from database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    attendeeTypesListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditAttendeeTypeDialog.setTitle("Editar tipo de asistente");
                    attendeeTypeid.setText(attendeeTypesList.get(position).getIdAttendeeType().toString());
                    attendeeTypeName.setText(attendeeTypesList.get(position).getName());
                    addEditAttendeeTypeDialog.show();
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
        itemTouchHelper.attachToRecyclerView(attendeeTypesRecyclerView);
    }

    private void initDialog(View thisFragment, LayoutInflater inflater)  {
        addEditAttendeeTypeDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_attendee_types,null);
        ButterKnife.bind(this, view);
        addEditAttendeeTypeDialog.setView(view);

        addEditAttendeeTypeDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AttendeeType attendeeType = null;
                try {
                    if(add){
                        add =false;
                        //getting data from dialog
                        attendeeType = new AttendeeType();
                        attendeeType.setName(attendeeTypeName.getText().toString());
                        attendeeTypeDao.create(attendeeType);
                        Log.d("AttendeeTypeFragment: ", "Saved attendee type : " + attendeeType.getName());
                    }else{
                        attendeeType = attendeeTypeDao.queryForId(Integer.parseInt(attendeeTypeid.getText().toString()));
                        attendeeType.setName(attendeeTypeName.getText().toString());
                        attendeeTypeDao.update(attendeeType);
                        Log.d("AttendeeTypeFragment: ", "Updated attendee type: " + attendeeType.getName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                attendeeTypesList.clear();
                attendeeTypesList.addAll(getAllAttendeeTypesFromDatabase());
                attendeeTypesListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    @Optional
    @OnClick(R.id.fabAddNewAteendeeType)
    public void onClickAddNewAttendeeType() {
        removeView();
        add = true;
        addEditAttendeeTypeDialog.setTitle("Nuevo tipo de asistente");
        attendeeTypeName.setText("");
        addEditAttendeeTypeDialog.show();
    }

    private List<AttendeeType> getAllAttendeeTypesFromDatabase() {
        // Reading all AttendeeTypes
        Log.d("AttendeeTypesFragment: ", "Reading all AttendeeTypes from database...");
        List<AttendeeType> attendeeTypeList = null;
        try {
            // This is how, a reference of DAO object can be done
            attendeeTypeList = attendeeTypeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendeeTypeList;
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
}
