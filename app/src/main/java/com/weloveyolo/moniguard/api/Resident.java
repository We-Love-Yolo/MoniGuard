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

    private String avatar;

    private String phone;

    private String email;
}
