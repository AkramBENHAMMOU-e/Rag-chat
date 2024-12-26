package com.enset.test;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class SimpleChatInterface extends Application {

    private Label pdfPathLabel;
    private Button sendButton;


    @Override
    public void start(Stage primaryStage) {
        // Création des éléments de l'interface
        VBox chatBox = new VBox(10); // Conteneur pour les messages
        chatBox.setPadding(new Insets(10));
        chatBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY))); // Fond bleu clair

        ScrollPane scrollPane = new ScrollPane(chatBox); // Ajout d'un ScrollPane pour faire défiler les messages
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message...");
        messageArea.setPrefRowCount(3); // Taille de la zone de texte pour écrire les messages
        messageArea.setFont(Font.font("Arial", 14));
        messageArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY))); // Fond blanc
        messageArea.setPadding(new Insets(10)); // Espacement interne pour plus d'esthétique
        HBox.setHgrow(messageArea, Priority.ALWAYS);

        // Gestionnaire d'événements pour la touche "Entrée"
        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendPrompt(messageArea, chatBox, sendButton);
            }
        });

        // Bouton "Send" avec un symbole Unicode
        sendButton = new Button("\u27A4"); // Symbole de flèche vers la droite
        sendButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sendButton.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, new CornerRadii(5), Insets.EMPTY)));
        sendButton.setTextFill(Color.WHITE);
        sendButton.setMinSize(50, 50); // Taille fixe pour le bouton (largeur et hauteur)
        sendButton.setMaxSize(50, 50); // Taille fixe pour le bouton (largeur et hauteur)

        sendButton.setOnAction(event -> sendPrompt(messageArea, chatBox, sendButton));




        // Bouton "Load PDF" avec un symbole Unicode
        Button loadPdfButton = new Button("\uD83D\uDCCE"); // Symbole de trombone (représentant un fichier)
        loadPdfButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        loadPdfButton.setTextFill(Color.BLACK); // Couleur du texte en noir
        loadPdfButton.setBackground(Background.EMPTY); // Pas d'arrière-plan
        loadPdfButton.setMinSize(50, 50); // Taille fixe pour le bouton (largeur et hauteur)
        loadPdfButton.setMaxSize(50, 50); // Taille fixe pour le bouton (largeur et hauteur)

        loadPdfButton.setOnAction(event -> loadAndDisplayPDFPath(chatBox));

        // Label pour afficher le chemin du fichier PDF
        pdfPathLabel = new Label();
        pdfPathLabel.setFont(Font.font("Arial", 14));
        pdfPathLabel.setPadding(new Insets(10));
        pdfPathLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(10), Insets.EMPTY))); // Fond gris clair
        pdfPathLabel.setTextFill(Color.BLACK);
        pdfPathLabel.setWrapText(true);

        // Mise en page avec HBox pour regrouper les boutons
        HBox buttonBox = new HBox(10, sendButton, loadPdfButton); // Boutons dans la même boîte
        buttonBox.setAlignment(Pos.CENTER_RIGHT); // Aligner à droite

        // Mise en page avec HBox pour aligner la zone de texte et les boutons
        HBox inputBox = new HBox(10, messageArea, buttonBox);
        inputBox.setAlignment(Pos.CENTER_RIGHT); // Aligner à droite
        HBox.setHgrow(buttonBox, Priority.NEVER);

        // Mise en page avec VBox pour centrer l'interface
        VBox vbox = new VBox(20, scrollPane, inputBox, pdfPathLabel);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER); // Aligner au centre
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY))); // Fond bleu clair
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(inputBox, Priority.NEVER);
        VBox.setVgrow(pdfPathLabel, Priority.NEVER);

        // Création de la scène
        Scene scene = new Scene(vbox);

        // Écouteur de changement de taille pour rendre l'interface responsive
        scene.widthProperty().addListener((observable, oldValue, newValue) -> adjustLayout(newValue.doubleValue(), scene.getHeight()));
        scene.heightProperty().addListener((observable, oldValue, newValue) -> adjustLayout(scene.getWidth(), newValue.doubleValue()));

        // Configuration et affichage de la fenêtre
        primaryStage.setTitle("Simple Chat Interface");
        primaryStage.setScene(scene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    // Fonction pour ajuster la mise en page en fonction des dimensions de la fenêtre
    private void adjustLayout(double width, double height) {
        // Ajustements possibles en fonction des dimensions
        if (width < 400) {
            pdfPathLabel.setFont(Font.font("Arial", 10));
        } else {
            pdfPathLabel.setFont(Font.font("Arial", 14));
        }
    }

    // Fonction pour envoyer un message
//    private void sendPrompt(TextArea messageArea, VBox chatBox) {
//        String userMessage = messageArea.getText();
//        if (!userMessage.isEmpty()) {
//            // Ajouter le message de l'utilisateur en haut de la zone de chat
//            HBox userMessageBox = createMessageBox(userMessage, Color.CORNFLOWERBLUE, Pos.CENTER_RIGHT);
//            chatBox.getChildren().add(0, userMessageBox); // Ajouter le message de l'utilisateur en haut
//
//            messageArea.clear(); // Efface le contenu de la zone de texte après l'envoi
//
//            // Appeler la fonction pour générer une réponse du chatbot après un délai
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                String botResponse = getBotResponse(userMessage);
//                HBox botMessageBox = createMessageBox(botResponse, Color.LIGHTBLUE.darker(), Pos.CENTER_LEFT);
//                chatBox.getChildren().add(botMessageBox); // Ajouter la réponse du chatbot en bas
//
//                // Faire défiler vers le bas après l'ajout du message
//                scrollToBottom(chatBox);
//            }));
//            timeline.setCycleCount(1);
//            timeline.play();
//        } else {
//            System.out.println("No message to send.");
//        }
//    }

//    private void sendPrompt(TextArea messageArea, VBox chatBox) {
//        String userMessage = messageArea.getText();
//        if (!userMessage.isEmpty()) {
//            // Ajouter le message de l'utilisateur en bas de la zone de chat
//            HBox userMessageBox = createMessageBox(userMessage, Color.CORNFLOWERBLUE, Pos.CENTER_RIGHT);
//            chatBox.getChildren().add(userMessageBox); // Ajouter en bas
//
//            messageArea.clear(); // Efface le contenu de la zone de texte après l'envoi
//
//            // Appeler la fonction pour générer une réponse du chatbot après un délai
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                String botResponse = getBotResponse(userMessage);
//                HBox botMessageBox = createMessageBox(botResponse, Color.LIGHTBLUE.darker(), Pos.CENTER_LEFT);
//                chatBox.getChildren().add(botMessageBox); // Ajouter en bas
//
//                // Faire défiler vers le bas après l'ajout du message
//                scrollToBottom(chatBox);
//            }));
//            timeline.setCycleCount(1);
//            timeline.play();
//        } else {
//            System.out.println("No message to send.");
//        }
//    }

    private void sendPrompt(TextArea messageArea, VBox chatBox, Button sendButton) {
        String userMessage = messageArea.getText();
        if (!userMessage.isEmpty()) {
            // Désactiver le bouton Send pendant le traitement
            sendButton.setDisable(true);

            // Ajouter le message de l'utilisateur en bas de la zone de chat
            HBox userMessageBox = createMessageBox(userMessage, Color.CORNFLOWERBLUE, Pos.CENTER_RIGHT);
            chatBox.getChildren().add(userMessageBox);

            messageArea.clear(); // Effacer la zone de texte après l'envoi

            // Appeler la fonction pour générer une réponse du chatbot après un délai
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                String botResponse = getBotResponse(userMessage);
                HBox botMessageBox = createMessageBox(botResponse, Color.LIGHTBLUE.darker(), Pos.CENTER_LEFT);
                chatBox.getChildren().add(botMessageBox);

                // Faire défiler vers le bas après l'ajout du message
                scrollToBottom(chatBox);

                // Réactiver le bouton Send après le traitement
                sendButton.setDisable(false);
            }));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            System.out.println("No message to send.");
        }
    }



    // Fonction pour faire défiler la fenêtre de chat vers le bas
    private void scrollToBottom(VBox chatBox) {
        // Utiliser un Timeline ou un autre moyen pour faire défiler la fenêtre vers le bas après un message
        Timeline scrollTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            chatBox.layout(); // Réajuster la disposition de la VBox pour appliquer les changements
        }));
        scrollTimeline.play();
    }

    Mainn mainn = new Mainn();

    // Fonction pour générer une réponse du chatbot
    private String getBotResponse(String userMessage) {
        String response = mainn.askQuestion(userMessage);
        System.out.println("result : " + response);
        return response;
    }

    // Fonction pour créer une boîte de message avec un style personnalisé
    private HBox createMessageBox(String message, Color backgroundColor, Pos alignment) {
        // Utilisation de TextFlow au lieu de Label pour gérer mieux le texte
        TextFlow messageFlow = new TextFlow();
        messageFlow.setStyle("-fx-font: 14 arial;"); // Appliquer une police similaire

        // Créer le texte et ajouter au TextFlow
        messageFlow.getChildren().add(new Text(message));

        // Définir l'arrière-plan et d'autres propriétés visuelles
        messageFlow.setBackground(new Background(new BackgroundFill(backgroundColor, new CornerRadii(10), Insets.EMPTY)));
        messageFlow.setTextAlignment(TextAlignment.LEFT);
        messageFlow.setMaxWidth(500); // Limiter la largeur, mais pas couper le texte
        messageFlow.setPadding(new Insets(10));

        // Ajout d'un effet d'ombre portée pour l'esthétique
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        messageFlow.setEffect(dropShadow);

        HBox messageBox = new HBox(messageFlow);
        messageBox.setAlignment(alignment);
        messageBox.setPadding(new Insets(5));

        return messageBox;
    }

    // Fonction pour charger un fichier PDF et afficher son chemin
//    private void loadAndDisplayPDFPath(VBox chatBox) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Load PDF File");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
//        File file = fileChooser.showOpenDialog(null);
//
//        if (file != null) {
//            String filePath = file.getAbsolutePath();
//            pdfPathLabel.setText("PDF loaded: " + filePath);
//            mainn.uploadUI(filePath);
//            System.out.println("PDF loaded: " + filePath);
//        } else {
//            System.out.println("No file selected.");
//        }
//    }

    private void loadAndDisplayPDFPath(VBox chatBox) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Désactiver le bouton Send pendant le traitement
            sendButton.setDisable(true);

            String filePath = file.getAbsolutePath();
            pdfPathLabel.setText("Processing PDF: " + filePath);
            System.out.println("Processing PDF: " + filePath);

            // Simuler le traitement avec un délai
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                try {
                    mainn.uploadUI(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pdfPathLabel.setText("PDF loaded: " + filePath);
                System.out.println("PDF loaded: " + filePath);

                // Réactiver le bouton Send après le traitement
                sendButton.setDisable(false);
            }));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            System.out.println("No file selected.");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
