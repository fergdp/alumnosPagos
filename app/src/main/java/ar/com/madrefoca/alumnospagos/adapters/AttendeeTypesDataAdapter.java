package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.AttendeeType;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class AttendeeTypesDataAdapter extends RecyclerView.Adapter{

    private ArrayList<AttendeeType> attendeeTypesList;

    public AttendeeTypesDataAdapter(ArrayList attendeeTypesList) {
        this.attendeeTypesList = attendeeTypesList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendee_type_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.attendeeTypeName.setText(attendeeTypesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return attendeeTypesList.size();
    }

    public void addItem(AttendeeType attendeeType) {
        attendeeTypesList.add(attendeeType);
        notifyItemInserted(attendeeTypesList.size());
    }

    public void removeItem(int position) {
        attendeeTypesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, attendeeTypesList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView attendeeTypeName;
        public ViewHolder(View view) {
            super(view);

            attendeeTypeName = (TextView)view.findViewById(R.id.attendee_type_name);
        }
    }
}
