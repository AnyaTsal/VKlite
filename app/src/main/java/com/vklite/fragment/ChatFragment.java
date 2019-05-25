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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.Util;
import com.vklite.model.Message;
import com.vklite.model.User;
import com.vklite.views.adapter.UserChatAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    public static final String CURRENT_USER_ID_DIALOG_WITH = "CURRENT_USER_ID_DIALOG_WITH";

    private String new_pts;
    private String server;
    private String key;
    private String ts;

    private Context context;
    private RequestQueue requestQueue;
    private RecyclerView messagesRecyclerView;
    private ArrayList<Message> dialogMessages;
    private UserChatAdapter userChatAdapter;
    private LinearLayoutManager linearLayoutManager;

    private User currentUserID;
    private User currentUserIdDialogWith;

    int countOfMessages;
    int messagesOffset = 0;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        assert context != null;
        requestQueue = Volley.newRequestQueue(context);
        dialogMessages = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        ((MainActivity)getActivity()).hideBottomNavigation();

        currentUserID = (User) getArguments().getSerializable(CURRENT_USER_ID);
        currentUserIdDialogWith = (User) getArguments().getSerializable(CURRENT_USER_ID_DIALOG_WITH);

        getMessageHistoryVK();

        userChatAdapter = new UserChatAdapter(context, dialogMessages, currentUserID);

        messagesRecyclerView = view.findViewById(R.id.recyclerview_message_list);
        messagesRecyclerView.setAdapter(userChatAdapter);
        ((MainActivity)getActivity()).setToolbar(currentUserIdDialogWith.getName());

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);

        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        setUpLongPullServer();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.button_chatbox_send).setOnClickListener(new View.OnClickListener() {
            EditText editText = getActivity().findViewById(R.id.edittext_chatbox);
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                if (!message.matches("[\\s]*")) {
                    VKRequest vkRequest = new VKRequest("messages.send", VKParameters.from(
                            "user_ids", currentUserIdDialogWith.getID(),
                            "message", message
                    ));
                    vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                        }
                    });
                    editText.setText("");
                }
            }
        });
        messagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    if (messagesOffset < countOfMessages) {
                        messagesOffset += 20;

                        getMessageHistoryVK();
                    }
                }
            }
        });
    }

    void getMessageHistoryVK() {
        VKRequest vkRequest = new VKRequest("messages.getHistory",
                VKParameters.from(
                        VKApiConst.OFFSET, messagesOffset,
                        VKApiConst.COUNT, 20,
                        VKApiConst.USER_ID, currentUserIdDialogWith.getID()));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject responseVK = response.json.getJSONObject("response");
                    countOfMessages = responseVK.getInt("count");
                    JSONArray messages = responseVK.getJSONArray("items");
                    dialogMessages.addAll(0, parseVKMessages(messages));
                    messagesRecyclerView.getAdapter().notifyDataSetChanged();
                    linearLayoutManager.scrollToPositionWithOffset(20, 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ArrayList<Message> parseVKMessages(JSONArray messagesJSONArray) {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            for (int i = 0; i < messagesJSONArray.length(); i++) {
                JSONObject message = messagesJSONArray.getJSONObject(i);
                User userFrom;
                if (message.getLong("from_id") == currentUserID.getID()) {
                    userFrom = currentUserID;
                } else {
                    userFrom = currentUserIdDialogWith;
                }

                messages.add(0, new Message(
                        message.getString("body"),
                        Util.convertTime(message.getLong("date")),
                        userFrom
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

    void setUpLongPullServer() {
        VKRequest vkRequest = new VKRequest("messages.getLongPollServer", VKParameters.from("need_pts", 1));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject vkResponse = response.json.getJSONObject("response");
                    setLongPullServerParameters(vkResponse);
                    startListenLongPullServer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void startListenLongPullServer() {
        String request = String.format(Util.longPullServerRequest, server, key, ts);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                request,
                response -> {
                    try {
                        JSONObject responseJSON = new JSONObject(response);
                        getLongPullHistory();
                        ts = responseJSON.getString("ts");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show());
        stringRequest.setTag(this);
        requestQueue.add(stringRequest);
    }

    void getLongPullHistory() {
        VKRequest vkRequest = new VKRequest(
                "messages.getLongPollHistory",
                VKParameters.from(
                        "ts", String.valueOf(Integer.parseInt(ts) - 1),
                        "pts", new_pts));

        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onComplete(VKResponse response) {
                try {
                    super.onComplete(response);
                    JSONObject vkResponse = response.json.getJSONObject("response");
                    JSONArray messages = vkResponse.getJSONObject("messages").getJSONArray("items");
                    if (messages.length() != 0) {
                        Message message = getMessageThatSentUserInDialog(messages);
                        if (message != null) {
                            dialogMessages.add(message);
                            userChatAdapter.notifyItemChanged(dialogMessages.size() - 1);
                            linearLayoutManager.scrollToPosition(dialogMessages.size() - 1);
                        }
                    }
                    new_pts = vkResponse.getString("new_pts");
                    startListenLongPullServer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void setLongPullServerParameters(JSONObject response) throws JSONException {
        new_pts = response.getString("pts");
        server = response.getString("server");
        key = response.getString("key");
        ts = response.getString("ts");
    }

    JSONObject findMessageThatUserSent(JSONArray messages) throws JSONException {
        for (int i = 0; i < messages.length(); i++) {
            JSONObject message = messages.getJSONObject(i);
            if (message.getLong("user_id") == currentUserIdDialogWith.getID() ||
                    (message.has("chat_id") && (message.getLong("chat_id") + 2000000000) == currentUserIdDialogWith.getID())) {
                return message;
            }
        }
        return null;
    }

    Message getMessageThatSentUserInDialog(JSONArray messagesJSONArray) throws JSONException {
        Message message = null;
        JSONObject messageJSON = findMessageThatUserSent(messagesJSONArray);

        if (messageJSON != null) {
            User user;
            if (messageJSON.getInt("out") == 0) {
                user = currentUserIdDialogWith;
            } else {
                user = currentUserID;
            }
            message = new Message(
                    messageJSON.getString("body"),
                    Util.convertTime(messageJSON.getLong("date")),
                    user
            );
        }
        return message;
    }

    @Override
    public void onStop() {
        super.onStop();
        requestQueue.cancelAll(this);
        ((MainActivity)getActivity()).showBottomNavigation();
    }
}