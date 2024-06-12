package com.weloveyolo.moniguard.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView mainPhoto;
    private TextView dateAndTime;
    private TextView photoPath;
    private ImageView strangePhoto;
    private TextView strangeName;
    private TextView deviceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);


        mainPhoto = findViewById(R.id.main_photo);
        dateAndTime = findViewById(R.id.date_time);
        photoPath = findViewById(R.id.img_path);
        strangePhoto = findViewById(R.id.stranger_photo);
        strangeName = findViewById(R.id.stranger_name);
        deviceName = findViewById(R.id.device_name);

//        fillMainPhoto();
        fillStrangerPhoto();
    }

    private void fillStrangerPhoto() {
        byte[] imgByte = new byte[]{};
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        strangePhoto.setImageBitmap(bitmap);
    }

    private void fillMainPhoto() {
        byte[] imgByte = new byte[]{};
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        mainPhoto.setImageBitmap(bitmap);
    }

    public void toBack(View view){
        finish();
    }
}
