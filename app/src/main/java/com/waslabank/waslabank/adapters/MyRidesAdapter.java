package com.waslabank.waslabank.adapters;

import android.content.Context;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.models.MyRideModel;
import com.waslabank.waslabank.networkUtils.Connector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.MyRideViewHolder> {


    ArrayList<MyRideModel> rides;
    Context context;
    OnItemClicked onItemClicked;
    Connector mConnector;
    private final String TAG = MyRidesAdapter.class.getSimpleName();

    public MyRidesAdapter(ArrayList<MyRideModel> rides, Context context, OnItemClicked onItemClicked) {
        this.rides = rides;
        this.context = context;
        this.onItemClicked = onItemClicked;
        mConnector = new Connector(context, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    Toast.makeText(context,"Started",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @NonNull
    @Override
    public MyRideViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_ride_item,viewGroup,false);
        return new MyRideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideViewHolder myRideViewHolder, int i) {
        myRideViewHolder.itemView.setTag(i);
        myRideViewHolder.car.setText(rides.get(i).getUser().getCarName());
        myRideViewHolder.fromDate.setText(rides.get(i).getRequestTime());
        myRideViewHolder.fromPlace.setText(rides.get(i).getAddress());
        myRideViewHolder.toDate.setText(rides.get(i).getRequestTime());
        myRideViewHolder.toPlace.setText(rides.get(i).getAddressTo());
        myRideViewHolder.name.setText(rides.get(i).getUser().getName());
        //myRideViewHolder.profileImage.setImageResource(rides.get(i).getImage());
        if (URLUtil.isValidUrl(rides.get(i).getUser().getImage()))
            Picasso.get().load(rides.get(i).getUser().getImage()).fit().centerCrop().into(myRideViewHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + rides.get(i).getUser().getImage()).fit().centerCrop().into(myRideViewHolder.profileImage);
        }
        //myRideViewHolder.seats.setText(rides.get(i).getNumOfSeats());

        myRideViewHolder.timeExpected.setText(rides.get(i).getRequestTime());
        myRideViewHolder.exactDate.setText(rides.get(i).getRequestDate());
        //myRideViewHolder.distance.setText(rides.get(i).getDistance());

        if (rides.get(i).getStatus().equals("1")){
            myRideViewHolder.state.setBackgroundResource(R.drawable.bg_rectangle_my_rides);
            myRideViewHolder.state.setText(context.getString(R.string.approved));

        } else {
            myRideViewHolder.state.setText(context.getString(R.string.pending));
            myRideViewHolder.state.setBackgroundResource(R.drawable.bg_rectangle_my_rides_orange);
        }

        if (rides.get(i).isUpcoming() && rides.get(i).getStart().equals("0")){
            myRideViewHolder.startRide.setVisibility(View.VISIBLE);
        } else {
            myRideViewHolder.startRide.setVisibility(View.INVISIBLE);
        }

        myRideViewHolder.startRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnector.getRequest(TAG,"http://www.cta3.com/waslabank/api/start_request?id=" + rides.get(i).getId());
                myRideViewHolder.startRide.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    class MyRideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        @BindView(R.id.distance)
        TextView distance;
        @BindView(R.id.exact_date)
        TextView exactDate;
        @BindView(R.id.time_expected)
        TextView timeExpected;
        @BindView(R.id.state)
        TextView state;
        @BindView(R.id.start_ride_button)
        Button startRide;

        MyRideViewHolder(View itemView) {
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
