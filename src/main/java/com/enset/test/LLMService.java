package com.enset.test;

import java.io.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class LLMService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private static final String API_KEY = "AIzaSyDwyxB4dCVZ-SPJAflfllaLos1DbirLprs"; // Remplacez par votre clé API valide

    /**
     * Méthode pour poser une question à l'API Gemini.
     *
     * @param question La question posée.
     * @return La réponse de l'API Gemini.
     * @throws IOException En cas d'erreur réseau.
     */
    public String askQuestion(String question) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Construire l'URL complète avec la clé API
            String fullApiUrl = API_URL + "?key=" + API_KEY;

            // Configurer la requête HTTP POST
            HttpPost post = new HttpPost(fullApiUrl);
            post.setHeader("Content-Type", "application/json");

            // Construire le JSON pour la requête
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", question + " Veuillez répondre uniquement en français.");

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject contentsObject = new JsonObject();
            contentsObject.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(contentsObject);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contentsArray);

            // Ajouter le JSON dans le corps de la requête
            StringEntity entity = new StringEntity(requestBody.toString(), "UTF-8");
            post.setEntity(entity);

            // Envoyer la requête et récupérer la réponse
            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("API Response: " + jsonResponse); // Debug: Affiche la réponse brute

                // Extraire la réponse textuelle de l'API
                JsonObject responseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                if (responseObject.has("candidates")) {
                    JsonArray candidates = responseObject.getAsJsonArray("candidates");

                    if (candidates != null && candidates.size() > 0) {
                        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();

                        // Vérifier le contenu textuel
                        if (firstCandidate.has("content")) {
                            JsonObject contentObject = firstCandidate.getAsJsonObject("content");
                            JsonArray parts = contentObject.getAsJsonArray("parts");

                            // Traiter les parties du texte
                            if (parts != null && parts.size() > 0) {
                                JsonObject firstPart = parts.get(0).getAsJsonObject();
                                if (firstPart.has("text")) {
                                    return firstPart.get("text").getAsString();
                                }
                            }
                        }
                    }
                }
                return "Aucune réponse valide trouvée.";
            }
        }
    }


}