package com.enset;

import com.enset.chatbot.LLMService;
import com.enset.chatbot.SimpleChatbot;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        SimpleChatbot chatbot = new SimpleChatbot();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Educational Chatbot");
        System.out.print("Enter the path to your PDF file: ");
        String pdfPath = scanner.nextLine();

        // Load the PDF document
        chatbot.loadPDFDocument(pdfPath);
        System.out.println("PDF loaded successfully!");

        // Chat loop
        while (true) {
            System.out.print("\nAsk a question (or type 'exit' to quit): ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                break;
            }

            String answer = chatbot.askQuestion(question);
            System.out.println("\nAnswer: " + answer);
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
    

    /*
        public static void main(String[] args) throws IOException  {
        LLMService chatbot = new LLMService();
        System.out.println(chatbot.askQuestion(" who is Professeur mohammed lyousfi"));

    }
     */


}
