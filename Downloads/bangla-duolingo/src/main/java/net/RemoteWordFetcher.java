package com.banglalearn.net;

import com.banglalearn.model.BanglaWord;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Downloads a JSON word pack over HTTP and parses it into BanglaWord objects. */
public class RemoteWordFetcher {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /** Blocking call: run it through BackgroundTasks, never on the JavaFX thread. */
    public List<BanglaWord> fetchWords(String url) throws IOException, InterruptedException {
        // 1. Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        // 2. Send it. Reading raw bytes avoids any charset problem with Bangla text.
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Server returned HTTP " + response.statusCode());
        }

        // 3. Parse JSON → List<BanglaWord> (same Jackson mapping ContentLoader uses)
        return mapper.readValue(response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, BanglaWord.class));
    }
}