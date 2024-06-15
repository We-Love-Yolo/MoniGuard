package com.weloveyolo.moniguard.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.adapter.BlackListAdapter;
import com.weloveyolo.moniguard.adapter.WhiteListAdapter;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView1;

    private RecyclerView recyclerView2;
    private WhiteListAdapter whiteListAdapter;
    private BlackListAdapter blackListAdapter;
    private List<String> whiteList;
    private List<String> blackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_album1);

        whiteList = new ArrayList<>();
        recyclerView1 = findViewById(R.id.white_album);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView1.setLayoutManager(layoutManager);
        whiteListAdapter = new WhiteListAdapter(this, whiteList);
        recyclerView1.setAdapter(whiteListAdapter);

        blackList = new ArrayList<>();
        recyclerView2 = findViewById(R.id.black_album);
        GridLayoutManager layoutManager1 = new GridLayoutManager(this, 3);
        recyclerView2.setLayoutManager(layoutManager1);
        blackListAdapter = new BlackListAdapter(this, blackList);
        recyclerView2.setAdapter(blackListAdapter);

        // 开始获取数据
        fetchScenes();
    }

    public void gotoDiscoverPage(View view){
        finish();
    }

    public void fetchScenes(){
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        whiteListAdapter.addFaceImage("https://www.hblqfrp.net/upload/2019/1a334fc25.jpg");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        blackListAdapter.addFaceImage("https://img0.baidu.com/it/u=1545054280,1016727184&fm=253&fmt=auto&app=138&f=JPEG?w=650&h=433");
        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        new Thread(() -> {
            moniGuardApi.getScenesApi().getScenes((scenes, success) -> {
                if (!success) {
                    Log.e("--","Failed to...");
                    return;
                }
                scenes.forEach(scene -> moniGuardApi.getScenesApi().getGuests(scene.getSceneId(), (guests, success1) -> {
                    guests.forEach(guest -> {
                        if(guest.isAllowed()){//白名单
                            moniGuardApi.getAnalysisApi().getFacesByGuestId((list1,success2)->{
                                if(!success2){
                                    return;
                                }
                                list1.forEach((item)->{
                                    moniGuardApi.getAnalysisApi().getFaceImage((url,success3)->{
                                        if(!success3){
                                            return;
                                        }
                                        runOnUiThread(() -> {
                                            whiteListAdapter.addFaceImage(url);
                                        });
                                    },item.getFaceId());
                                });
                            },guest.getGuestId());

                        }else{//黑名单
                            moniGuardApi.getAnalysisApi().getFacesByGuestId((list2,success2)->{
                                if(!success2){
                                    return;
                                }
                                list2.forEach((item)->{
                                    moniGuardApi.getAnalysisApi().getFaceImage((url,success3)->{
                                        if(!success3){
                                            return;
                                        }
                                        runOnUiThread(() -> {
                                            blackListAdapter.addFaceImage(url);
                                        });
                                    },item.getFaceId());
                                });
                            },guest.getGuestId());


                        }
                    });
                }));
            });
        }).start();
    }
    public void AddWhiteList(View view){//选黑进白

        findViewById(R.id.check_icon).setVisibility(View.VISIBLE);
        findViewById(R.id.check_box).setVisibility(View.VISIBLE);




    }


}
