package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class ScenesAdapter extends ArrayAdapter<String> {
    private String[] scenes;

    public ScenesAdapter(Context context, int resource,String[] scenes) {
        super(context, resource,scenes);
        this.scenes=scenes;
    }
}
