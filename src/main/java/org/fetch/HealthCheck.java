package org.fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HealthCheck {
    private static class Endpoint {
        private String name;
        private String url;
        private String method = "GET";
        private Map<String, String> headers;
        private String body;

        // Constructor for Jackson to use
        public Endpoint() {
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }


    public static void main(String[] args) throws Exception {
        String inputFile = "src/main/resources/sample_input_config.yaml";
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(inputFile)) {
            List<Map<String, Object>> endpointsList = yaml.load(inputStream);
            ObjectMapper mapper = new ObjectMapper();
            Endpoint[] endpoints = endpointsList.stream()
                    .map(map -> mapper.convertValue(map, Endpoint.class))
                    .toArray(Endpoint[]::new);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            AtomicInteger[] successfulRequestCounts = new AtomicInteger[endpoints.length];
            AtomicLong[] totalRequestCounts = new AtomicLong[endpoints.length];
            for (int i = 0; i < endpoints.length; i++) {
                successfulRequestCounts[i] = new AtomicInteger(0);
                totalRequestCounts[i] = new AtomicLong(0);
            }

            scheduler.scheduleAtFixedRate(() -> {
                for (int i = 0; i < endpoints.length; i++) {
                    Endpoint endpoint = endpoints[i];
                    totalRequestCounts[i].incrementAndGet();
                    try {
                        long startTime = System.currentTimeMillis();
                        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint.url).openConnection();
                        connection.setRequestMethod(endpoint.method);

                        if (endpoint.headers != null) {
                            for (Map.Entry<String, String> header : endpoint.headers.entrySet()) {
                                connection.setRequestProperty(header.getKey(), header.getValue());
                            }
                        }

                        if (endpoint.method.equalsIgnoreCase("POST") && endpoint.body != null) {
                            connection.setDoOutput(true);
                            connection.getOutputStream().write(endpoint.body.getBytes());
                        }

                        int responseCode = connection.getResponseCode();
                        long latency = System.currentTimeMillis() - startTime;

                        if (responseCode >= 200 && responseCode < 300 && latency < 500) {
                            successfulRequestCounts[i].incrementAndGet();
                        }
                    } catch (Exception e) {
                        // Endpoint is down, do nothing.
                    }
                }

                for (int i = 0; i < endpoints.length; i++) {
                    String domain = getDomainName(endpoints[i].url);
                    long up = successfulRequestCounts[i].get();
                    long total = totalRequestCounts[i].get();
                    int availability = (int) Math.round(100.0 * up / total);
                    System.out.println(domain + " has " + availability + "% availability percentage");
                }
            }, 0, 15, TimeUnit.SECONDS);
        }
    }

    private static String getDomainName(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
