package com.vklite.views.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vklite.R;
import com.vklite.model.DialogItem;

import java.util.ArrayList;
import java.util.List;

public class DialogsListAdapter extends ArrayAdapter<DialogItem> {
    private Context context;
    private List<DialogItem> dialogItems;

    public DialogsListAdapter(ArrayList<DialogItem> dialogItems, Context context) {
        super(context, R.layout.item_dialogs_layout, dialogItems);
        this.context = context;
        this.dialogItems = dialogItems;
    }

    private class ViewHolder {
        TextView dialogNameTextView;
        TextView messageTextView;
        TextView messageTimeTextView;
        ImageView dialogPhotoImageView;
    }

    @Override
    public int getCount() {
        return dialogItems.size();
    }

    @Override
    public DialogItem getItem(int position) {
        return dialogItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dialogItems.indexOf(getItem(position));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        DialogItem messageItem = dialogItems.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_dialogs_layout, parent, false);

            holder = new ViewHolder();

            holder.dialogNameTextView = convertView.findViewById(R.id.preview_messages_name);
            holder.messageTextView = convertView.findViewById(R.id.preview_user_message);
            holder.messageTimeTextView = convertView.findViewById(R.id.preview_message_time);
            holder.dialogPhotoImageView = convertView.findViewById(R.id.preview_messages_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageTimeTextView.setText(messageItem.getMessageTime());
        holder.messageTextView.setText(messageItem.getUserMessage());
        holder.dialogNameTextView.setText(messageItem.getDialogName());
        Picasso.with(getContext())
                .load(messageItem.getImageURL())
                .placeholder(R.drawable.placeholder_person)
                .error(R.drawable.placeholder_person)
                .into(holder.dialogPhotoImageView);

        return convertView;
    }
}
