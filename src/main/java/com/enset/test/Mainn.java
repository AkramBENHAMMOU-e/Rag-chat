 package com.enset.test;

 import org.apache.poi.hwpf.extractor.WordExtractor;
 import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
 import org.apache.poi.xwpf.usermodel.XWPFDocument;
 import org.json.JSONArray;
 import org.apache.poi.hwpf.HWPFDocument;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Paths;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
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




        /////////////////////////

    }
     public String extractTextFromTxt(String filePath) throws IOException {
         return new String(Files.readAllBytes(Paths.get(filePath)));
     }

     public String extractTextFromDocx(String filePath) throws IOException {
         try (FileInputStream fis = new FileInputStream(filePath);
              XWPFDocument document = new XWPFDocument(fis);
              XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
             return extractor.getText();
         }
     }


     public boolean uploadUI(String chemin, int conversationId) throws IOException {
         File file = new File(chemin);
         String fileName = file.getName().toLowerCase();

         if (fileName.endsWith(".pdf")) {
             // Traitement des fichiers PDF
             PdfDocument pdfProcessor = new PdfDocument();
             List<String> chunks = pdfProcessor.extractPagesFromPDF(chemin);

             if (chunks.isEmpty()) {
                 System.err.println("No chunks extracted from the PDF.");
                 return false;
             }

             boolean allChunksStoredSuccessfully = true;

             for (String chunk : chunks) {
                 chunk = cleanText(chunk);
                 float[] vect = embeddingGenerator.generateEmbeddings(chunk);
                 boolean storedSuccessfully = storeVectorInDatabase(chunk, convertFloatArrayToList(vect), conversationId);
                 if (!storedSuccessfully) {
                     allChunksStoredSuccessfully = false;
                     System.err.println("Failed to store chunk in database: " + chunk);
                 }
             }

             return allChunksStoredSuccessfully;

         } else if (fileName.endsWith(".docx")) {
             // Traitement des fichiers DOCX

             String text = extractTextFromDocx(chemin);

             if (text == null || text.trim().isEmpty()) {
                 System.err.println("No text extracted from the DOCX file.");
                 return false;
             }

             text = cleanText(text);
             float[] vect = embeddingGenerator.generateEmbeddings(text);
             return storeVectorInDatabase(text, convertFloatArrayToList(vect), conversationId);

         }  else if (fileName.endsWith(".txt")) {
             // Traitement des fichiers TXT

             String text = extractTextFromTxt(chemin);

             if (text == null || text.trim().isEmpty()) {
                 System.err.println("No text extracted from the TXT file.");
                 return false;
             }

             text = cleanText(text);
             float[] vect = embeddingGenerator.generateEmbeddings(text);
             return storeVectorInDatabase(text, convertFloatArrayToList(vect), conversationId);

         } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
             // Traitement des images
             ImageTextExtractor imageExtractor = new ImageTextExtractor();
             String extractedText = imageExtractor.extractTextFromImage(chemin);

             if (extractedText == null || extractedText.trim().isEmpty()) {
                 System.err.println("No text extracted from the image.");
                 return false;
             }

             extractedText = cleanText(extractedText);
             float[] vect = embeddingGenerator.generateEmbeddings(extractedText);
             return storeVectorInDatabase(extractedText, convertFloatArrayToList(vect), conversationId);

         } else {
             System.err.println("Unsupported file format: " + fileName);
             return false;
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

     public boolean storeVectorInDatabase(String text, List<Float> vector, int conversationId) {
         connexion conn = new connexion();
         Connection dbConnection = conn.connect();
         String insertSQL = "INSERT INTO items (content, embedding, conversation_id) VALUES (?, ?::vector, ?)";

         try (PreparedStatement preparedStatement = dbConnection.prepareStatement(insertSQL)) {
             // Convertir la liste de floats en JSON
             JSONArray jsonArray = new JSONArray(vector);

             preparedStatement.setString(1, text);
             preparedStatement.setString(2, jsonArray.toString());
             preparedStatement.setInt(3, conversationId);

             int rowsAffected = preparedStatement.executeUpdate();
             System.out.println("Vector stored in database successfully. Rows affected: " + rowsAffected);
             return rowsAffected > 0; // Retourner true si l'insertion a réussi
         } catch (SQLException e) {
             System.err.println("Error storing vector in database: " + e.getMessage());
             return false; // Retourner false en cas d'erreur
         } finally {
             try {
                 if (dbConnection != null) {
                     dbConnection.close(); // Fermer la connexion
                 }
             } catch (SQLException e) {
                 System.err.println("Error closing database connection: " + e.getMessage());
             }
         }
     }



     public List<String> similaritySearch(String testText, int conversationId) {
         List<String> results = new ArrayList<>();
         System.out.println("keyword: " + testText);
         float[] vect = embeddingGenerator.generateEmbeddings(testText);
         List<Float> vector = convertFloatArrayToList(vect);
         connexion conn = new connexion();

         // SQL query to get the top 5 similar vectors for the current conversation
         String sqlQuery = "SELECT * FROM items WHERE conversation_id = ? ORDER BY embedding <-> ?::vector LIMIT 5";

         try (Connection dbConnection = conn.connect();
              PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlQuery)) {

             // Convert the List<Double> to a JSON array
             JSONArray jsonArray = new JSONArray(vector);
             preparedStatement.setInt(1, conversationId); // Filtrer par conversation
             preparedStatement.setString(2, jsonArray.toString());

             ResultSet resultSet = preparedStatement.executeQuery();

             // Print the results
             while (resultSet.next()) {
                 String textOfvect = resultSet.getString("content");
                 results.add(textOfvect);
                 System.out.println("Text: " + textOfvect);
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return results;
     }

     public String askQuestion(String question, int conversationId) {
         List<String> res = similaritySearch(question, conversationId); // Ajouter l'identifiant de conversation
         String res2 = String.join("\n", res);
         // Combine PDF text with the question
         String fullQuestion = "context" + res2 + "\n" + "Task : \n" + question;

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

