package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.EventType;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class EventTypesDataAdapter extends RecyclerView.Adapter{

    private ArrayList<EventType> eventTypesList;

    public EventTypesDataAdapter(ArrayList eventTypesList) {
        this.eventTypesList = eventTypesList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_type_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.eventTypeName.setText(eventTypesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return eventTypesList.size();
    }

    public void addItem(EventType eventType) {
        eventTypesList.add(eventType);
        notifyItemInserted(eventTypesList.size());
    }

    public void removeItem(int position) {
        eventTypesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventTypesList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventTypeName;
        public ViewHolder(View view) {
            super(view);

            eventTypeName = (TextView)view.findViewById(R.id.event_type_name);
        }
    }
}
