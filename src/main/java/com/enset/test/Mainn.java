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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     static EmbeddingGenerator embeddingGenerator = new EmbeddingGenerator();
     static Mainn main = new Mainn();
    public static void main(String[] args) throws Exception {




        PdfDocument pdfProcessor = new PdfDocument();
        String text = pdfProcessor.extractTextFromPDF("C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\ATIF_METKOUL_PFE.pdf");
        text=main.cleanText(text);
        List<String> chunks = splitIntoParagraphsLastV(text);
      /*for (String chunk : chunks) {
            System.out.println(chunk);
            System.out.println("---------------------------------------------");

            float[] vect = embeddingGenerator.generateEmbeddings(chunk);
            for (float value : vect) {
                System.out.print(value + " ");
            }
            System.out.println(vect);
            System.out.println("================================");
            main.storeVectorInDatabase(chunk,convertFloatArrayToList(vect));


        }*/

        String response = main.askQuestion("donner la definition de docker");
        System.out.println("result : "+response);


        /////////////////////////

    }


     public  void uploadUI(String chemin) throws IOException {
         PdfDocument pdfProcessor = new PdfDocument();
         List<String> chunks = pdfProcessor.extractPagesFromPDF(chemin);
         for (String chunk : chunks) {
             chunk=main.cleanText(chunk);
             System.out.println(chunk);
             System.out.println("---------------------------------------------");

             float[] vect = embeddingGenerator.generateEmbeddings(chunk);
             for (float value : vect) {
                 System.out.print(value + " ");
             }
             System.out.println(vect);
             System.out.println("================================");
             main.storeVectorInDatabase(chunk,convertFloatArrayToList(vect));
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

    public static String cleanText(String text){
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

     public static List<String> splitIntoParagraphsLastV(String text) {
         if (text == null || text.isEmpty()) {
             return Collections.emptyList();
         }

         List<String> paragraphs = new ArrayList<>();
         List<String> sentences = new ArrayList<>();

         // D'abord, on découpe le texte en phrases individuelles
         Pattern pattern = Pattern.compile("([^.!?]+[.!?])");
         Matcher matcher = pattern.matcher(text);

         while (matcher.find()) {
             String sentence = matcher.group(1).trim();
             if (!sentence.isEmpty()) {
                 sentences.add(sentence);
             }
         }

         // Ensuite, on regroupe les phrases par 4 pour former des paragraphes
         StringBuilder currentParagraph = new StringBuilder();
         int sentenceCount = 0;

         for (String sentence : sentences) {
             currentParagraph.append(sentence).append(" ");
             sentenceCount++;

             // Quand on atteint 4 phrases ou qu'on est à la dernière phrase
             if (sentenceCount == 4 || sentences.indexOf(sentence) == sentences.size() - 1) {
                 if (!currentParagraph.toString().trim().isEmpty()) {
                     paragraphs.add(currentParagraph.toString().trim());
                 }
                 currentParagraph = new StringBuilder();
                 sentenceCount = 0;
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

     public static void clearDatabase() {
         connexion conn = new connexion();
         Connection dbConnection = conn.connect();
         String deleteSQL = "DELETE FROM items";

         try (PreparedStatement preparedStatement = dbConnection.prepareStatement(deleteSQL)) {
             int rowsAffected = preparedStatement.executeUpdate();
             System.out.println("Database cleared successfully. Rows affected: " + rowsAffected);
             // Fermer la connexion après utilisation
             dbConnection.close();
         } catch (SQLException e) {
             System.err.println("Error clearing database: " + e.getMessage());
             e.printStackTrace();
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

