package com.vklite.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.vk.sdk.VKSdk;
import com.vklite.R;
import com.vklite.fragment.DialogListFragment;
import com.vklite.fragment.MenuListFragment;
import com.vklite.fragment.NewsListFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    BottomNavigationView navigation;

    public void setToolbar(String toolbar) {
        this.toolbar.setTitle(toolbar);
    }

    public void hideBottomNavigation() {
       navigation.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        navigation.setVisibility(View.VISIBLE);
    }

    public void loadFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            loadFragment(NewsListFragment.newInstance());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_news:
                loadFragment(NewsListFragment.newInstance());
                return true;
            case R.id.action_dialogs:
                loadFragment(DialogListFragment.newInstance());
                return true;
            case R.id.action_menu:
                loadFragment(MenuListFragment.newInstance());
                return true;
        }
        return false;
    }
}