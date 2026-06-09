package com.campingmanager.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Meteo corrente della localita del campeggio.
 */
@Data
@AllArgsConstructor
public class WeatherDTO {
    private String city;
    private double temperature;
    private String description;
    private int humidity;
    private double windSpeed;
    private boolean badWeather;
}
