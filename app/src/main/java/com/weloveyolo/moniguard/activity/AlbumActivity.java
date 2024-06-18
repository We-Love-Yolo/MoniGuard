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
import com.weloveyolo.moniguard.api.Guest;
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

        // 测试数据
        blackListAdapter.guestList.add(new Guest(1,1,"组长", ":2024-06-18T13:26:12.3281952", false,null));
        blackListAdapter.guestList.add(new Guest(1,1,"组员1", "2024-06-19T15:35:12.3281952", false,null));
        blackListAdapter.guestList.add(new Guest(1,1,"组员2", "2024-06-20T10:32:12.3281952", false,null));
        blackListAdapter.guestList.add(new Guest(1,1,"组员3", "2024-06-10T07:01:12.3281952", false,null));

        blackListAdapter.sceneNameList.add("春笛");
        blackListAdapter.sceneNameList.add("夏筝");
        blackListAdapter.sceneNameList.add("秋瑟");
        blackListAdapter.sceneNameList.add("冬筑");

        whiteListAdapter.addFaceImage("https://tse1-mm.cn.bing.net/th/id/OIP-C.f6PKBW6dgTNevbbqXSeDMAHaJU?rs=1&pid=ImgDetMain");
        whiteListAdapter.addFaceImage("https://tse1-mm.cn.bing.net/th/id/OIP-C.gFcNp1Kq9WC-GG1pQ42SVgAAAA?rs=1&pid=ImgDetMain");

        blackListAdapter.addFaceImage("https://img-blog.csdnimg.cn/f9ef14c5c658464d91bb05bcf13b1cac.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5LiA5ouz5Y2B5Liq6ZS16ZS15oCq,size_20,color_FFFFFF,t_70,g_se,x_16");
        blackListAdapter.addFaceImage("https://img.zcool.cn/community/014ab057b6f04f0000018c1bc1cd18.jpg@1280w_1l_2o_100sh.jpg");
        blackListAdapter.addFaceImage("https://n.sinaimg.cn/sinakd10113/354/w900h1054/20200706/d4a9-ivwfwmp9066114.jpg");
        blackListAdapter.addFaceImage("https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL3N6X21tYml6X3BuZy9rT1ROa2ljNWdWQkhvb2FGb3FzeW9pY2NKRElLTHhsNnBYalM2cTZGdHR2RmZBMW1neGNEYXpYVEZQaGVwajAwQkJkalpiREFkaWNZU3N1RmsyTzVDVXBUQS82NDA?x-oss-process=image/format,png");


        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        new Thread(() -> {
            moniGuardApi.getScenesApi().getScenes((scenes, success) -> {
                if (!success) {
                    Log.e("--","Failed to...");
                    return;
                }
                scenes.forEach(scene -> moniGuardApi.getScenesApi().getGuests(scene.getSceneId(), (guests, success1) -> {
                    blackListAdapter.sceneNameList.add(scene.getName());
                    guests.forEach(guest -> {
                        if(guest.isWhitelisted()){//白名单
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
                            blackListAdapter.guestList.add(guest);
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
        blackListAdapter.checkBoxes.forEach(item->{
            item.setVisibility(View.VISIBLE);
        });

    }


}
