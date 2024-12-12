package com.enset.chatbot;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
/*
public class LLMService {
    private static final String API_URL = "YOUR_LLM_API_URL"; // Replace with your LLM API URL
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your API key

    public String askQuestion(String question) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + API_KEY);

            JsonObject json = new JsonObject();
            json.addProperty("question", question);
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return jsonObject.get("answer").getAsString(); // Adjust based on actual API response structure
            }
        }
    }
}
*/

public class LLMService {
    private static final String API_URL = "https://api.cohere.ai/generate";
    private static final String API_KEY = "z52lfZ5HXQt2Ji4XckATeWM0jOCL3uUwKweELP6H"; // Replace with your Cohere API key

    public String askQuestion(String question) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Authorization", "Bearer " + API_KEY);
            post.setHeader("Content-Type", "application/json");
    
            JsonObject json = new JsonObject();
            question = question + " answer in French please"; // Ensure the response is in French
            json.addProperty("prompt", question);
            json.addProperty("max_tokens", 200); // Adjust as needed
            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);
    
            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("API Response: " + jsonResponse); // Print the full response
    
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return jsonObject.get("text").getAsString(); // Adjust based on actual API response structure
            }
        }
    }
}