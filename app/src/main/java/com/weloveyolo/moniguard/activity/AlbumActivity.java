package com.weloveyolo.moniguard.activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.weloveyolo.moniguard.R;
import androidx.appcompat.widget.Toolbar;

public class AlbumActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_album1);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // 启用返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // 返回按钮点击事件
            toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

}
