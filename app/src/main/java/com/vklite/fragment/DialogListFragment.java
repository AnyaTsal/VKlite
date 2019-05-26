package com.vklite.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vklite.model.Fragments;
import com.vklite.activity.MainActivity;
import com.vklite.R;
import com.vklite.model.Util;
import com.vklite.model.Message;
import com.vklite.model.DialogItem;
import com.vklite.model.User;
import com.vklite.views.adapter.DialogsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DialogListFragment extends ListFragment {
    public static final int NOTIFY_ID = 70000;

    private String new_pts;
    private String server;
    private String key;
    private String ts;

    private Context context;
    private Fragment fragment;
    private RequestQueue requestQueue;
    private ArrayList<DialogItem> dialogItems;
    private DialogsListAdapter adapter;
    private int dialogsCount;
    private int dialogOffset = 0;

    public static DialogListFragment newInstance() {
        return new DialogListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        assert context != null;
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int preLast;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (preLast != lastItem) {
                        if (dialogOffset < dialogsCount) {
                            dialogOffset += 15;
                            getDialogs();
                        }
                        preLast = lastItem;
                    }
                }
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> showDialog(position));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;

        ((MainActivity)getActivity()).setToolbar(Fragments.MESSAGES);

        fragment = this;
        dialogOffset = 0;
        dialogItems = new ArrayList<>();

        adapter = new DialogsListAdapter(dialogItems, context);
        setListAdapter(adapter);

        view = inflater.inflate(R.layout.fragment_messages, container, false);
        getDialogs();
        setUpLongPullServer();

        return view;
    }

    private void getDialogs() {
        VKRequest request = new VKRequest("messages.getConversations",
                VKParameters.from("extended", 1,
                        "count", 15,
                        "offset", dialogOffset));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                adapter.addAll(parseMessages(response.json));
                adapter.notifyDataSetChanged();
            }
        });
    }

    ArrayList<DialogItem> parseMessages(JSONObject response) {
        ArrayList<DialogItem> dialogItems = new ArrayList<>();
        try {
            dialogsCount = response.getJSONObject("response").getInt("count");
            JSONArray items = response.getJSONObject("response").getJSONArray("items");
            JSONArray profiles = response.getJSONObject("response").getJSONArray("profiles");

            for (int i = 0; i < items.length(); i++) {
                DialogItem dialogItem = new DialogItem(items.getJSONObject(i), profiles);
                dialogItems.add(dialogItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialogItems;
    }

    void showDialog(int position) {
        ChatFragment dialogsListFragment = ChatFragment.newInstance();

        DialogItem dialogItem = dialogItems.get(position);

        Bundle bundle = new Bundle();
        bundle.putSerializable("CURRENT_USER_ID", new User(
                "",
                "",
                Long.valueOf(VKAccessToken.currentToken().userId)));

        bundle.putSerializable(ChatFragment.CURRENT_USER_ID_DIALOG_WITH, new User(
                dialogItem.getImageURL(),
                dialogItem.getDialogName(),
                dialogItem.getUserID()));

        dialogsListFragment.setArguments(bundle);

        ((MainActivity)getActivity()).loadFragment(dialogsListFragment);
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
                }, error -> {
        });
        stringRequest.setTag(this);
        requestQueue.add(stringRequest);
    }

    void getLongPullHistory() {
        VKRequest vkRequest = new VKRequest(
                "execute",
                VKParameters.from("code", String.format(Util.executeCodeNotifications, ts, new_pts)));

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
                    JSONArray messages = vkResponse.getJSONArray("messages");
                    if (messages.length() != 0) {
                        List<Message> messageList = getReceivedMessagesList(messages);
                        sendNotification(messageList);
                        if (fragment != null && fragment.isVisible()) {
                            setDialogsWithNewMessages(messageList);
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

    private void setDialogsWithNewMessages(List<Message> messageList) {
        label:
        for (Message message : messageList) {
            for (int i = 0; i < dialogItems.size(); i++) {
                DialogItem dialogItem = dialogItems.get(i);
                if (dialogItem.getUserID() == message.getSender().getID()) {
                    dialogItems.remove(dialogItem);
                    dialogItem.setMessageTime(message.getTime());
                    dialogItem.setUserMessage(message.getMessage());
                    dialogItem.setImageURL(message.getSender().getImageURL());
                    dialogItem.setDialogName(message.getSender().getName());
                    dialogItems.add(0, dialogItem);
                    break label;
                }
            }
            dialogItems.add(0, new DialogItem(
                    message.getSender().getName(),
                    message.getMessage(),
                    message.getTime(),
                    message.getSender().getImageURL(),
                    message.getSender().getID()
            ));
        }
        adapter.notifyDataSetChanged();
    }

    private void sendNotification(List<Message> messageList) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        for (Message message : messageList) {
            builder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message.getSender().getName())
                    .setContentText(message.getMessage())
                    .setPriority(NotificationCompat.PRIORITY_MAX);
            mNotificationManager.notify(NOTIFY_ID, builder.build());
        }
    }

    void setLongPullServerParameters(JSONObject response) throws JSONException {
        new_pts = response.getString("pts");
        server = response.getString("server");
        key = response.getString("key");
        ts = response.getString("ts");
    }

    List<Message> getReceivedMessagesList(JSONArray messagesJSONArray) throws JSONException {
        List<Message> messageList = new ArrayList<>();

        for (int i = 0; i < messagesJSONArray.length(); i++) {
            JSONObject messageJSON = messagesJSONArray.getJSONObject(i);
            long userID;
            String dialogName;
            if (messageJSON.has("chat_id")) {
                userID = messageJSON.getLong("chat_id") + 2000000000;
                dialogName = messageJSON.getString("title");
            } else {
                userID = messageJSON.getLong("user_id");
                dialogName = messageJSON.getString("first_name") + " " + messageJSON.getString("last_name");
            }

            messageList.add(new Message(
                    messageJSON.getString("body"),
                    Util.convertTime(messageJSON.getLong("date")),
                    new User(
                            messageJSON.getString("photo_100"),
                            dialogName,
                            userID
                    )
            ));
        }
        return messageList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(this);
    }
}
