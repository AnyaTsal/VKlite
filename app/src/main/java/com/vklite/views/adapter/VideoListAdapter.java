package com.vklite.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vklite.R;
import com.vklite.model.VideoItem;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    private List<VideoItem> videoItemList;
    private Context context;
    private View.OnClickListener mOnItemClickListener;

    public VideoListAdapter(List<VideoItem> videoItemList, Context context) {
        this.videoItemList = videoItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_preview, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.videoTitle.setText(videoItemList.get(position).getVideoTitle());
        holder.videoViews.setText(videoItemList.get(position).getVideoViews());
        try {
            Picasso.with(context)
                    .load(videoItemList.get(position).getVideoPreviewImageURL())
                    .into(holder.videoPreviewImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        TextView videoViews;
        ImageView videoPreviewImage;

        ViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.video_title);
            videoViews = itemView.findViewById(R.id.video_views);
            videoPreviewImage = itemView.findViewById(R.id.video_image);
            /*videoPreviewImage.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoItemList.get(selectedPosition).getVideoURl()));
                intent.setDataAndType(Uri.parse(videoItemList.get(selectedPosition).getVideoURl()), "video/*");
                MainActivity activity = (MainActivity) context;
                activity.startActivity(intent);
            });*/

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}