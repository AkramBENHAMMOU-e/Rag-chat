package com.enset.test;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {


        /**
         * Extrait les pages d'un fichier PDF et retourne une liste de chaînes.
         *
         * @param pdfFilePath Chemin du fichier PDF.
         * @return Une liste contenant le texte de chaque page.
         * @throws IOException Si une erreur d'entrée/sortie se produit.
         */
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

        // Exemple d'utilisation
        public static void main(String[] args) {
            String pdfFilePath = "C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\ATIF_METKOUL_PFE.pdf";

            try {
                List<String> pages = extractPagesFromPDF(pdfFilePath);
                for (int i = 0; i < pages.size(); i++) {
                    System.out.println("Page " + (i + 1) + ":\n" + Mainn.cleanText(pages.get(i)));
                    System.out.println("------------------------------------");
                    System.out.println(pages.get(i).length());

                    System.out.println("------------------------------------");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la lecture du fichier PDF.");
            }
        }
    }


