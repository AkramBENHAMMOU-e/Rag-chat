package com.enset.test;

import java.io.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class LLMCohere {



        private static final String API_URL = "https://api.cohere.ai/v1/generate";
        private static final String API_KEY = "Z2vDImrUYu5IqVomEeCJJxTc3Ob38Ze6Wyq0YvWM"; // Replace with your Cohere API key

        public String askQuestion(String question) throws IOException {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(API_URL);
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Authorization", "Bearer " + API_KEY);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("prompt", question + " Veuillez répondre uniquement en français.");
                requestBody.addProperty("model", "command");
                requestBody.addProperty("max_tokens", 300);
                requestBody.addProperty("temperature", 0.7);

                StringEntity entity = new StringEntity(requestBody.toString(), "UTF-8");
                post.setEntity(entity);

                try (CloseableHttpResponse response = client.execute(post)) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    System.out.println("API Response: " + jsonResponse);

                    JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    if (responseObject.has("generations")) {
                        JsonArray generations = responseObject.getAsJsonArray("generations");
                        if (generations != null && generations.size() > 0) {
                            JsonObject firstGeneration = generations.get(0).getAsJsonObject();
                            if (firstGeneration.has("text")) {
                                return firstGeneration.get("text").getAsString();
                            }
                        }
                    }
                    return "Aucune réponse valide trouvée.";
                }
            }
        }
    }

