package com.weloveyolo.moniguard.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            toHome();
        }
    }

    public void buttonLoginOnClick(View v) {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
    }

    private void toHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("ApplySharedPref")
    private void setPersist(HashMap<String, String> hash) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", hash.get("phone"));
        editor.putString("token", hash.get("token"));
        editor.putBoolean("isLogin", true);
        editor.commit();
    }
}
