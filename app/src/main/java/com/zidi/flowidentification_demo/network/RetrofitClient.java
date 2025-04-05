package com.zidi.flowidentification_demo.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private final UploadApi uploadApi;
    private final AuthApi authApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uploadApi = retrofit.create(UploadApi.class);
        authApi = retrofit.create(AuthApi.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public UploadApi getUploadApi() {
        return uploadApi;
    }

    public AuthApi getAuthApi() {
        return authApi;
    }
}
