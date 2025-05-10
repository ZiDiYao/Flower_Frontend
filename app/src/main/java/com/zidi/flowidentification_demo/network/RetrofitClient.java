package com.zidi.flowidentification_demo.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {
    private static RetrofitClient instance;

    private static final String BASE_URL = "http://10.0.2.2:8080";

    private static final String Python_URL = "http://10.0.2.2:5000";
    private final UploadApi uploadApi;
    private final AuthApi authApi;
    private final DescriptionApi descriptionApi;
    private final ImageMLAPI imageMLAPI;

    private final ResultApi resultApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        uploadApi = retrofit.create(UploadApi.class);
        authApi = retrofit.create(AuthApi.class);
        descriptionApi = retrofit.create(DescriptionApi.class);

        Retrofit pythonRetrofit = new Retrofit.Builder()
                .baseUrl(Python_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        imageMLAPI = pythonRetrofit.create(ImageMLAPI.class);

        resultApi = retrofit.create(ResultApi.class);

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

    public DescriptionApi getDescriptionApi() {
        return descriptionApi;
    }


    public ImageMLAPI getImageMLAPI() {
        return imageMLAPI;
    }

    public ResultApi getResultApi() {
        return resultApi;

    }
}
