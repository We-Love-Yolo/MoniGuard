package com.weloveyolo.moniguard.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.weloveyolo.moniguard.R;

public class Health_monitoring extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_monitoring);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 启用返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 返回按钮点击事件
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }
}
