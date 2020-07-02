package com.example.twjogrd.model;

import java.util.ArrayList;

public class CurrentWeather {

    private Integer count;
    private ArrayList<CurrentWeatherDetails> data;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ArrayList<CurrentWeatherDetails> getData() {
        return data;
    }

    public void setData(ArrayList<CurrentWeatherDetails> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "count=" + count +
                ", details=" + data +
                '}';
    }

}
