package com.enset.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PdfDocument {

    public String extractTextFromPDF(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            System.err.println("Error processing PDF: " + e.getMessage());
            return "";
        }
    }

    public static List<String> extractPagesFromPDF(String pdfFilePath) throws IOException {
        List<String> pages = new ArrayList<>();
        File file = new File(pdfFilePath);

        // Charger le fichier PDF
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // Parcourir les pages du document
            int totalPages = document.getNumberOfPages();
            for (int i = 1; i <= totalPages; i++) {
                pdfStripper.setStartPage(i);
                pdfStripper.setEndPage(i);

                // Extraire le texte de la page courante
                String pageText = pdfStripper.getText(document);
                pages.add(pageText.trim());
            }
        }

        return pages;
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
}
