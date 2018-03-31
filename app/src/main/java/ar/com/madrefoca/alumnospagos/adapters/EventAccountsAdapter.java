package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.utils.AttendeePaymentRow;

import java.util.ArrayList;

/**
 * Created by fernando on 15/10/17.
 */

public class EventAccountsAdapter extends RecyclerView.Adapter<EventAccountsAdapter.ViewHolder> {

    private ArrayList<AttendeePaymentRow> attendeePaymentRowArrayList ;

    public EventAccountsAdapter(ArrayList<AttendeePaymentRow> attendeePaymentRowArrayList ) {
        this.attendeePaymentRowArrayList = attendeePaymentRowArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_event_accounts_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewName.setText(attendeePaymentRowArrayList.get(position).getAttendee().getAlias());
        holder.amountCardView.setText("$ " + attendeePaymentRowArrayList.get(position).getPayment()
                .getAmount().toString());
    }

    @Override
    public int getItemCount() {
        return attendeePaymentRowArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView amountCardView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.attendee_name_cardview);
            amountCardView = (TextView) itemView.findViewById(R.id.amount_cardview);
        }
    }

    public void removeItem(int position) {
        attendeePaymentRowArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, attendeePaymentRowArrayList.size());
    }

    public Double calculateTotalCash() {
        Double partialTotalCash = 0.0;
        for(AttendeePaymentRow attendeePaymentRow : attendeePaymentRowArrayList) {
            partialTotalCash+= attendeePaymentRow.getPayment().getAmount();
        }
        return partialTotalCash;
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<AttendeePaymentRow> filterdAttendeePaymentRow) {
        this.attendeePaymentRowArrayList = filterdAttendeePaymentRow;
        notifyDataSetChanged();
    }
}
