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
import com.vklite.model.User;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> friendItems;

    public FriendListAdapter(List<User> friendItems, Context context) {
        super(context, R.layout.item_friend, friendItems);
        this.context = context;
        this.friendItems = friendItems;
    }

    private class ViewHolder {
        TextView userNameTextView;
        ImageView userPhotoImageView;
    }

    @Override
    public int getCount() {
        return friendItems.size();
    }

    @Override
    public User getItem(int position) {
        return friendItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friendItems.indexOf(getItem(position));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        User friendItem = friendItems.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_friend, parent, false);

            holder = new ViewHolder();

            holder.userNameTextView = convertView.findViewById(R.id.friend_name);
            holder.userPhotoImageView = convertView.findViewById(R.id.friend_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userNameTextView.setText(friendItem.getName());

        Picasso.with(getContext())
                .load(friendItem.getImageURL())
                .placeholder(R.drawable.placeholder_person)
                .error(R.drawable.placeholder_person)
                .into(holder.userPhotoImageView);

        return convertView;
    }
}