package com.campingmanager.weather;

import com.campingmanager.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeatherServiceTest {

    private WeatherService service(String key) {
        return new WeatherService(key, "Trento,IT", RestClient.builder());
    }

    @Test
    void shouldThrowWhenApiKeyMissing() {
        assertThatThrownBy(() -> service("").getCurrentWeather())
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldMapResponseAndFlagBadWeather() {
        OpenWeatherResponse response = new OpenWeatherResponse(
                List.of(new OpenWeatherResponse.Weather("Rain", "pioggia leggera")),
                new OpenWeatherResponse.Main(18.5, 80),
                new OpenWeatherResponse.Wind(4.2),
                "Trento");

        WeatherDTO dto = service("any-key").toDto(response);

        assertThat(dto.getCity()).isEqualTo("Trento");
        assertThat(dto.getTemperature()).isEqualTo(18.5);
        assertThat(dto.getDescription()).isEqualTo("pioggia leggera");
        assertThat(dto.getHumidity()).isEqualTo(80);
        assertThat(dto.getWindSpeed()).isEqualTo(4.2);
        assertThat(dto.isBadWeather()).isTrue();
    }

    @Test
    void shouldNotFlagBadWeatherWhenClear() {
        WeatherService service = service("any-key");
        assertThat(service.isBadWeather("Clear")).isFalse();
        assertThat(service.isBadWeather("Clouds")).isFalse();
        assertThat(service.isBadWeather("Snow")).isTrue();
    }
}
