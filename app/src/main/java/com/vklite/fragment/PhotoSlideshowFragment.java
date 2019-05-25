package com.vklite.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.vklite.model.PhotoItem;

import org.json.JSONException;

import java.util.ArrayList;

public class PhotoSlideshowFragment extends DialogFragment implements View.OnClickListener {
    private String TAG = PhotoSlideshowFragment.class.getSimpleName();
    private ArrayList<PhotoItem> images;
    private ViewPager viewPager;
    private TextView count, likes, reposts;
    private ImageView likesImage;
    private int selectedPosition = 0;

    public static PhotoSlideshowFragment newInstance() {
        return new PhotoSlideshowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = v.findViewById(R.id.viewpager);
        count = v.findViewById(R.id.count);
        likes = v.findViewById(R.id.photo_likes);
        reposts = v.findViewById(R.id.photo_reposts);
        likesImage = v.findViewById(R.id.photo_likes_image);

        likesImage.setOnClickListener(this);

        images = (ArrayList<PhotoItem>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        myViewPagerAdapter.notifyDataSetChanged();

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    @SuppressLint("SetTextI18n")
    private void displayMetaInfo(int position) {
        count.setText((position + 1) + " of " + images.size());

        PhotoItem image = images.get(position);
        likes.setText(String.valueOf(images.get(position).getPhotoLikesCount()));
        if (images.get(position).isFavorite() == 1) {
            likesImage.setColorFilter(Color.RED);
        }
        else {
            likesImage.setColorFilter(Color.WHITE);
        }
        reposts.setText(image.getPhotoRepostsCount());
        selectedPosition = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onClick(View v) {
       if (images.get(selectedPosition).isFavorite == 1) {
           deleteLike(v);
       }
       else {
           setLike(v);
       }
    }

    private void deleteLike(View view) {
        VKRequest request = new VKRequest("likes.delete",
                VKParameters.from("type", "photo",
                        "item_id", images.get(selectedPosition).getPhotoId()));
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
                images.get(selectedPosition).photoLikesCount = likesCount;
                likes.setText(String.valueOf(likesCount));
                ImageView likesImage = view.findViewById(R.id.photo_likes_image);
                images.get(selectedPosition).isFavorite = 0;
                likesImage.setColorFilter(Color.WHITE);
            }
        });
    }

    private void setLike(View view) {
        VKRequest request = new VKRequest("likes.add",
                VKParameters.from("type", "photo",
                        "item_id", images.get(selectedPosition).getPhotoId()));
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
                images.get(selectedPosition).photoLikesCount = likesCount;
                ImageView likesImage = view.findViewById(R.id.photo_likes_image);
                likes.setText(String.valueOf(likesCount));
                images.get(selectedPosition).isFavorite = 1;
                likesImage.setColorFilter(Color.RED);
            }
        });
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {}

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = view.findViewById(R.id.image_preview);
            PhotoItem image = images.get(position);
            Picasso.with(getActivity()).load(image.getPhotoURL()).into(imageViewPreview);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}