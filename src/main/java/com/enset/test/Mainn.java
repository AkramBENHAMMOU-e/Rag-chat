 package com.enset.test;

import java.io.IOException;
import java.io.StringReader;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import javax.net.ssl.SSLContext;

 class Mainn {
    EmbeddingGenerator embeddingGenerator = new EmbeddingGenerator();
    public static void main(String[] args) throws IOException {
        Mainn main = new Mainn();

        PdfDocument pdfProcessor = new PdfDocument();
        String text = pdfProcessor.extractTextFromPDF("C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\ATIF_METKOUL_PFE.pdf");
        text=main.cleanText(text);
        List<String> chunks = splitIntoParagraphs(text);
//        for (String chunk : chunks) {
//            System.out.println(chunk);
//
//            float[] vect = embeddingGenerator.generateEmbeddings(chunk);
//            for (float value : vect) {
//                System.out.print(value + " ");
//            }
//            System.out.println(vect);
//            System.out.println("================================");
//            main.storeVectorInDatabase(chunk,convertFloatArrayToList(vect));
//
//
//        }

        String response = main.askQuestion("c'est quoi casa shop ?");
        String response1 = main.translate(response, "fr");
        System.out.println(response1);

        /////////////////////////

    }

    private static final String API_URL = "https://libretranslate.de/translate"; // Public LibreTranslate instance

    private static CloseableHttpClient createHttpClientWithNoSSL() throws Exception {
        // Trust all certificates
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        // Create an SSLContext with the TrustStrategy
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        // Create an SSLConnectionSocketFactory that skips hostname verification
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE);

        return HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
    }

     public String translate(String text, String targetLanguage) throws IOException {
         try (CloseableHttpClient client = HttpClients.createDefault()) {
             HttpPost post = new HttpPost(API_URL);
             post.setHeader("Content-Type", "application/json");

             // Préparer le corps JSON
             JsonObject json = new JsonObject();
             json.addProperty("q", text);
             json.addProperty("source", "en");
             json.addProperty("target", targetLanguage);
             json.addProperty("format", "text");

             post.setEntity(new StringEntity(json.toString()));

             // Exécuter la requête
             try (CloseableHttpResponse response = client.execute(post)) {
                 String jsonResponse = EntityUtils.toString(response.getEntity());

                 // Log de la réponse brute
                 System.out.println("Raw Response: " + jsonResponse);

                 // Vérifiez que la réponse est au format JSON
                 if (!jsonResponse.trim().startsWith("{")) {
                     throw new IOException("Unexpected response format: " + jsonResponse);
                 }

                 // Utiliser JsonReader pour tolérer les JSON mal formés
                 JsonReader jsonReader = new JsonReader(new StringReader(jsonResponse));
                 jsonReader.setLenient(true);

                 // Analyse du JSON
                 JsonObject jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();
                 return jsonObject.get("translatedText").getAsString();
             }
         }
     }
    public List<Double> embedTextIntoDatabase(String text) {
        // Convert text to vector
        List<Double> vector = convertTextToVector(text);

        // Example: Print the vector
        System.out.println("Text converted to vector: " + vector);

        // Return the vector
        return vector;
    }

    // Hypothetical method for text-to-vector conversion
    private List<Double> convertTextToVector(String text) {
        // Placeholder implementation
        List<Double> vector = new ArrayList<>();
        // Example: Convert each character to its ASCII value and normalize
        for (char c : text.toCharArray()) {
            vector.add((double) c / 128); // Normalize ASCII values
        }
        return vector;
    }

    public String cleanText(String text){
        return text.replaceAll("\\s+", " ").trim();
    }

//    public void storeVectorInDatabase(List<Double> vector) {
//        connexion conn = new connexion();
//        Connection dbConnection = conn.connect();
//        String insertSQL = "INSERT INTO vector_data (vector) VALUES (?::jsonb)";
//
//        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertSQL)) {
//            // Convert the List<Double> to a JSON array
//            JSONArray jsonArray = new JSONArray(vector);
//            preparedStatement.setString(1, jsonArray.toString());
//            preparedStatement.executeUpdate();
//            System.out.println("Vector stored in database successfully.");
//        } catch (SQLException e) {
//            System.err.println("Error storing vector in database: " + e.getMessage());
//        }
//    }
private static List<String> splitIntoParagraphs(String text) {
    List<String> paragraphs = new ArrayList<>();
    String[] lines = text.split("\\.");  // Splits on empty line between paragraphs

    for (String line : lines) {
        line = line.trim(); // Remove leading/trailing whitespace
        if (!line.isEmpty()) {
            paragraphs.add(line);
        }
    }
    return paragraphs;
}

    public static List<Float> convertFloatArrayToList(float[] floatArray) {
        List<Float> floatList = new ArrayList<>();
        for (float f : floatArray) {
            floatList.add(f); // Autoboxing float to Float
        }
        return floatList;
    }

    public void storeVectorInDatabase(String text, List<Float> vector) {
        connexion conn = new connexion();
        Connection dbConnection = conn.connect();
        String insertSQL = "INSERT INTO items (content, embedding) VALUES (?, ?::vector)";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertSQL)) {
            // Convert the List<Double> to a JSON array
            JSONArray jsonArray = new JSONArray(vector);

            preparedStatement.setString(2, jsonArray.toString());
            preparedStatement.setString(1, text);
            preparedStatement.executeUpdate();
            System.out.println("Vector stored in database successfully.");
        } catch (SQLException e) {
            System.err.println("Error storing vector in database: " + e.getMessage());
        }
    }

    public List<String> similaritySearch(String testText){
        List<String> results = new ArrayList<>();
        System.out.println("keyword: " + testText);
        float[] vect = embeddingGenerator.generateEmbeddings(testText);
        List<Float> vector = convertFloatArrayToList(vect);
        connexion conn = new connexion();
// SQL query to get the top 5 similar vectors
        String sqlQuery = "SELECT * FROM items ORDER BY embedding <-> ?::vector LIMIT 5";

        try (Connection dbConnection = conn.connect();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlQuery)) {

// Convert the List<Double> to a JSON array
            JSONArray jsonArray = new JSONArray(vector);
            preparedStatement.setString(1, jsonArray.toString());

//preparedStatement.setString(1, vectorString); // Set the vector string as a parameter
            ResultSet resultSet = preparedStatement.executeQuery();

// Print the results
            while (resultSet.next()) {
// Assuming your table has columns 'id' and 'embedding', adjust as necessary
                int id = resultSet.getInt("id");
                String textOfvect = resultSet.getString("content");
                String embedding = resultSet.getString("embedding");
                results.add(textOfvect);
                System.out.println("Text: " + textOfvect);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public String askQuestion(String question) {
        List<String> res = similaritySearch(question);
        String res2 = String.join("\n", res);
        // Combine PDF text with the question
        String fullQuestion =  "context"+res2+ "\n"+"Task : \n" + question;

        // Use LLM service to get an answer
        LLMService llmService = new LLMService();
        try {
            return llmService.askQuestion(fullQuestion);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing the question.";
        }
    }

}

