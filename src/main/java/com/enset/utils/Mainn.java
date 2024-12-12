 package com.enset.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;


import com.enset.pdf.PDFDocumentProcessor;

class Mainn {
    

    public static void main(String[] args) {
        Mainn main = new Mainn();
        
        PDFDocumentProcessor pdfProcessor = new PDFDocumentProcessor();
        String text = pdfProcessor.extractTextFromPDF("C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\WXC.pdf");
        String[] chunks = text.split("\\.");
        for (String chunk : chunks) {
            System.out.println(chunk);
            System.out.println("================================");
            List<Double> vect = main.embedTextIntoDatabase(chunk);
            main.storeVectorInDatabase(vect);
           

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

    public void storeVectorInDatabase(List<Double> vector) {
        connexion conn = new connexion();
        Connection dbConnection = conn.connect();
        String insertSQL = "INSERT INTO vector_data (vector) VALUES (?::jsonb)";
    
        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertSQL)) {
            // Convert the List<Double> to a JSON array
            JSONArray jsonArray = new JSONArray(vector);
            preparedStatement.setString(1, jsonArray.toString());
            preparedStatement.executeUpdate();
            System.out.println("Vector stored in database successfully.");
        } catch (SQLException e) {
            System.err.println("Error storing vector in database: " + e.getMessage());
        }
    }

    
}