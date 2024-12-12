package com.enset.chatbot;

import com.enset.pdf.PDFDocumentProcessor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class SimpleChatbot {
    private final Directory memoryIndex;
    private final StandardAnalyzer analyzer;
    private final PDFDocumentProcessor pdfProcessor;
    private String pdfText;

    public SimpleChatbot() {
        this.memoryIndex = new ByteBuffersDirectory();
        this.analyzer = new StandardAnalyzer();
        this.pdfProcessor = new PDFDocumentProcessor();
    }

    public void loadPDFDocument(String pdfPath) {
        try {
            pdfText = pdfProcessor.extractTextFromPDF(pdfPath);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            
            try (IndexWriter writer = new IndexWriter(memoryIndex, config)) {
                Document doc = new Document();
                doc.add(new TextField("content", pdfText, Field.Store.YES));
                writer.addDocument(doc);
                System.out.println(pdfText);
            }
        } catch (IOException e) {
            System.err.println("Error indexing document: " + e.getMessage());
        }
    }

    public String loadPDFDocument() {
        return pdfText;
    }

    public String askQuestion(String question) {
        // Extract text from PDF document
        String pdfText = loadPDFDocument();

        // Combine PDF text with the question
        String fullQuestion = pdfText + "\n" + question;

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
