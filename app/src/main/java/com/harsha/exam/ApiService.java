package com.harsha.exam;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("b/6HBE/")
    Call<ApiResponse> getApiData();
}

