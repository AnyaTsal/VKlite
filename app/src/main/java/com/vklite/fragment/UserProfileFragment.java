package com.vklite.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    public UserProfileFragment() {}

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        getProfile();

        return view;
    }

    void getProfile() {
        VKRequest getUserInfo = new VKRequest("users.get", VKParameters.from(
                "user_ids", getArguments().getString("USER_ID"),
                "fields", "bdate,city,status,photo_max_orig,followers_count,contacts,counters,friends"));

        getUserInfo.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    parseUserProfile(response.json.getJSONArray("response").getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void parseUserProfile(JSONObject userInfo) {
        try {
            User user = new User(
                    userInfo.getString("photo_max_orig"),
                    userInfo.getString("first_name") + " " + userInfo.getString("last_name"),
                    userInfo.getLong("id")
            );
            ((TextView) getActivity().findViewById(R.id.profile_user_name)).setText(user.getName());

            ((MainActivity)getActivity()).setToolbar(user.getName());

            Picasso.with(getContext())
                    .load(user.getImageURL())
                    .placeholder(R.drawable.placeholder_person)
                    .error(R.drawable.placeholder_person)
                    .into((ImageView) getActivity().findViewById(R.id.profile_user_photo));

            if (userInfo.has("bdate") && !userInfo.getString("bdate").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_birthday)).append(userInfo.getString("bdate"));
            } else {
                getActivity().findViewById(R.id.profile_user_birthday).setVisibility(View.GONE);
                getActivity().findViewById(R.id.birthday).setVisibility(View.GONE);
            }

            if (userInfo.has("city") && !userInfo.getString("city").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_city)).append(userInfo.getJSONObject("city").getString("title"));
            } else {
                getActivity().findViewById(R.id.profile_user_city).setVisibility(View.GONE);
            }

            if (userInfo.has("status")) {
                ((TextView) getActivity().findViewById(R.id.profile_user_status)).append(userInfo.getString("status"));
            }
            else {
                getActivity().findViewById(R.id.profile_user_status).setVisibility(View.GONE);
            }

            if (userInfo.has("mobile_phone") && !userInfo.getString("mobile_phone").isEmpty()) {
                ((TextView) getActivity().findViewById(R.id.profile_user_phone_number)).append(userInfo.getString("mobile_phone"));
            } else {
                getActivity().findViewById(R.id.profile_user_phone_number).setVisibility(View.GONE);
                getActivity().findViewById(R.id.phone_number).setVisibility(View.GONE);
            }

            ((TextView) getActivity().findViewById(R.id.profile_user_followers)).append(userInfo.getString("followers_count"));
            ((TextView) getActivity().findViewById(R.id.profile_user_friends)).append(userInfo.getJSONObject("counters").getString("friends"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
    }
}