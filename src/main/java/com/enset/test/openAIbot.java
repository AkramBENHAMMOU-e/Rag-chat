package com.enset.test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
public class openAIbot {


        public static void main(String[] args) {
            try {
                // Replace with your Google Cloud API key or the key for Gemini service
                String apiKey = "AIzaSyDp7K5K_b6AF9kq46qvVvp29NoiSy1GitM";

                // Create a HTTP Client
                HttpClient client = HttpClient.newHttpClient();

                // Create the JSON body for the API request (this depends on the API you're using)
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("input", "How does Gemini work?");
                jsonRequest.put("context", "general");

                // Build the request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://gemini.googleapis.com/v1/query")) // Replace with actual API URL
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString(), StandardCharsets.UTF_8))
                        .build();

                // Send the request and receive the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Process the response
                System.out.println("API Response: " + response.body());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

