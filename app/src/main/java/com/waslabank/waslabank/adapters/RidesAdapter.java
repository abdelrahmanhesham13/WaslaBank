package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.ConfirmRideRequest;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.VerifyDriverAccountActivity;
import com.waslabank.waslabank.WhereYouGoActivity;
import com.waslabank.waslabank.models.NotificationModel;
import com.waslabank.waslabank.models.RideModel;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.RideViewHolder> {

    ArrayList<RideModel> rides;
    Context context;
    OnItemClicked onItemClicked;


    public RidesAdapter(ArrayList<RideModel> rides, Context context, OnItemClicked onItemClicked) {
        this.rides = rides;
        this.context = context;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_item, viewGroup, false);
        return new RideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder rideViewHolder, int i) {
        rideViewHolder.car.setText(rides.get(i).getUser().getCarName());
        //rideViewHolder.code.setText(rides.get(i).getCode());
        rideViewHolder.fromDate.setText(rides.get(i).getRequestTime());
        rideViewHolder.fromPlace.setText(rides.get(i).getAddress());
        rideViewHolder.toDate.setText(rides.get(i).getRequestTime());
        rideViewHolder.toPlace.setText(rides.get(i).getAddressTo());
        rideViewHolder.name.setText(rides.get(i).getUser().getName());
        rideViewHolder.seats.setText(String.format(Locale.ENGLISH,"%.2f KM", Float.valueOf(rides.get(i).getDistance())));
        rideViewHolder.code.setText(rides.get(i).getSeats() + " Seats");
        if (rides.get(i).getStart().equals("0")){
            rideViewHolder.status.setText("Not Started Yet");
        } else {
            rideViewHolder.status.setText("Started");
        }
        rideViewHolder.duration.setText(rides.get(i).getDuration());
        if (URLUtil.isValidUrl(rides.get(i).getUser().getImage()))
            Picasso.get().load(rides.get(i).getUser().getImage()).fit().centerCrop().into(rideViewHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + rides.get(i).getUser().getImage()).fit().centerCrop().into(rideViewHolder.profileImage);
        }
        if (!rides.get(i).getUser().getRating().isEmpty() && !rides.get(i).getUser().getRating().equals("null"))
            rideViewHolder.rating.setRating(Float.parseFloat(rides.get(i).getUser().getRating()));
        rideViewHolder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.getUserSharedPreferences(context).getStatus().equals("0")) {
                    context.startActivity(new Intent(context, VerifyDriverAccountActivity.class));
                } else {
                    context.startActivity(new Intent(context, ConfirmRideRequest.class).putExtra("request", rides.get(rideViewHolder.getAdapterPosition())).putExtra("type","request"));
                }
            }
        });
        //rideViewHolder.numberOfReviews.setText(rides.get(i).getNumOfReviews());
        //rideViewHolder.seats.setText(rides.get(i).getNumOfSeats());
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
        @BindView(R.id.rating_bar)
        RatingBar rating;
        @BindView(R.id.number_of_reviews)
        TextView numberOfReviews;
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
        @BindView(R.id.code)
        TextView code;
        @BindView(R.id.request_ride_button)
        Button requestButton;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.duration)
        TextView duration;

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
