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
import ar.com.madrefoca.alumnospagos.adapters.PlacesDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Place;

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
public class PlacesFragment extends Fragment {


    private ArrayList<Place> placesList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private PlacesDataAdapter placesListAdapter;
    private AlertDialog.Builder addEditPlaceDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    private ArrayAdapter<String> dataAdapter;


    @Nullable
    @BindView(R.id.fabAddNewPlace)
    FloatingActionButton fabAddPlace;

    @Nullable
    @BindView(R.id.placesRecyclerView)
    RecyclerView placesRecyclerView;

    @Nullable
    @BindView(R.id.dialog_place_id)
    EditText placeId;

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
    Dao<Place, Integer> placeDao;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_places, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            placeDao = databaseHelper.getPlacesDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);

        this.populatePlacesList();
        this.initSwipe();
        this.initDialog(thisFragment, inflater);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        placesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        placesRecyclerView.setLayoutManager(layoutManager);
        placesListAdapter = new PlacesDataAdapter(placesList);
        placesRecyclerView.setAdapter(placesListAdapter);

    }

    private void populatePlacesList() {
        Log.d("PlaceFragment: ", "put the Places in the view...");
        placesList.addAll(getAllPlaceFromDatabase());
        placesListAdapter.notifyDataSetChanged();
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
                        placeDao.deleteById(placesList.get(position).getIdplace());
                        Log.d("PlaceFragment: ", "Place: " + placesList.get(position).getName() + ", with id: " +
                                placesList.get(position).getIdplace() + " was deleted from database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    placesListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditPlaceDialog.setTitle("Editar lugar");
                    placeId.setText(placesList.get(position).getIdplace().toString());
                    placeName.setText(placesList.get(position).getName());
                    placeAddress.setText(placesList.get(position).getAddress());
                    if(placesList.get(position).getPhone() != null)
                        placePhone.setText(placesList.get(position).getPhone().toString());
                    placeFacebook.setText(placesList.get(position).getFacebookLink().toString());
                    placeEmail.setText(placesList.get(position).getEmail().toString());

                    addEditPlaceDialog.show();
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
        itemTouchHelper.attachToRecyclerView(placesRecyclerView);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(View thisFragment, LayoutInflater inflater) {
        addEditPlaceDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_places,null);

        ButterKnife.bind(this, view);

        addEditPlaceDialog.setView(view);

        addEditPlaceDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Place place = null;

                try {
                    if(add){
                        add =false;

                        //getting data from dialog
                        place = new Place();
                        place.setName(placeName.getText().toString());
                        place.setAddress(placeAddress.getText().toString());
                        place.setPhone(placePhone.getText().toString());
                        place.setFacebookLink(placeFacebook.getText().toString());
                        place.setEmail(placeEmail.getText().toString());

                        placeDao.create(place);
                        Log.d("PlaceFragment: ", "Saved place: " + place.getName());
                    }else{
                        place = placeDao.queryForId(Integer.parseInt(placeId.getText().toString()));
                        place.setName(placeName.getText().toString());
                        place.setAddress(placeAddress.getText().toString());
                        place.setPhone(placePhone.getText().toString());
                        place.setFacebookLink(placeFacebook.getText().toString());
                        place.setEmail(placeEmail.getText().toString());

                        placeDao.update(place);
                        Log.d("PlaceFragment: ", "Updated place: " + place.getName());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                placesList.clear();
                placesList.addAll(getAllPlaceFromDatabase());
                placesListAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
    }

    private List<Place> getAllPlaceFromDatabase() {
        // Reading all Place
        Log.d("PlaceFragment: ", "Reading all places from database...");
        List<Place> placesList = null;
        try {
            // This is how, a reference of DAO object can be done
            placesList = databaseHelper.getPlacesDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return placesList;
    }

    @Optional
    @OnClick(R.id.fabAddNewPlace)
    public void onClickAddNewPlace() {
        removeView();
        add = true;
        addEditPlaceDialog.setTitle("Nuevo lugar");
        placeName.setText("");
        placeAddress.setText("");
        placePhone.setText("");
        placeFacebook.setText("");
        placeEmail.setText("");
        addEditPlaceDialog.show();
    }
}
