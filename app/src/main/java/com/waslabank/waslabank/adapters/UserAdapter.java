package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    ArrayList<UserModel> users;
    Context context;
    FriendsAdapter.OnItemClicked onItemClicked;
    Connector mConnector;


    public UserAdapter(ArrayList<UserModel> users, Context context, FriendsAdapter.OnItemClicked onItemClicked) {
        this.users = users;
        this.context = context;
        this.onItemClicked = onItemClicked;
        mConnector = new Connector(context, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response))
                    Helper.showSnackBarMessage(context.getString(R.string.requested),(AppCompatActivity)context);
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });
    }


    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_item,viewGroup,false);
        return new UserAdapter.UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder userViewHolder, int i) {
        userViewHolder.name.setText(users.get(i).getName());

        if (URLUtil.isValidUrl(users.get(i).getImage()))
            Picasso.get().load(users.get(i).getImage()).fit().centerCrop().into(userViewHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + users.get(i).getImage()).fit().centerCrop().into(userViewHolder.profileImage);
        }

        userViewHolder.mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)view).setText(context.getString(R.string.requested));
                mConnector.getRequest("","http://www.cta3.com/waslabank/api/request_join?from_id=" + Helper.getUserSharedPreferences(context).getId() + "&user_id=" + users.get(userViewHolder.getAdapterPosition()).getId() + "&name=" + Helper.getUserSharedPreferences(context).getName());
            }
        });

    }


    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.profile_image)
        ImageView profileImage;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.add_friend)
        Button mAddFriendButton;


        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mAddFriendButton.setText(context.getString(R.string.link_account));
        }

        @Override
        public void onClick(View view) {
            onItemClicked.setOnItemClicked(getAdapterPosition());
        }
    }


    public interface OnItemClicked {
        void setOnItemClicked(int position);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


}
