package com.vklite.views.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vklite.R;
import com.vklite.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends ArrayAdapter<MenuItem> {

    private List<MenuItem> menuItems;
    private Context context;

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    public MenuAdapter(Context context, ArrayList<MenuItem> menuItems) {
        super(context, R.layout.item_menu, menuItems);

        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        MenuItem menuItem = menuItems.get(position);

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_menu, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.menu_text);
            viewHolder.imageView = convertView.findViewById(R.id.menu_image);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.imageView.setImageResource(menuItem.getImageID());
        viewHolder.textView.setText(menuItem.getItem());

        return convertView;
    }
}