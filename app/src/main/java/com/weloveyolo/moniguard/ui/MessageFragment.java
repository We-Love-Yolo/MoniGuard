package com.weloveyolo.moniguard.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.util.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        insertMessage(new Message(3, "你好", "00:00", 1));
        selectAllMessages(3);
    }

    // 查询消息
    public void selectAllMessages(int userId){
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        List<Message> messages = new ArrayList<>();
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
    }

    // 插入消息
    public void insertMessage(Message message){
        if (message == null) return;
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("residentId", message.getResidentId());
        values.put("content", message.getContent());
        values.put("createdAt", message.getCreatedAt());
        values.put("type", message.getType());
        db.insert("message", null, values);
    }
}
