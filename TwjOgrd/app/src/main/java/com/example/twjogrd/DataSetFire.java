package com.example.twjogrd;

public class DataSetFire {
    String nazwa;
    String nazwa_lac;
    Integer idrosliny;
    Integer temp_min;
    Integer wilg_min;

    String hint_temperature;
    String hint_soil;

    public String getHint_temperature() {
        return hint_temperature;
    }

    public void setHint_temperature(String hint_temperature) {
        this.hint_temperature = hint_temperature;
    }

    public String getHint_soil() {
        return hint_soil;
    }

    public void setHint_soil(String hint_soil) {
        this.hint_soil = hint_soil;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa_lac() {
        return nazwa_lac;
    }

    public void setNazwa_lac(String nazwa_lac) {
        this.nazwa_lac = nazwa_lac;
    }

    public String getIdrosliny() {
        return idrosliny.toString();
    }

    public void setIdrosliny(Integer idrosliny) {
        this.idrosliny = idrosliny;
    }

    public String getTemp_min() {
        return temp_min.toString();
    }

    public void setTemp_min(Integer temp_min) {
        this.temp_min = temp_min;
    }

    public String getWilg_min() {
        return wilg_min.toString();
    }

    public void setWilg_min(Integer wilg_min) {
        this.wilg_min = wilg_min;
    }

    public DataSetFire() {
    }

    public DataSetFire(String nazwa, String nazwa_lac, Integer idrosliny, Integer temp_min, Integer wilg_min) {
        this.nazwa = nazwa;
        this.nazwa_lac = nazwa_lac;
        this.idrosliny = idrosliny;
        this.temp_min = temp_min;
        this.wilg_min = wilg_min;
    }
}
