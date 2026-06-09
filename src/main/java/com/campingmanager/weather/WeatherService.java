package com.campingmanager.weather;

import com.campingmanager.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Set;

/**
 * Recupera il meteo corrente da OpenWeatherMap tramite RestClient.
 */
@Service
public class WeatherService {

    private static final Set<String> BAD_CONDITIONS = Set.of("Rain", "Thunderstorm", "Snow", "Drizzle");

    private final RestClient restClient;
    private final String apiKey;
    private final String city;

    public WeatherService(@Value("${weather.api.key:}") String apiKey,
                          @Value("${weather.api.city:Trento,IT}") String city,
                          RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.city = city;
        this.restClient = restClientBuilder.baseUrl("https://api.openweathermap.org").build();
    }

    public WeatherDTO getCurrentWeather() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException("Meteo non configurato (manca WEATHER_API_KEY)");
        }
        OpenWeatherResponse response = restClient.get()
                .uri(uri -> uri.path("/data/2.5/weather")
                        .queryParam("q", city)
                        .queryParam("units", "metric")
                        .queryParam("lang", "it")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(OpenWeatherResponse.class);
        return toDto(response);
    }

    WeatherDTO toDto(OpenWeatherResponse r) {
        String main = (r.weather() != null && !r.weather().isEmpty()) ? r.weather().get(0).main() : null;
        String description = (r.weather() != null && !r.weather().isEmpty()) ? r.weather().get(0).description() : null;
        double temp = r.main() != null ? r.main().temp() : 0;
        int humidity = r.main() != null ? r.main().humidity() : 0;
        double wind = r.wind() != null ? r.wind().speed() : 0;
        return new WeatherDTO(r.name(), temp, description, humidity, wind, isBadWeather(main));
    }

    boolean isBadWeather(String main) {
        return main != null && BAD_CONDITIONS.contains(main);
    }
}
