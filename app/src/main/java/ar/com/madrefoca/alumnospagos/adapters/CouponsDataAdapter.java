package ar.com.madrefoca.alumnospagos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.madrefoca.alumnospagos.R;
import ar.com.madrefoca.alumnospagos.model.Coupon;

import java.util.ArrayList;


/**
 * Created by fernando on 09/09/17.
 */

public class CouponsDataAdapter extends RecyclerView.Adapter{

    private ArrayList<Coupon> couponsList;

    public CouponsDataAdapter(ArrayList couponsList) {
        this.couponsList = couponsList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.couponNumber.setText(couponsList.get(position).getNumber().toString());
    }

    @Override
    public int getItemCount() {
        return couponsList.size();
    }

    public void addItem(Coupon coupon) {
        couponsList.add(coupon);
        notifyItemInserted(couponsList.size());
    }

    public void removeItem(int position) {
        couponsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, couponsList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView couponNumber;
        public ViewHolder(View view) {
            super(view);
            couponNumber = (TextView)view.findViewById(R.id.coupon_number);
        }
    }
}
