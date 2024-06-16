package com.weloveyolo.moniguard.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.adapter.AlbumListAdapter;
import com.weloveyolo.moniguard.api.Guest;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class AlbumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Guest guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        guest = (Guest) getIntent().getSerializableExtra("guest");    // 访客
        String midPhotoUrl = getIntent().getStringExtra("photo"); // 主图url

        firstRender(midPhotoUrl);

        recyclerView = findViewById(R.id.album_area);

        // 设置布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 设置适配器
        List<String> urls = new ArrayList<>();
        for(int i=0;i<13;i++){
            urls.add("https://img2.baidu.com/it/u=3519725849,56459421&fm=253&fmt=auto&app=120&f=JPEG?w=590&h=331");
        }

        recyclerView.setAdapter(new AlbumListAdapter(urls));
        // 如果您希望在滚动时提高性能，可以添加以下设置
        recyclerView.setHasFixedSize(true);
    }

    public void firstRender(String midPhotoUrl){
        // 名字
        TextView textView = findViewById(R.id.album_name);
        textView.setText(guest.getName());

        // 地点


        // 日期+地点
        String locaAndDate = "";
        try {
            Pattern pattern = Pattern.compile("\\d{4}-(\\d{2})-(\\d{2})T");
            Matcher matcher = pattern.matcher(guest.getIsWhitelisted());
            if (matcher.find()) {
                locaAndDate  += matcher.group(1) + "月" + matcher.group(2) + "日开始出现在";
            }
        } catch (Exception e) {
            locaAndDate = "解析出错";
        }


        // 主图
        new Thread(() -> {
            Response response = HttpClient.get(midPhotoUrl, null);
            try {
                byte[] bytes = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedDrawable.setCircular(true);
                runOnUiThread(()->{
                    ImageView imageView = findViewById(R.id.mid_photo);
                    imageView.setImageDrawable(roundedDrawable);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getUrls(){
        IMoniGuardApi moniGuardApi = new MoniGuardApi();

    }

    public void toBack(View view){
        finish();
    }
}

