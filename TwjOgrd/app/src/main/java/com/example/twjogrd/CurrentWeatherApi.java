package com.example.twjogrd;

import com.example.twjogrd.model.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrentWeatherApi {

    String BASE_URL = "https://api.weatherbit.io/v2.0/current/";

    @GET("?city=Raleigh,NC&key=22b8f92434474c88af17c9bd729e0e7a")
    Call<CurrentWeather> getCurrentWeather();

}
