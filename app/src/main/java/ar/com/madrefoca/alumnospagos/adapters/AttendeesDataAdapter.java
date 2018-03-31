package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.Attendee;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class AttendeesDataAdapter extends RecyclerView.Adapter{

    private ArrayList<Attendee> attendeesList;

    public AttendeesDataAdapter(ArrayList attendeesList) {
        this.attendeesList = attendeesList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendee_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.attendeeName.setText(attendeesList.get(position).getAlias());
    }

    @Override
    public int getItemCount() {
        return attendeesList.size();
    }

    public void addItem(Attendee attendee) {
        attendeesList.add(attendee);
        notifyItemInserted(attendeesList.size());
    }

    public void removeItem(int position) {
        attendeesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, attendeesList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView attendeeName;
        public ViewHolder(View view) {
            super(view);

            attendeeName = (TextView)view.findViewById(R.id.attendee_name);
        }
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<Attendee> filterdAttendeesList) {
        this.attendeesList = filterdAttendeesList;
        notifyDataSetChanged();
    }
}
