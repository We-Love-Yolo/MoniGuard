package com.weloveyolo.moniguard.util;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.weloveyolo.moniguard.R;

public class CustomToast extends Toast {

    private Context context;
    private boolean isShow;

    public CustomToast(Context context) {
        super(context);
        this.context = context;
    }

    public void showSuccessToast(String text, int time) {
        showCustomToast(1, text, time);
    }

    public void showErrorToast(String text, int time) {
        showCustomToast(2, text, time);
    }

    private void showCustomToast(int type, String text, int time){
        if(isShow) return;
        int layoutId, textId;
        switch (type){
            case 2:
                layoutId = R.layout.toast_error;
                textId = R.id.text_toast_error;
                break;
            default:
                layoutId = R.layout.toast_success;
                textId = R.id.text_toast_success;
        }

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutId, null);
        // 设置文本
        TextView textView = layout.findViewById(textId);
        textView.setText(text);
        // 设置Toast的视图和位置
        setView(layout);
            setGravity(Gravity.CENTER, 0, 0);
        // 显示Toast
        isShow = true;
        show();
        // 模拟延时
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancel();
                isShow = false;
            }
        }, time);
    }
}
