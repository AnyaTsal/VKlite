package com.vklite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.model.Fragments;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.GroupItem;
import com.vklite.views.adapter.GroupListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends ListFragment {
    private Context context;
    private GroupListAdapter adapter;

    public GroupListFragment() {}

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<GroupItem> groupItems = new ArrayList<>();

        ((MainActivity)getActivity()).setToolbar(Fragments.GROUPS);

        adapter = new GroupListAdapter(groupItems, context);
        setListAdapter(adapter);

        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        getGroups();
        return view;
    }

    private void getGroups() {
        VKRequest request = new VKRequest("groups.get", VKParameters.from(
                VKApiConst.EXTENDED, 1));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                adapter.addAll(parseGroups(response.json));
            }
        });
    }

    ArrayList<GroupItem> parseGroups(JSONObject response) {
        ArrayList<GroupItem> groupsList = new ArrayList<>();
        JSONArray items;

        try {
            items = response.getJSONObject("response").getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                GroupItem groupItem = new GroupItem(items.getJSONObject(i));
                groupsList.add(groupItem);
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return groupsList;
    }
}
