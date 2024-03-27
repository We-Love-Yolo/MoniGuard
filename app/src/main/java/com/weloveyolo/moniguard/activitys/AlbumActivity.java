package com.weloveyolo.moniguard.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.ui.discover.DiscoverFragment;

public class AlbumActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_album1);

        // 这里可以添加 AlbumActivity 的其他逻辑和布局
    }
}
