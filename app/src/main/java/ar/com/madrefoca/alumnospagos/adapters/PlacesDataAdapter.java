package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.Place;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class PlacesDataAdapter extends RecyclerView.Adapter{

    private ArrayList<Place> placesList;

    public PlacesDataAdapter(ArrayList placesList) {
        this.placesList = placesList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.placeName.setText(placesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public void addItem(Place place) {
        placesList.add(place);
        notifyItemInserted(placesList.size());
    }

    public void removeItem(int position) {
        placesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, placesList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView placeName;
        public ViewHolder(View view) {
            super(view);

            placeName = (TextView)view.findViewById(R.id.place_name);
        }
    }
}
