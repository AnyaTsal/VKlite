package com.vklite.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vklite.model.Fragments;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.MenuItem;
import com.vklite.views.adapter.MenuAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuListFragment extends ListFragment {

    public ArrayList<MenuItem> menuItems = null;
    private Context context;
    public MenuListFragment() {}

    public static MenuListFragment newInstance() {
        return new MenuListFragment();
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
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        try {
            menuItems = new ArrayList<>();
            menuItems.addAll(Arrays.asList(MenuItem.items));

            MenuAdapter menuAdapter = new MenuAdapter(context, menuItems);
            ((MainActivity)getActivity()).setToolbar(Fragments.MENU);
            setListAdapter(menuAdapter);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        view = inflater.inflate(R.layout.fragment_menu, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        String item = menuItems.get(position).getItem();
        switch (item) {
            case "Friends":
                loadFragment(FriendListFragment.newInstance());
                break;
            case "Groups":
                loadFragment(GroupListFragment.newInstance());
                break;
            case "Photos":
                loadFragment(PhotoListFragment.newInstance());
                break;
            case "Videos":
                loadFragment(VideoListFragment.newInstance());
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        ((MainActivity)getActivity()).loadFragment(fragment);
    }
}