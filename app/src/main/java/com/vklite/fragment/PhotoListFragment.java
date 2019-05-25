package com.vklite.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.vklite.model.PhotoItem;
import com.vklite.views.adapter.PhotoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

public class PhotoListFragment extends Fragment {

    PhotoListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<PhotoItem> photoItemList;

    public PhotoListFragment() {
    }

    public static PhotoListFragment newInstance() {
        return new PhotoListFragment();
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        photoItemList = new ArrayList<>();

        adapter = new PhotoListAdapter(getActivity(), photoItemList);

        recyclerView = view.findViewById(R.id.photo_list);
        recyclerView.setAdapter(adapter);

        ((MainActivity)getActivity()).setToolbar(Fragments.PHOTOS);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new PhotoListAdapter.RecyclerTouchListener(getApplicationContext(),
                recyclerView,
                new PhotoListAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", photoItemList);
                bundle.putInt("position", position);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                PhotoSlideshowFragment newFragment = PhotoSlideshowFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        getPhotos();
        return view;
    }

    private void getPhotos() {
        VKRequest request = new VKRequest("photos.getAll",
                VKParameters.from("extended", 1));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject responseVK = response.json.getJSONObject("response");
                    JSONArray photos = responseVK.getJSONArray("items");
                    photoItemList.addAll(parsePhotos(photos));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ArrayList<PhotoItem> parsePhotos(JSONArray photos) {
        ArrayList<PhotoItem> photoItemList = new ArrayList<>();

        for (int i = 0; i < photos.length(); i++) {
            try {
                PhotoItem item = new PhotoItem(photos.getJSONObject(i));
                photoItemList.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return photoItemList;
    }
}