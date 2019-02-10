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

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.UserViewHolder> {


    ArrayList<UserModel> users;
    Context context;
    OnItemClicked onItemClicked;
    int type;
    Connector mConnector;


    public FriendsAdapter(ArrayList<UserModel> users, Context context, OnItemClicked onItemClicked, int type) {
        this.users = users;
        this.context = context;
        this.onItemClicked = onItemClicked;
        this.type = type;
        mConnector = new Connector(context, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response))
                Helper.showSnackBarMessage(Connector.getMessage(response),(AppCompatActivity)context);
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_item,viewGroup,false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        userViewHolder.name.setText(users.get(i).getName());
        if (type == 1){
            userViewHolder.mAddFriendButton.setVisibility(View.VISIBLE);
        } else {
            userViewHolder.mAddFriendButton.setVisibility(View.GONE);
        }
        if (URLUtil.isValidUrl(users.get(i).getImage()))
            Picasso.get().load(users.get(i).getImage()).fit().centerCrop().into(userViewHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + users.get(i).getImage()).fit().centerCrop().into(userViewHolder.profileImage);
        }

        if (users.get(i).isFriend()){
            userViewHolder.mAddFriendButton.setText("Un friend");
        } else {
            userViewHolder.mAddFriendButton.setText("Add friend");
        }

        userViewHolder.mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (users.get(i).isFriend()) {
                    users.get(i).setFriend(false);
                } else {
                    users.get(i).setFriend(true);
                }
                notifyItemChanged(i);
                mConnector.getRequest("","http://www.cta3.com/waslabank/api/add_friend?from_id=" + Helper.getUserSharedPreferences(context).getId() + "&to_id=" + users.get(userViewHolder.getAdapterPosition()).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
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
