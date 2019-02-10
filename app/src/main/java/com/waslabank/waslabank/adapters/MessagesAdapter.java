package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.models.MessageModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<MessageModel> mMessagesModel;
    private OnItemClicked onItemClicked;


    public MessagesAdapter(Context mContext, ArrayList<MessageModel> mMessagesModel, OnItemClicked onItemClicked) {
        this.mContext = mContext;
        this.mMessagesModel = mMessagesModel;
        this.onItemClicked = onItemClicked;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessagesModel.get(position).isMine()) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_outgoing, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_incoming, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == 0){
            ((DateViewHolder)holder).mDate.setText(mMessagesModel.get(position).getMessage());
        } else {
            ((MessageViewHolder) holder).mMessageText.setText(mMessagesModel.get(position).getMessage());
            ((MessageViewHolder) holder).mMessageDate.setText(mMessagesModel.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return mMessagesModel.size();
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.message_text)
        TextView mMessageText;
        @BindView(R.id.message_date)
        TextView mMessageDate;

        MessageViewHolder (View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onItemClicked.setOnItemClicked(getAdapterPosition());
        }
    }


    public class DateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.message_date)
        TextView mDate;

        DateViewHolder (View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onItemClicked.setOnItemClicked(getAdapterPosition());
        }
    }

    public interface OnItemClicked{
        void setOnItemClicked(int position);
    }

}
