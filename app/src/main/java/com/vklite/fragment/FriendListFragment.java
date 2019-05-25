package com.vklite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.model.Fragments;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.User;
import com.vklite.views.adapter.FriendListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends ListFragment {

    private Context context;
    private FriendListAdapter adapter;

    public FriendListFragment() {}

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.friends_action_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                VKRequest deleteFriendRequest = new VKRequest("friends.delete", VKParameters.from(
                        VKApiConst.USER_ID, adapter.getItem(info.position).getID()));
                deleteFriendRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        adapter.remove(adapter.getItem(info.position));
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            case R.id.menu_blacklist:
                VKRequest banFriendRequest = new VKRequest("account.ban", VKParameters.from(
                        VKApiConst.OWNER_ID, adapter.getItem(info.position).getID()));
                banFriendRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        adapter.remove(adapter.getItem(info.position));
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getContext();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<User> friendItems = new ArrayList<>();

        ((MainActivity)getActivity()).setToolbar(Fragments.FRIENDS);

        adapter = new FriendListAdapter(friendItems, context);
        setListAdapter(adapter);

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        getFriends();
        return view;
    }

    private void getFriends() {
        VKRequest request = new VKRequest("friends.get", VKParameters.from(
                "order", "hints",
                VKApiConst.FIELDS, "photo_100"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                adapter.addAll(parseFriends(response.json));
            }
        });
    }

    ArrayList<User> parseFriends(JSONObject response) {
        ArrayList<User> friendsList = new ArrayList<>();
        JSONArray items;
        try {
            items = response.getJSONObject("response").getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                User friendItem = new User(items.getJSONObject(i));
                friendsList.add(friendItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendsList;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        registerForContextMenu(listView);
        listView.setOnItemClickListener((parent, view, position, id) -> showUserProfile(position));
    }

    void showUserProfile(int position) {

        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance();

        User friendItem = adapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", String.valueOf(friendItem.getID()));

        userProfileFragment.setArguments(bundle);
        loadFragment(userProfileFragment);
    }

    private void loadFragment(Fragment fragment) {
        ((MainActivity)getActivity()).loadFragment(fragment);
    }
}
