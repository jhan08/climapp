package com.example.climapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Sensors {

    public LocalDateTime date;
    public Long temperature;
    public Long humidity;
    public Long luminosity;



    public Sensors() {
    }

    public Sensors(LocalDateTime date, Long temperature, Long humidity, Long luminosity) {
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminosity = luminosity;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("temperature", temperature);
        result.put("humidity", humidity);
        result.put("luminosity", luminosity);

        return result;
    }
}