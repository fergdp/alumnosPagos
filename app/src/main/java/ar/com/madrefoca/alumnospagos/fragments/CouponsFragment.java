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
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.CouponsDataAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.Attendee;
import ar.com.madrefoca.alumnospagos.model.Coupon;

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
public class CouponsFragment extends Fragment {

    private ArrayList<Coupon> couponsList =  new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private CouponsDataAdapter couponsListAdapter;
    private AlertDialog.Builder addEditCouponDialog;
    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();
    private ArrayAdapter<String> dataAdapter;


    @Nullable
    @BindView(R.id.fabAddNewCoupon)
    FloatingActionButton fabAddCoupon;

    @Nullable
    @BindView(R.id.couponsRecyclerView)
    RecyclerView couponsRecyclerView;

    @Nullable
    @BindView(R.id.dialog_coupon_id)
    EditText couponId;

    @Nullable
    @BindView(R.id.dialog_coupon_number)
    EditText couponNumber;

    @Nullable
    @BindView(R.id.dialog_attendees_spinner)
    Spinner attendeesSpinner;

    //daos
    Dao<Coupon, Integer> couponDao;
    Dao<Attendee, Integer> attendeeDao;

    public CouponsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_coupons, container, false);

        ButterKnife.bind(this, thisFragment);

        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(),DatabaseHelper.class);

        try {
            couponDao = databaseHelper.getCouponsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.initViews(thisFragment);

        this.populateCouponsList();
        this.initSwipe();
        this.initDialog(thisFragment, inflater);
        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initViews(View thisFragment) {
        couponsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(thisFragment.getContext());
        couponsRecyclerView.setLayoutManager(layoutManager);
        couponsListAdapter = new CouponsDataAdapter(couponsList);
        couponsRecyclerView.setAdapter(couponsListAdapter);

    }

    private void populateCouponsList() {
        Log.d("CouponsFragment: ", "put the Coupons in the view...");
        couponsList.addAll(getAllCouponFromDatabase());
        couponsListAdapter.notifyDataSetChanged();
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
                        couponDao.deleteById(couponsList.get(position).getIdCoupon());
                        Log.d("CouponsFragment: ", "Coupon: " + couponsList.get(position).getNumber() + ", with id: " +
                                couponsList.get(position).getIdCoupon() + " was deleted from database.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    couponsListAdapter.removeItem(position);
                } else {
                    removeView();
                    edit_position = position;
                    addEditCouponDialog.setTitle("Editar cupón");
                    couponId.setText(couponsList.get(position).getIdCoupon().toString());
                    couponNumber.setText(couponsList.get(position).getNumber().toString());
                    attendeesSpinner.setSelection(dataAdapter.getPosition(
                            couponsList.get(position).getAttendee().getName()));

                    addEditCouponDialog.show();
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
        itemTouchHelper.attachToRecyclerView(couponsRecyclerView);
    }

    private void removeView(){
        if(view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void initDialog(View thisFragment, LayoutInflater inflater) {
        addEditCouponDialog = new AlertDialog.Builder(thisFragment.getContext());
        view = inflater.inflate(R.layout.dialog_coupons,null);

        ButterKnife.bind(this, view);

        addEditCouponDialog.setView(view);

        addEditCouponDialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Attendee attendee = new Attendee();
                // TODO: 16/09/17 agregar una validacion por si tratan de crear un cupon y no tienen cargados los alumnos.
                String attendeeSelected = attendeesSpinner.getSelectedItem().toString();
                try {
                    // TODO: 24/09/17 cambiar esta busqueda por un id para que no teng conflictos por nombres repetidos
                    attendee = attendeeDao.queryForEq("name", attendeeSelected).get(0);
                } catch (SQLException e) {
                    Log.d("AttendeeFragment: ", "Cannot find attendee type: " + attendeeSelected + "in database");
                    e.printStackTrace();
                }
                Coupon coupon = null;

                try {
                    if(add){
                        add =false;

                        //getting data from dialog
                        coupon = new Coupon();
                        coupon.setNumber(couponNumber.getText().toString());
                        coupon.setAttendee(attendee);
                        couponDao.create(coupon);
                        Log.d("CouponsFragment: ", "Saved coupon: " + coupon.getNumber());
                    }else{
                        coupon = couponDao.queryForId(Integer.parseInt(couponId.getText().toString()));
                        coupon.setNumber(couponNumber.getText().toString());
                        coupon.setAttendee(attendee);
                        couponDao.update(coupon);
                        Log.d("CouponsFragment: ", "Updated coupon: " + coupon.getNumber());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                couponsList.clear();
                couponsList.addAll(getAllCouponFromDatabase());
                couponsListAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
    }

    private List<Coupon> getAllCouponFromDatabase() {
        // Reading all Coupon
        Log.d("CouponsFragment: ", "Reading all coupons from database...");
        List<Coupon> couponsList = null;
        try {
            // This is how, a reference of DAO object can be done
            couponsList = databaseHelper.getCouponsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couponsList;
    }

    @Optional
    @OnClick(R.id.fabAddNewCoupon)
    public void onClickAddNewCoupon() {
        // TODO: 24/09/17 arreglar el spinner para que tenga los alumnos cargados
        removeView();
        add = true;
        addEditCouponDialog.setTitle("Nuevo cupón");
        couponNumber.setText("");
        attendeesSpinner.setSelection(1);
        addEditCouponDialog.show();
    }

}
