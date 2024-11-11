package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoapService {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    public List<String> send(List<String> requests) {
        return requests.stream()
                .map(it-> {
                    try {
                        return sendRequest(it);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<String> sendInThreads(List<String> requests) throws InterruptedException {
        var tasks = requests.stream()
                .map(it-> (Callable<String>) () -> sendRequest(it))
                .toList();
        var res = executorService.invokeAll(tasks);
        return res.stream().map(it-> {
            try {
                return it.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private String sendRequest(String body) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "text/xml")
                .uri(URI.create("http://localhost:8080/WSServlet"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
    }

}
