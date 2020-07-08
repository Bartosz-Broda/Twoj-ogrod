package com.example.twjogrd.model;

public class AgroWeatherDetails {

    private String soilm_10_40cm;


    public String getSoilm_10_40cm() {
        return soilm_10_40cm;
    }


    public AgroWeatherDetails(String soilm_10_40cm) {
        this.soilm_10_40cm = soilm_10_40cm;
    }

    @Override
    public String toString() {
        return "AgroWeatherDetails{" +
                "soilMoisture='" + soilm_10_40cm + '\'' +
                '}';
    }
}
