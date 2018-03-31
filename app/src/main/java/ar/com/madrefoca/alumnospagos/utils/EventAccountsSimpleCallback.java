package ar.com.madrefoca.alumnospagos.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.adapters.EventAccountsAdapter;
import ar.com.madrefoca.alumnospagos.helpers.DatabaseHelper;
import ar.com.madrefoca.alumnospagos.model.AttendeeEventPayment;

import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fernando on 16/10/17.
 */

public class EventAccountsSimpleCallback extends ItemTouchHelper.SimpleCallback {

    @Nullable
    @BindView(R.id.total_cash)
    EditText totalCash;

    private ArrayList<AttendeePaymentRow> attendeePaymentRowArrayList =  new ArrayList<>();
    private EventAccountsAdapter eventAccountsAdapter;
    private Context context;
    private int position;
    private Paint p = new Paint();
    private View thisFragment;
    private DatabaseHelper databaseHelper;

    //daos
    Dao<AttendeeEventPayment, Integer> attendeeEventPaymentDao;

    public EventAccountsSimpleCallback(int dragDirs, int swipeDirs, Context context,
                                       ArrayList<AttendeePaymentRow> attendeePaymentRowArrayList,
                                       EventAccountsAdapter eventAccountsAdapter,
                                       View thisFragment) {
        super(dragDirs, swipeDirs);

        this.context = context;
        this.attendeePaymentRowArrayList = attendeePaymentRowArrayList;
        this.eventAccountsAdapter = eventAccountsAdapter;
        this.thisFragment = thisFragment;
        databaseHelper = OpenHelperManager.getHelper(thisFragment.getContext(), DatabaseHelper.class);

        try {
            attendeeEventPaymentDao = databaseHelper.getAttendeeEventPaymentDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ButterKnife.bind(this, thisFragment);

        //this.initDialog();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT){
            this.manageDelete();
        } else {
            //this.manageEdit();
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
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_edit_white);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() +
                        width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);

                c.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background,p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void manageDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Borrar Pago?");
        builder.setMessage("Esta por borrar un pago de un alumno! Desea continuar?");
        builder.setCancelable(false);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    attendeeEventPaymentDao.deleteById(attendeePaymentRowArrayList.get(position)
                            .getAttendeeEventPayment().getIdAttendeeEventPayment());
                    Log.d("EventAccSC: ", "Payment of: " + attendeePaymentRowArrayList.get(position).getPayment().getAmount() +
                            " from attendee: " + attendeePaymentRowArrayList.get(position).getAttendee().getAlias() +
                            ", with AEP id: " + attendeePaymentRowArrayList.get(position).getAttendeeEventPayment().getIdAttendeeEventPayment() +
                            " was deleted frm database.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                eventAccountsAdapter.removeItem(position);

                //calculate new total
                totalCash.setText(eventAccountsAdapter.calculateTotalCash().toString());

                Toast.makeText(context, "Se borro el el pago del alumno.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "No se borro el pago del alumno!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}
