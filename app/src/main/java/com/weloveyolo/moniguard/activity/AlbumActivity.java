package com.weloveyolo.moniguard.activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.adapter.ScreenshotListAdapter;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_album1);

    }
    public void gotoDiscoverPage(View view){
        finish();
    }

}
