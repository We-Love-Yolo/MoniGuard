package com.weloveyolo.moniguard.api;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

    private int settingsId;

    private int residentId;

    private boolean receiveWarning;

    private boolean receiveNewGuest;

    private boolean healthNotice;


    public Settings(int residentId, boolean receiveWarning, boolean receiveNewGuest, boolean healthNotice) {
        this.residentId = residentId;
        this.receiveWarning = receiveWarning;
        this.receiveNewGuest = receiveNewGuest;
        this.healthNotice = healthNotice;
    }

}
