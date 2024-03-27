package com.weloveyolo.moniguard.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;

public class DiscoverFragmentActivity extends AppCompatActivity {

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_discover);

        Button button1 = findViewById(R.id.smart_photo_album);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscoverFragmentActivity.this, AlbumActivity.class);
                Toast.makeText(DiscoverFragmentActivity.this, "You clicked", Toast.LENGTH_SHORT).show();
                String data="123";
                Log.d("SecondActivity",data);
                startActivity(intent);
            }
        });
    }
}
