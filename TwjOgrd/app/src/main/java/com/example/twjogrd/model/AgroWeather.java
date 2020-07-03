package com.example.twjogrd.model;

import java.util.ArrayList;

public class AgroWeather {

    private ArrayList<AgroWeatherDetails> data;


    public ArrayList<AgroWeatherDetails> getData() {
        return data;
    }

    public void setData(ArrayList<AgroWeatherDetails> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AgroWeather{" +
                "data=" + data +
                '}';
    }
}
