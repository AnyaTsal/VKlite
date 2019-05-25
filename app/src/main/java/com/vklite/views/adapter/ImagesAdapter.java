package com.vklite.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vklite.R;
import com.vklite.model.PhotoItem;
import com.vklite.views.MyItemClickListener;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<PhotoItem> postImageList;
    private Context context;

    ImagesAdapter(Context context, List<PhotoItem> postImageList) {
        this.postImageList = postImageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Picasso.with(context)
                    .load(postImageList.get(position).getPhotoURL())
                    .into(holder.previewImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setMyItemClickListener((view, position1) -> {
        });
    }

    @Override
    public int getItemCount() {
        return postImageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView previewImage;

        MyItemClickListener myItemClickListener;

        void setMyItemClickListener(MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }
        ViewHolder(View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.post_image_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myItemClickListener.onItemClickListener(itemView, getAdapterPosition());
        }
    }
}
