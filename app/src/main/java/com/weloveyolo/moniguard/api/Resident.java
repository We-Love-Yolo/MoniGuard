package com.weloveyolo.moniguard.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Resident {
    private int residentId;
    private String nameIdentifier;
    private String nickname;
    private byte[] avatar;
    private String phone;
    private String email;

    public int getResidentId() {
        return residentId;
    }

    public String getNickname() {
        return nickname;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
