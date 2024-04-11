package com.weloveyolo.moniguard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activitys.AddDeviceActivity;
import com.weloveyolo.moniguard.activitys.MonitorActivity;
import com.weloveyolo.moniguard.utils.CustomToast;
import com.weloveyolo.moniguard.utils.HttpClient;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 进入添加设备
        ImageButton addSceneButton=view.findViewById(R.id.add_camera_button);
        addSceneButton.setOnClickListener(view1 -> {
            Intent intent=new Intent(getActivity(), AddDeviceActivity.class);
            startActivity(intent);
        });

        // 进入添加场景
        ImageButton addCameraButton = view.findViewById(R.id.add_scene);
        addCameraButton.setOnClickListener(v -> {
//            Intent intent=new Intent(getActivity(), );
//            startActivity(intent);

//            HttpUrl.Builder urlBuilder = HttpUrl.parse(String.valueOf(R.string.get获取设备视频流)).newBuilder();
//            urlBuilder.addQueryParameter("CameraID", "1");
//            Call call = HttpClient.get(urlBuilder.build().toString());
            Call call = HttpClient.get("");
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    Looper.prepare();
                    try{
                        JSONObject json = new JSONObject(response.body().string());
                        Toast.makeText(getActivity(), json.get("data").toString(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                }
            });
        });

        // 进入门禁
        ImageButton cameraButton1 = view.findViewById(R.id.camera1_button);
        cameraButton1.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), MonitorActivity.class);
            startActivity(intent);
        });

        // 测试
        CustomToast ct = new CustomToast(getActivity());
        ct.showSuccessToast("场景已创建", 1000);
    }
}
