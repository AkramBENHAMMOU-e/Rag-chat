package com.enset.test;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class ImageTextExtractor {

    private final Tesseract tesseract;

    public ImageTextExtractor() {
        tesseract = new Tesseract();
        // Définir le chemin vers le dossier tessdata
        tesseract.setDatapath("src/main/resources/tessdata");
        // Définir la langue (par exemple, "eng" pour l'anglais, "fra" pour le français)
        tesseract.setLanguage("eng+fra");
    }

    public String extractTextFromImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            return tesseract.doOCR(imageFile); // Extraire le texte de l'image
        } catch (TesseractException e) {
            System.err.println("Error extracting text from image: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        ImageTextExtractor extractor = new ImageTextExtractor();
        String extractedText = extractor.extractTextFromImage("src/main/resources/convert-handwriting-to-text-1.jpg");
        System.out.println(extractedText);
    }
}