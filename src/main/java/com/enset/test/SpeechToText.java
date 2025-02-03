package com.enset.test;




import org.vosk.Model;
import org.vosk.Recognizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SpeechToText {

    public String startVoiceRecording() {
        StringBuilder recognizedText = new StringBuilder();

        // Load the Vosk model
        try (Model model = new Model("C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\vosk-model-small-fr-pguyot-0.3")) {
            System.out.println("Model loaded successfully.");

            // Create a recognizer
            try (Recognizer recognizer = new Recognizer(model, 16000)) {
                System.out.println("Recognizer initialized.");

                // Set up audio capture
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Microphone not supported.");
                    return null;
                }

                try (TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info)) {
                    microphone.open(format);
                    microphone.start();

                    System.out.println("Recording... Say something in english...");

                    // Buffer for audio data
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];

                    // Record for 5 seconds (adjust as needed)
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 5000) { // Record for 5 seconds
                        int count = microphone.read(buffer, 0, buffer.length);
                        if (count > 0) {
                            out.write(buffer, 0, count);

                            // Process the audio chunk
                            if (recognizer.acceptWaveForm(buffer, count)) {
                                String result = recognizer.getResult();
                                System.out.println("Recognized: " + result);

                                // Parse the JSON response
                                ObjectMapper objectMapper = new ObjectMapper();
                                VoskResponse voskResponse = objectMapper.readValue(result, VoskResponse.class);

                                // Append the recognized text
                                recognizedText.append(voskResponse.getText()).append(" ");
                            }
                        }
                    }

                    // Stop recording
                    microphone.stop();
                    System.out.println("Recording stopped.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recognizedText.toString().trim(); // Return the recognized text
    }


}
