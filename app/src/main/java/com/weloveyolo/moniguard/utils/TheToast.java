package com.weloveyolo.moniguard.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weloveyolo.moniguard.R;

public class TheToast {

    private Activity ac;
    private ViewGroup container;
    private boolean showing;

    public TheToast(Activity ac, int id) {
        this.ac = ac;
        container = (ViewGroup) ac.findViewById(id);
        showing = false;
    }

    public void showSuccessToast(String content){
        if(!showing){
            showing = true;
            View toastView = ac.getLayoutInflater().inflate(R.layout.toast_success, container);
            TextView tv = toastView.findViewById(R.id.text_toast_success);
            tv.setText(content);
            stopShow(1000);
        }
    }

    public void showErrorToast(String content){
        if(!showing){
            showing = true;
            View toastView = ac.getLayoutInflater().inflate(R.layout.toast_error, container);
            TextView tv = toastView.findViewById(R.id.text_toast_error);
            tv.setText(content);
            stopShow(1000);
        }
    }

    private void stopShow(int ms) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                container.removeAllViews();
                showing = false;
            }
        }, ms);
    }
}
