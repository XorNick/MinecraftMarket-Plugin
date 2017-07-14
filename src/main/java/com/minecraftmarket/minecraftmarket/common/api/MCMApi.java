package com.minecraftmarket.minecraftmarket.common.api;

import com.minecraftmarket.minecraftmarket.common.api.types.GSONApi;
import com.minecraftmarket.minecraftmarket.common.api.types.JSONApi;

public class MCMApi {
    private final MCMarketApi marketApi;

    public MCMApi(String apiKey, boolean debug, ApiType apiType) {
        if (apiType == ApiType.JSON) {
            marketApi = new JSONApi(apiKey, debug);
        } else {
            marketApi = new GSONApi(apiKey, debug);
        }
    }

    public MCMarketApi getMarketApi() {
        return marketApi;
    }

    public enum ApiType {
        JSON,
        GSON
    }
}