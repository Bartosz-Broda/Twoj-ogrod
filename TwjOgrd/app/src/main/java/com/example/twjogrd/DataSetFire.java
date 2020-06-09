package com.example.twjogrd;

public class DataSetFire {
    String nazwa;
    String nazwa_lac;
    Integer idrosliny;


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

    public DataSetFire() {
    }

    public DataSetFire(String nazwa, String nazwa_lac, Integer idrosliny) {
        this.nazwa = nazwa;
        this.nazwa_lac = nazwa_lac;
        this.idrosliny = idrosliny;
    }
}
