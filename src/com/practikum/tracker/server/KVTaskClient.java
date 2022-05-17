package com.practikum.tracker.server;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String API_TOKEN;
    private final String urlServer;
    private final HttpClient httpClient;
    public KVTaskClient(String  url) throws IOException, InterruptedException {
        urlServer = url;
        httpClient = HttpClient.newHttpClient();

        URI uriRegistration = URI.create(urlServer + "/register");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uriRegistration).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    public KVTaskClient(String  url, String API_TOKEN) throws IOException, InterruptedException {
        urlServer = url;
        httpClient = HttpClient.newHttpClient();
        this.API_TOKEN = API_TOKEN;
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void put(String key, String json) throws IOException, InterruptedException{
        String url = urlServer + "/save/" + key + "?API_TOKEN=" + API_TOKEN;
        URI uri = URI.create(url);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws  IOException, InterruptedException {
        URI uri = URI.create(urlServer + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
