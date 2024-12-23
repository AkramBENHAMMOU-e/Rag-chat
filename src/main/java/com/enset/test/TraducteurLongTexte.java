package com.enset.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TraducteurLongTexte {
    private static final String BASE_URL = "https://translate.googleapis.com/translate_a/single";
    private static final int MAX_SEGMENT_LENGTH = 500; // Longueur maximale par segment

    /**
     * Traduit un long texte de l'anglais au français en le divisant en segments
     *
     * @param texteOriginal Le texte à traduire
     * @return Le texte traduit
     * @throws Exception en cas d'erreur de traduction
     */
    public static String traduireLongTexte(String texteOriginal) throws Exception {
        // Division du texte en segments
        List<String> segments = diviserTexte(texteOriginal);

        // Traduction de chaque segment
        List<String> segmentsTraduits = new ArrayList<>();
        for (String segment : segments) {
            segmentsTraduits.add(traduireSegment(segment));

            // Pause entre les segments pour éviter le blocage
            Thread.sleep(500);
        }

        // Reconstruction du texte traduit
        return String.join(" ", segmentsTraduits);
    }

    /**
     * Divise le texte en segments de longueur contrôlée
     *
     * @param texte Le texte à diviser
     * @return Liste des segments
     */
    private static List<String> diviserTexte(String texte) {
        List<String> segments = new ArrayList<>();
        String[] mots = texte.split("\\s+");

        StringBuilder segmentActuel = new StringBuilder();
        for (String mot : mots) {
            // Si l'ajout du mot dépasse la longueur maximale, on crée un nouveau segment
            if (segmentActuel.length() + mot.length() > MAX_SEGMENT_LENGTH) {
                segments.add(segmentActuel.toString().trim());
                segmentActuel = new StringBuilder();
            }

            segmentActuel.append(mot).append(" ");
        }

        // Ajouter le dernier segment
        if (segmentActuel.length() > 0) {
            segments.add(segmentActuel.toString().trim());
        }

        return segments;
    }

    /**
     * Traduit un segment de texte
     *
     * @param segment Le segment à traduire
     * @return Le segment traduit
     * @throws Exception en cas d'erreur
     */
    private static String traduireSegment(String segment) throws Exception {
        // Encodage du texte pour l'URL
        String encodedText = URLEncoder.encode(segment, StandardCharsets.UTF_8.toString());

        // Construction de l'URL de requête
        String urlString = String.format("%s?client=gtx&sl=en&tl=fr&dt=t&q=%s",
                BASE_URL, encodedText);

        URL url = new URL(urlString);
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Vérification du code de réponse
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Erreur HTTP : " + responseCode);
            }

            // Lecture de la réponse
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }

                // Extraction de la traduction
                return extraireTraduction(response.toString());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Extrait la traduction de la réponse
     *
     * @param response La réponse brute
     * @return Le texte traduit
     */
    private static String extraireTraduction(String response) {
        if (response == null || response.isEmpty()) {
            return "";
        }

        // Extraction de la première traduction
        int start = response.indexOf("\"") + 1;
        int end = response.indexOf("\"", start);

        return start > 0 && end > start ?
                response.substring(start, end) :
                response;
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        try {
            String texteOriginal = "Hello, this is a very long text that needs to be translated in multiple segments. " +
                    "We want to ensure that even lengthy documents can be translated efficiently. " +
                    "The translation process will break down the text into manageable chunks " +
                    "and then reassemble them into a complete translated document.";

            String traduction = traduireLongTexte(texteOriginal);

            System.out.println("Texte original : " + texteOriginal);
            System.out.println("Traduction : " + traduction);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}