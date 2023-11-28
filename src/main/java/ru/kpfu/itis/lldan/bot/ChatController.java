package ru.kpfu.itis.lldan.bot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChatController {
    private Boolean started = false;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField inputField;

    @FXML
    void handleUserInput(ActionEvent event) {
        String userInput = inputField.getText().toLowerCase();
        displayResponseUser(userInput);
        inputField.clear();
        if (userInput.equals("/start")) {
            started = true;
            displayResponse("Бот запущен, вводите команды");
        } else if (userInput.equals("/end")) {
            displayResponse("Бот остановлен. Прощайте");
            started = false;
            System.exit(0);
        } else if (userInput.equals("/help")) {
            displayResponse("Вот список команд бота:");
            displayResponse("/start - запуск бота");
            displayResponse("/end - закрывает бота");
            displayResponse("/weather - после этого сообщения, через пробел нужно указать название города на английском");
            displayResponse("/exchange - необходимо указать две валюты, например `/exchange USD RUB`");
        } else if (userInput.contains("/weather")) {
            if (started) {
                String town = userInput.split(" ")[1];
                try {
                    Map<String, String> weather = getWeather(town);
                    displayResponse("Погода в городе " + weather.get("temperature"));
                    displayResponse("Осадки " + weather.get("precipitation"));
                    displayResponse("Влажность " + weather.get("humidity"));
                } catch (IOException e) {
                    displayResponse("Произошла ошибка");
                }
            }
        } else if (userInput.contains("/exchange")) {
            if (started) {
                String currencyFrom = userInput.split(" ")[1].toUpperCase();
                String currencyTo = userInput.split(" ")[2].toUpperCase();
                try {
                    String result = getExchange(currencyFrom, currencyTo);
                    displayResponse("1 " + currencyFrom + " = " + result + " " + currencyTo);
                } catch (IOException | InterruptedException e) {
                    displayResponse("Произошла ошибка");
                }

            }

        } else if (userInput.contains("/")) {
            displayResponse("Неизвестная команда. Напишите /help, чтобы узнать список команд");
        }

    }

    private void displayResponse(String response) {
        chatArea.appendText(response + "\n");
    }

    private void displayResponseUser(String response) {
        if (!response.isEmpty()) {
            chatArea.appendText("You: " + response + "\n");
        }

    }


    public static Map<String, String> getWeather(String town) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://api.openweathermap.org/data/2.5/weather?q=" + town + "&appid=143c9d8999112b2f489a1e3a44de6ade").openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String input;
            while ((input = reader.readLine()) != null) {
                content.append(input);
            }
        }
        connection.disconnect();
        JsonObject weatherJson = JsonParser.parseString(content.toString()).getAsJsonObject();
        JsonObject currentWeather = weatherJson.get("main").getAsJsonObject();
        JsonElement temperature = currentWeather.get("temp");
        JsonElement humidity = currentWeather.get("humidity");
        JsonElement precipitation = weatherJson.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description");
        Map<String, String> weather = new HashMap<>();
        weather.put("temperature", new DecimalFormat("#0.00").format(Double.parseDouble(temperature.getAsString()) - 273) + " °C");
        weather.put("humidity", humidity.getAsString() + "%");
        weather.put("precipitation", precipitation.getAsString());
        return weather;
    }

    public static String getExchange(String from, String to) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=" + from + "&to=" + to + "&amount=1"))
                .header("X-RapidAPI-Key", "86838455b6msh7a4ac724b0a50b0p130b99jsn9a5d40f84ccc")
                .header("X-RapidAPI-Host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject currencyJson = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonElement result = currencyJson.get("result");
        return result.getAsString();
    }
}