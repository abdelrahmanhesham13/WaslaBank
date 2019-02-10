package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.models.ChatModel;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatHolder> {

    ArrayList<ChatModel> chats;
    Context mContext;
    OnItemClicked onItemClicked;

    public ChatsAdapter(ArrayList<ChatModel> chats, Context mContext, OnItemClicked onItemClicked) {
        this.chats = chats;
        this.mContext = mContext;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);
        return new ChatHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatHolder chatHolder, int i) {
        chatHolder.lastMessage.setText(chats.get(i).getLastMessage());
        chatHolder.name.setText(chats.get(i).getName());
        Helper.writeToLog(chats.get(i).getImage());
        if (URLUtil.isValidUrl(chats.get(i).getImage()))
            Picasso.get().load(chats.get(i).getImage()).fit().centerCrop().into(chatHolder.profileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + chats.get(i).getImage()).fit().centerCrop().into(chatHolder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public interface OnItemClicked {
        void setOnItemClicked(int position);
    }


    class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.profile_image)
        ImageView profileImage;
        @BindView(R.id.last_message)
        TextView lastMessage;

        ChatHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClicked.setOnItemClicked(getAdapterPosition());
        }
    }

}
