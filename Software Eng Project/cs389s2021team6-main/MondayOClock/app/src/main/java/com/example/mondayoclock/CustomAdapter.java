package com.example.mondayoclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<MessageModel> list;

    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;

    public CustomAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV,dateTV;
        EditText message;
        MessageViewHolder(final View itemView) {
            super(itemView);

            messageTV = itemView.findViewById(R.id.message_text);
            dateTV = itemView.findViewById(R.id.date_text);
            message = itemView.findViewById(R.id.type_message);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = viewType == MESSAGE_TYPE_IN ? R.layout.item_text_in: R.layout.item_text_out;
        return new MessageViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        MessageModel messageModel = list.get(position);

        messageViewHolder.messageTV.setText(messageModel.message);
        messageViewHolder.dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        return list.get(position).isOwn? 2:1;
    }
}
