package com.vklite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.model.Fragments;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.VideoItem;
import com.vklite.views.adapter.VideoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VideoListFragment extends Fragment {

    VideoListAdapter adapter;
    RecyclerView recyclerView;
    List<VideoItem> videoItemList;
    Context context;

    public VideoListFragment() {
    }

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        videoItemList = new ArrayList<>();

        ((MainActivity)getActivity()).setToolbar(Fragments.VIDEOS);

        getVideos();

        adapter = new VideoListAdapter(videoItemList, getActivity());

        recyclerView = view.findViewById(R.id.video_list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(onItemClickListener);

        return view;
    }

    private View.OnClickListener onItemClickListener = view -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();

        int position = viewHolder.getAdapterPosition();
        VideoItem videoItem = videoItemList.get(position);
        VideoFragment videoFragment = VideoFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("url", videoItem.getVideoURl());
        videoFragment.setArguments(bundle);
        ((MainActivity)getActivity()).loadFragment(videoFragment);
    };

    private void getVideos() {
        VKRequest request = new VKRequest("video.get", VKParameters.from("extended", 1));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject responseVK = response.json.getJSONObject("response");
                    JSONArray videos = responseVK.getJSONArray("items");
                    videoItemList.addAll(parseVideos(videos));
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ArrayList<VideoItem> parseVideos(JSONArray videos) {
        ArrayList<VideoItem> videoItemList = new ArrayList<>();

        for (int i = 0; i < videos.length(); i++) {
            try {
                VideoItem item = new VideoItem(videos.getJSONObject(i));
                videoItemList.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return videoItemList;
    }
}