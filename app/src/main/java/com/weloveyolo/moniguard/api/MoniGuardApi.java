package com.weloveyolo.moniguard.api;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MoniGuardApi implements IMoniGuardApi {
    private static final String BASE_URL = "https://mgapi.bitterorange.cn";

    @Setter
    private String accessToken;

    private final IResidentsApi residentsApi;

    public MoniGuardApi() {
        this.residentsApi = new ResidentsApi(this);
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }
}
