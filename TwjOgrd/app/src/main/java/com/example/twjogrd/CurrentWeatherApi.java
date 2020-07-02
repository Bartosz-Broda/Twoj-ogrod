package com.example.twjogrd;

import android.util.Log;

import com.example.twjogrd.model.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeatherApi {

    String BASE_URL = "https://api.weatherbit.io/v2.0/current/";

    @GET("?key=22b8f92434474c88af17c9bd729e0e7a")
    Call<CurrentWeather> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon);


}
