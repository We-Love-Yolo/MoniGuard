package com.weloveyolo.moniguard.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.adapter.MessageListAdapter;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.util.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private MessageListAdapter messageListAdapter;
    private RecyclerView messageList;
    private List<Message> messages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_message, container, false);
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        getMessage();

        messageList = view.findViewById(R.id.message_list);
        messageList.setLayoutManager(new GridLayoutManager(getContext(), 1));

        messageListAdapter = new MessageListAdapter(getContext(), messages);
        messageList.setAdapter(messageListAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getMessage() {
        int userId = ((MainActivity)requireActivity()).resident.getResidentId();
        selectAllMessages(userId);
        new Thread(() -> ((MainActivity)getActivity()).moniGuardApi.getAnalysisApi().getMessages((messages, success) -> {
            if (success) {
                messages.forEach(message->{
                    getActivity().runOnUiThread(()->{
                        insertMessage(message, userId);
                    });
                });
            }
        })).start();
    }

    // 查询消息
    public void selectAllMessages(int userId){
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        Cursor cursor = db.query("message",
                new String[]{"mid", "residentId", "content", "createdAt", "type"},
                "residentId = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int mid = cursor.getInt(cursor.getColumnIndexOrThrow("mid"));
                int residentId = cursor.getInt(cursor.getColumnIndexOrThrow("residentId"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                Message message = new Message(residentId, content, createdAt, type);
                messages.add(message);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        this.messages = messages;
    }

    // 插入消息
    public void insertMessage(Message message, int userId){
        if (message == null) return;
        messages.add(message);
        messageList.getAdapter().notifyItemInserted(messages.size() - 1);
        ContentValues values = new ContentValues();
        values.put("residentId", userId);
        values.put("content", message.getContent());
        values.put("createdAt", message.getCreatedAt());
        values.put("type", message.getType());
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        db.insert("message", null, values);
        db.close();
    }

}
