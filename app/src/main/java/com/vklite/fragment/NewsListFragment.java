package com.vklite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.vklite.model.NewsItem;
import com.vklite.views.DividerItemView;
import com.vklite.views.adapter.NewsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment {

    NewsListAdapter adapter;
    RecyclerView recyclerView;
    List<NewsItem> newsItemList;
    RecyclerView.LayoutManager layoutManager;
    Context context;
    String startFrom = null;

    public NewsListFragment() {
    }

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getContext();
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsItemList = new ArrayList<>();

        ((MainActivity)getActivity()).setToolbar(Fragments.NEWS);

        recyclerView = view.findViewById(R.id.list_news);
        recyclerView.addItemDecoration(new DividerItemView(getActivity()));
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NewsListAdapter(context, newsItemList);
        recyclerView.setAdapter(adapter);
        getNews();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    getNews();
                }
            }
        });
        return view;
    }

    private void getNews() {
        VKRequest request;
        request = new VKRequest("newsfeed.get", VKParameters.from(
                    "start_from", startFrom,
                    "filters", "post"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject responseVK = response.json.getJSONObject("response");
                    startFrom = responseVK.getString("next_from");
                    newsItemList.addAll(parseNews(responseVK));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ArrayList<NewsItem> parseNews(JSONObject responseVK) {
        ArrayList<NewsItem> newsList = new ArrayList<>();
        try {
            JSONArray items = responseVK.getJSONArray("items");
            JSONArray profiles = responseVK.getJSONArray("profiles");
            JSONArray groups = responseVK.getJSONArray("groups");

            for (int i = 0; i < items.length(); i++) {
                try {
                    NewsItem newsItem = new NewsItem(items.getJSONObject(i), groups, profiles);
                    newsList.add(newsItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }
}