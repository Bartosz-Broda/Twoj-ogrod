package com.example.twjogrd.model;

public class CurrentWeatherDetails {
    private String temp;
    private String city_name;
    private String precip;
    private String wind_spd;


    public CurrentWeatherDetails(String temp, String city_name, String precip, String wind_spd) {
        this.temp = temp;
        this.city_name = city_name;
        this.precip = precip;
        this.wind_spd = wind_spd;
    }

    public String getTemp() {
        return temp;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getPrecip() {
        return precip;
    }

    public String getWind_spd() {
        return wind_spd;
    }

    @Override
    public String toString() {
        return "CurrentWeatherDetails{" +
                "temp='" + temp + '\'' +
                ", city_name='" + city_name + '\'' +
                ", precip='" + precip + '\'' +
                ", wind_spd='" + wind_spd + '\'' +
                '}';
    }
}
