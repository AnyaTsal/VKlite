package com.vklite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vklite.R;

public class LoginActivity extends AppCompatActivity {

    private String[] scope = {
            VKScope.MESSAGES,
            VKScope.GROUPS,
            VKScope.NOTIFY,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.OFFLINE,
            VKScope.STATUS,
            VKScope.FRIENDS,
            VKScope.VIDEO,
            VKScope.STATS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> loginAction());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                finish();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loginAction() {
        VKSdk.login(this, scope);
    }
}