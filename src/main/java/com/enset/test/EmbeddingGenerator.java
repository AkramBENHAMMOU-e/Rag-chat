package com.enset.test;

import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;

import java.io.IOException;
import java.nio.file.Path;

public class EmbeddingGenerator {
    private static final String MODEL_URL = "djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2";
    
    public static void main(String[] args) {
        try {
            Criteria<String, float[]> criteria = Criteria.builder()
                    .setTypes(String.class, float[].class)
                    .optModelUrls(MODEL_URL)
                    .optEngine("PyTorch")
                    .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                    .optProgress(new ProgressBar())
                    .optDevice(Device.cpu())
                    .build();

            try (ZooModel<String, float[]> model = criteria.loadModel();
                 Predictor<String, float[]> predictor = model.newPredictor()) {
                
                String text = "This is a sample text for embedding generation.";
                float[] embedding = predictor.predict(text);
                
                System.out.println("Generated embedding dimensions: " + embedding.length);
                System.out.print("First few values: ");
                for (int i = 0; i < Math.min(10, embedding.length); i++) {
                    System.out.printf("%.4f ", embedding[i]);
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}