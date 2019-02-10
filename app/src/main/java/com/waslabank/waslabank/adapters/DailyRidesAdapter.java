package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.ConfirmRideRequest;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.VerifyDriverAccountActivity;
import com.waslabank.waslabank.WhereYouGoActivity;
import com.waslabank.waslabank.models.DailyRideModel;
import com.waslabank.waslabank.models.RideModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyRidesAdapter extends RecyclerView.Adapter<DailyRidesAdapter.RideViewHolder> {

    ArrayList<DailyRideModel> rides;
    Context context;
    OnItemClicked onItemClicked;
    Connector mConnector;


    public DailyRidesAdapter(ArrayList<DailyRideModel> rides, Context context, OnItemClicked onItemClicked) {
        this.rides = rides;
        this.context = context;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_daily_ride_item, viewGroup, false);
        return new RideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder rideViewHolder, int i) {
        rideViewHolder.car.setText(Helper.getUserSharedPreferences(context).getCarName());
        //rideViewHolder.code.setText(rides.get(i).getCode());
        rideViewHolder.fromDate.setText(rides.get(i).getRequestTime());
        rideViewHolder.fromPlace.setText(rides.get(i).getAddress());
        rideViewHolder.toDate.setText(rides.get(i).getRequestTime());
        rideViewHolder.toPlace.setText(rides.get(i).getAddressTo());
        rideViewHolder.name.setText(Helper.getUserSharedPreferences(context).getName());
        rideViewHolder.mTime.setText(rides.get(i).getRequestTime());
        if (rides.get(i).getStatus().equals("1"))
            rideViewHolder.mActiveSwitch.setChecked(true);
        else
            rideViewHolder.mActiveSwitch.setChecked(false);
        if (URLUtil.isValidUrl(Helper.getUserSharedPreferences(context).getImage()))
            Picasso.get().load(Helper.getUserSharedPreferences(context).getImage()).fit().centerCrop().into(rideViewHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + Helper.getUserSharedPreferences(context).getImage()).fit().centerCrop().into(rideViewHolder.profileImage);
        }

        rideViewHolder.mActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mConnector.getRequest("Daily", "http://www.cta3.com/waslabank/api/add_daily?user_id=" + Helper.getUserSharedPreferences(context).getId() + "&longitude=" + rides.get(i).getLon() + "&latitude=" + rides.get(i).getLat()  + "&address=" + Uri.encode(rides.get(i).getAddress()) + "&time=" + Uri.encode(rides.get(i).getRequestTime()) + "&latitude_to=" + Uri.encode(rides.get(i).getLatTo()) +  "&address_to=" + Uri.encode(rides.get(i).getAddressTo()) + "&longitude_to=" + Uri.encode(rides.get(i).getLonTo()) + "&weekday=" + Uri.encode(rides.get(i).getWeekDay()) + "&id=" + rides.get(i).getId() + "&status=" + (b?1:0));
            }
        });

        mConnector = new Connector(context, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {

            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return rides.size();
    }


    class RideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.profile_image)
        ImageView profileImage;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.car)
        TextView car;
        @BindView(R.id.from_date)
        TextView fromDate;
        @BindView(R.id.from_place)
        TextView fromPlace;
        @BindView(R.id.to_date)
        TextView toDate;
        @BindView(R.id.to_place)
        TextView toPlace;
        @BindView(R.id.seats)
        TextView seats;
        @BindView(R.id.active)
        Switch mActiveSwitch;
        @BindView(R.id.time_expected)
        TextView mTime;

        RideViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClicked.setOnItemClicked(getAdapterPosition());
        }
    }

    public interface OnItemClicked {
        void setOnItemClicked(int position);
    }

}
