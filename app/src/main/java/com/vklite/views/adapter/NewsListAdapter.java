package com.vklite.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.R;
import com.vklite.model.NewsItem;
import com.vklite.model.PostLink;
import com.vklite.model.PhotoItem;

import org.json.JSONException;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {

    private List<NewsItem> newsItemList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView postAuthorImage;
        public TextView postAuthorName;
        public TextView postData;
        public TextView postText;
        public TextView postLikes;
        public TextView postComments;
        public TextView postReposts;
        public ImageView likesImage;
        public RecyclerView recyclerViewImages;
        public RecyclerView recyclerViewLinks;
        public View v;

        MyViewHolder(View view) {
            super(view);
            this.v = view;
            likesImage = view.findViewById(R.id.post_likes_image);
            postAuthorImage = view.findViewById(R.id.post_author_image);
            postAuthorName = view.findViewById(R.id.post_author_name);
            postData = view.findViewById(R.id.post_data);
            postText = view.findViewById(R.id.post_context);
            postComments = view.findViewById(R.id.post_comments);
            postLikes = view.findViewById(R.id.post_likes);
            postReposts = view.findViewById(R.id.post_reposts);
            recyclerViewImages = view.findViewById(R.id.post_images_list);
            recyclerViewLinks = view.findViewById(R.id.post_links_list);
        }
    }

    public NewsListAdapter(Context context, List<NewsItem> news) {
        this.context = context;
        this.newsItemList = news;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (newsItemList.get(position).isFavorite() == 1) {
            holder.likesImage.setColorFilter(Color.RED);
        }
        holder.postAuthorName.setText(newsItemList.get(position).getPostAuthorName());
        holder.postData.setText(newsItemList.get(position).getPostData());
        holder.postText.setText(newsItemList.get(position).getPostText());

        try {
            Picasso.with(context)
                    .load(newsItemList.get(position).getPostAuthorImageURL())
                    .into(holder.postAuthorImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.postLikes.setText(String.valueOf(newsItemList.get(position).getPostLikesCount()));
        holder.postComments.setText(newsItemList.get(position).getPostCommentsCount());
        holder.postReposts.setText(newsItemList.get(position).getPostRepostsCount());

        if (newsItemList.get(position).getImageList() != null && holder.recyclerViewImages != null) {
            List<PhotoItem> postImages = newsItemList.get(position).getImageList();
            ImagesAdapter imagesAdapter = new ImagesAdapter(context, postImages);
            holder.recyclerViewImages.setHasFixedSize(true);
            holder.recyclerViewImages.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerViewImages.setAdapter(imagesAdapter);
            holder.recyclerViewImages.setNestedScrollingEnabled(false);
        }
        else {
            assert holder.recyclerViewImages != null;
            holder.recyclerViewImages.setVisibility(View.GONE);
        }

        if (newsItemList.get(position).getLinkList() != null && holder.recyclerViewLinks != null) {
            List<PostLink> postLinks = newsItemList.get(position).getLinkList();
            LinksAdapter linksAdapter = new LinksAdapter(context, postLinks);
            holder.recyclerViewLinks.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerViewLinks.setAdapter(linksAdapter);
        }
        else {
            assert holder.recyclerViewLinks != null;
            holder.recyclerViewLinks.setVisibility(View.GONE);
        }

        holder.likesImage.setOnClickListener(v -> {
            if (newsItemList.get(position).isFavorite() == 0) {
                setLike(v, position, holder.postLikes);
            } else {
                deleteLike(v, position, holder.postLikes);
            }
        });
    }

    private void deleteLike(View view, int position, View textLikes) {
        VKRequest request = new VKRequest("likes.delete",
                VKParameters.from("type", "post",
                        "owner_id", newsItemList.get(position).getSourceId(),
                        "item_id", newsItemList.get(position).getPostId()));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                int likesCount = 0;
                try {
                    likesCount = response.json.getJSONObject("response").getInt("likes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newsItemList.get(position).postLikesCount = likesCount;
                newsItemList.get(position).isFavorite = 0;

                TextView likes = textLikes.findViewById(R.id.post_likes);
                likes.setText(String.valueOf(likesCount));

                ImageView imageView = view.findViewById(R.id.post_likes_image);
                imageView.setColorFilter(Color.WHITE);
            }
        });
    }

    private void setLike(View view, int position, View textLikes) {
        VKRequest request = new VKRequest("likes.add",
                VKParameters.from("type", "post",
                        "owner_id", newsItemList.get(position).getSourceId(),
                        "item_id", newsItemList.get(position).getPostId()));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                int likesCount = 0;
                try {
                    likesCount = response.json.getJSONObject("response").getInt("likes");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newsItemList.get(position).isFavorite = 1;
                newsItemList.get(position).postLikesCount = likesCount;

                TextView likes = textLikes.findViewById(R.id.post_likes);
                likes.setText(String.valueOf(likesCount));
                ImageView imageView = view.findViewById(R.id.post_likes_image);
                imageView.setColorFilter(Color.RED);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
    }
}