package com.weloveyolo.moniguard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AddDeviceActivity;
import com.weloveyolo.moniguard.activity.MonitorActivity;
import com.weloveyolo.moniguard.api.Camera;


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
            intent.putExtra("sceneId", 1);
            startActivity(intent);
        });

        // 进入添加场景
        ImageButton addCameraButton = view.findViewById(R.id.add_scene);
 //       addCameraButton.setOnClickListener(v -> {
//            Intent intent=new Intent(getActivity(), );
//            startActivity(intent);
//        });

        // 进入门禁
        ImageButton cameraButton1 = view.findViewById(R.id.camera1_button);
        cameraButton1.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), MonitorActivity.class);
            startActivity(intent);
        });

        tryShow();
    }

    public void tryShow() {
        MainActivity mainActivity = ((MainActivity) getActivity());
        if(mainActivity == null) return;
        if(mainActivity.scenes != null) {
            // 场景
            if(mainActivity.cameras != null){
                // 相机
                requireActivity().runOnUiThread(() -> {

                    /*
                        测试用
                     */
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("相机"); // 设置对话框标题
                    String res = "";
                    for(Camera camera : mainActivity.cameras){
                        res = res + camera.toString() + "\n";
                    }
                    builder.setMessage(res); // 设置对话框内容
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }
        } else {

        }
    }
}
