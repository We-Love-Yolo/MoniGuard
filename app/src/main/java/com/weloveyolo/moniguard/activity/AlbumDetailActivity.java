package com.weloveyolo.moniguard.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.BoringLayout;
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
import com.weloveyolo.moniguard.api.Photo;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class AlbumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> urls;
    private Guest guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);


        guest = (Guest) getIntent().getSerializableExtra("guest");    // 访客
        String midPhotoUrl = getIntent().getStringExtra("photoUrl"); // 主图url
        String sceneName = getIntent().getStringExtra("sceneName"); // 场景名
        Boolean isWhitelisted = getIntent().getBooleanExtra("isWhitelisted", false);

        recyclerView = findViewById(R.id.album_area);

        // 设置布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 设置适配器
        urls = new ArrayList<>();

        recyclerView.setAdapter(new AlbumListAdapter(urls));
        // 如果您希望在滚动时提高性能，可以添加以下设置
        recyclerView.setHasFixedSize(true);


        try {
            firstRender(midPhotoUrl, sceneName, isWhitelisted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderPhotos();
    }

    public void firstRender(String midPhotoUrl, String sceneName, boolean isWhitelisted) throws Exception {
        // 名字
        TextView nameView = findViewById(R.id.album_name);
        nameView.setText(guest.getName());

        // 日期+地点
        String locaAndDate = "";
        String personType = isWhitelisted ? "的熟人" : "的怪人";
        try {
            Pattern pattern = Pattern.compile("\\d{4}-(\\d{2})-(\\d{2})T");
            Matcher matcher = pattern.matcher(guest.getCreatedAt());
            if (matcher.find()) {
                locaAndDate  += matcher.group(1) + "月" + matcher.group(2) + "日开始出现在" + sceneName + personType;
            }
        } catch (Exception e) {
            locaAndDate = "暂无行踪信息" + personType;
        }
        TextView introView = findViewById(R.id.album_intro);
        introView.setText(locaAndDate);


        // 主图
        new Thread(() -> {
            Response response = HttpClient.get(midPhotoUrl, null);
            try {
                byte[] bytes = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                runOnUiThread(()->{
                    ImageView imageView = findViewById(R.id.mid_photo);
                    imageView.setImageBitmap(bitmap);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void renderPhotos() {
        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        new Thread(()->{
            moniGuardApi.getAnalysisApi().getPhotos((photoList, success1)->{
                if (success1) {
                    photoList.forEach(photo -> {
                        String photoUrl = moniGuardApi.getBaseUrl() + "/Analysis"
                                + "/GetPhoto/" + photo.getPhotoId();
                        urls.add(photoUrl);
                        runOnUiThread(()->{
                            recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                        });
                    });
                } else {
                    // 测试数据
                    runOnUiThread(()->{
                        urls.add("https://img95.699pic.com/photo/50131/2108.jpg_wh300.jpg!/fh/300/quality/90");
                        recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                        urls.add("https://pic88.ibaotu.com/preview/2020/10/08/16021107295f7e4509651ed.jpg!wgi");
                        recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                        urls.add("https://img95.699pic.com/photo/50116/0506.jpg_wh300.jpg!/fh/300/quality/90");
                        recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                        urls.add("https://img.shetu66.com/2023/06/21/1687327028476920.jpg");
                        recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                        urls.add("https://img95.699pic.com/photo/50139/9656.jpg_wh300.jpg!/fh/300/quality/90");
                        recyclerView.getAdapter().notifyItemInserted(urls.size() - 1);
                    });
                }
            }, guest.getGuestId());
        }).start();
    }

    public void toBack(View view){
        finish();
    }
}
