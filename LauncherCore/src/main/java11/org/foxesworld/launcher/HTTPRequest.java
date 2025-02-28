package org.foxesworld.launcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.foxesworld.utils.helper.IOHelper;
import org.foxesworld.utils.helper.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class HTTPRequest {
    private static final int TIMEOUT = 10000;

    private HTTPRequest() {
    }

    public static JsonElement jsonRequest(JsonElement request, URL url) throws IOException {
        return jsonRequest(request, "POST", url);
    }

    public static JsonElement jsonRequest(JsonElement request, String method, URL url) throws IOException {
        HttpClient client = HttpClient.newBuilder()
                .build();
        HttpRequest.BodyPublisher publisher;
        if (request != null) {
            publisher = HttpRequest.BodyPublishers.ofString(request.toString());
        } else {
            publisher = HttpRequest.BodyPublishers.noBody();
        }
        try {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .method(method, publisher)
                    .uri(url.toURI())
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofMillis(TIMEOUT))
                    .build();
            HttpResponse<InputStream> response = client.send(request1, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();
            try {
                return JsonParser.parseReader(IOHelper.newReader(response.body()));
            } catch (Exception e) {
                if (200 > statusCode || statusCode > 300) {
                    LogHelper.error("JsonRequest failed. Server response code %d", statusCode);
                    throw new IOException(e);
                }
                return null;
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e);
        }
    }
}
