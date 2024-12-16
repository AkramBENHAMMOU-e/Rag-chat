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
import java.util.List;

public class EmbeddingGenerator {
    private static final String MODEL_URL = "djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2";
    
    public static void main(String[] args) {

        EmbeddingGenerator generator = new EmbeddingGenerator();
        float[] embedding = generator.generateEmbeddings("Hello, world!");
        System.out.println(embedding.length);
        // Print the generated embedding
        for (float value : embedding) {
            System.out.print(value + " ");
        }
    }


    public float[] generateEmbeddings(String text) {
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
                
               
                float[] embedding = predictor.predict(text);
                return embedding ;
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new float[0];
        }
       
    }
}