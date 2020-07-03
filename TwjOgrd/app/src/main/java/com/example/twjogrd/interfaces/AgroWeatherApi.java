package com.example.twjogrd.interfaces;

import com.example.twjogrd.model.AgroWeather;
import com.example.twjogrd.model.CurrentWeather;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AgroWeatherApi {

    String BASE_URL = "https://api.weatherbit.io/v2.0/history/agweather/";

    @GET("?key=22b8f92434474c88af17c9bd729e0e7a")
    Call<AgroWeather> getAgroWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("start_date") String start, @Query("end_date") String end);
}
