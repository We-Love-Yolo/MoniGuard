package com.weloveyolo.moniguard.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activitys.AlbumActivity;

import android.widget.ImageButton;

public class DiscoverFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 进入智能相册
        ImageButton button1 = view.findViewById(R.id.smart_photo_album);
        button1.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            startActivity(intent);
        });
    }
}

