package com.weloveyolo.moniguard.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Face {

    private int faceId;
    private int guestId;
    private String name;
    private String capturedAt;

    public Face(int guestId, String name, Date cur) {
        this.guestId = guestId;
        this.name = name;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        String timeString = dateFormat.format(cur);
        capturedAt = timeString.replace(" ", "T").concat("Z");
    }
}
