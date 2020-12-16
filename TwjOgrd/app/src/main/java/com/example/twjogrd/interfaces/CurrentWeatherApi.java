package com.example.twjogrd.interfaces;

import android.util.Log;

import com.example.twjogrd.model.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeatherApi {

    String BASE_URL = "https://api.weatherbit.io/v2.0/current/";

    @GET("?key=c32cb0bb4fe645248c5d96ff235b9dcf")
    Call<CurrentWeather> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon);


}
