package com.vklite.views.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.PostLink;
import com.vklite.views.MyItemClickListener;

import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.ViewHolder> {

    private List<PostLink> postLinkList;
    private Context context;

    LinksAdapter(Context context, List<PostLink> postLinkList) {
        this.postLinkList = postLinkList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_link, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.link.setText(postLinkList.get(position).getTitle());

        holder.setMyItemClickListener((view, position1) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((postLinkList.get(position).getUrl())));
            MainActivity activity = (MainActivity) context;
            activity.startActivity(browserIntent);
        });

    }

    @Override
    public int getItemCount() {
        return postLinkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView link;

        MyItemClickListener myItemClickListener;

        void setMyItemClickListener(MyItemClickListener myItemClickListener) {
            this.myItemClickListener = myItemClickListener;
        }
        ViewHolder(View itemView) {
            super(itemView);
            link = itemView.findViewById(R.id.post_link);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myItemClickListener.onItemClickListener(itemView, getAdapterPosition());
        }
    }
}