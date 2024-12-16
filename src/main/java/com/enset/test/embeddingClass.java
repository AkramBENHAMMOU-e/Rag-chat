package com.enset.test;
import ai.djl.Application;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.djl.modality.nlp.embedding.WordEmbedding;
import ai.djl.modality.nlp.translator.TextEmbeddingTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
public class embeddingClass {



    public static void main(String[] args) {
        try {
            // Load a pre-trained word embedding model (e.g., FastText, GloVe, or Word2Vec)
            Model model = Model.newInstance("word2vec");
            model.load(Utils.getResourcePath("models/word2vec.zip")); // Replace with your model path

            // Create a translator to handle text input and embedding output
            Translator<String, float[]> translator = new TextEmbeddingTranslator();

            // Create a predictor
            try (Predictor<String, float[]> predictor = model.newPredictor(translator)) {
                // Input text
                String input = "hello world";

                // Get embeddings
                float[] embeddings = predictor.predict(input);

                // Display embeddings
                System.out.println("Embeddings for \"" + input + "\":");
                System.out.println(Arrays.toString(embeddings));
            }

        } catch (IOException | ModelException | TranslateException e) {
            e.printStackTrace();
        }
    }
}


