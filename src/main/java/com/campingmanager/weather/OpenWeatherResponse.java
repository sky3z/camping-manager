package com.campingmanager.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Mappatura (parziale) della risposta dell'API "current weather" di OpenWeatherMap.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherResponse(List<Weather> weather, Main main, Wind wind, String name) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(String main, String description) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(double temp, int humidity) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(double speed) {
    }
}
