package ru.ilka.chat.logic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.ilka.chat.exception.LogicException;

import java.io.IOException;

public class WeatherLogic {
    private static final String GISMETEO_URL_MINSK = "https://www.gismeteo.by/weather-minsk-4248/now/";
    private static final String CELSIUS_DEGREE = "°C";

    public String findWeatherInMinsk() throws LogicException {
        try {
            Document document = Jsoup.connect(GISMETEO_URL_MINSK).get();

            Element nowWeatherDiv = document.select("div.now__weather").first();
            String nowTemperature = nowWeatherDiv.child(0).text();

            Element nowDescriptionDiv = document.select("div.__frame_sm").first().child(0).child(0).child(0);
            String nowDescription = nowDescriptionDiv.attributes().asList().get(1).getValue();
            return nowTemperature + CELSIUS_DEGREE + ". " + nowDescription;
        } catch (IOException e) {
            throw new LogicException("Can not find Minsk weather forecast");
        }
    }
}
