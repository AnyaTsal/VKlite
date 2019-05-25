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
import com.vklite.model.GroupItem;

import java.util.List;

public class GroupListAdapter extends ArrayAdapter<GroupItem> {
    private Context context;
    private List<GroupItem> groupItems;

    public GroupListAdapter(List<GroupItem> groupItems, Context context) {
        super(context, R.layout.item_group, groupItems);
        this.context = context;
        this.groupItems = groupItems;
    }

    private class ViewHolder {
        TextView groupNameTextView;
        ImageView groupPhotoImageView;
    }

    @Override
    public int getCount() {
        return groupItems.size();
    }

    @Override
    public GroupItem getItem(int position) {
        return groupItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return groupItems.indexOf(getItem(position));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        GroupItem groupItem = groupItems.get(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_group, parent, false);

            holder = new ViewHolder();

            holder.groupNameTextView = convertView.findViewById(R.id.group_name);
            holder.groupPhotoImageView = convertView.findViewById(R.id.group_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupNameTextView.setText(groupItem.getName());

        Picasso.with(getContext())
                .load(groupItem.getImageURL())
                .placeholder(R.drawable.placeholder_person)
                .error(R.drawable.placeholder_person)
                .into(holder.groupPhotoImageView);

        return convertView;
    }
}