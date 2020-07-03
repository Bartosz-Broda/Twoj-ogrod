package com.example.twjogrd.model;

public class AgroWeatherDetails {

    private String soilm_0_10cm;


    public String getSoilm_0_10cm() {
        return soilm_0_10cm;
    }


    public AgroWeatherDetails(String soilm_0_10cm) {
        this.soilm_0_10cm = soilm_0_10cm;
    }

    @Override
    public String toString() {
        return "AgroWeatherDetails{" +
                "soilMoisture='" + soilm_0_10cm + '\'' +
                '}';
    }
}
