package com.enset.test;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SimpleChatInterface extends Application {

    private Label pdfPathLabel;
    private Button sendButton;
    TextArea messageArea;
    private Mainn mainn = new Mainn();
    private LoadingIndicator loadingIndicator;
    private ScrollPane scrollPane;
    private VBox chatBox;  // Made this a class field
    private VBox conversationList;  // To store conversation history
    private int currentConversationId = 1;
    private Stage primaryStage;
    private ChatDatabaseManager dbManager = new ChatDatabaseManager();
    Scene scene;
    private SplitPane splitPane; // Déclarer le SplitPane comme variable de classe
    private boolean isConversationPanelVisible = true; // État du panneau des conversations
    Button toggleButton;
    private VBox mainSidebar; // Référence au sidebar principal
    private VBox iconSidebar; // Référence au sidebar des icônes
    private SpeechToText speechToText;
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadingIndicator = new LoadingIndicator();
        speechToText = new SpeechToText();
        // Créer le SplitPane pour le panneau des conversations et la zone de chat
        splitPane = new SplitPane();

        // Créer le sidebar principal et le sidebar des icônes une seule fois
        mainSidebar = createSideBar();
        iconSidebar = createIconSidebar();

        // Ajouter le sidebar principal au SplitPane par défaut
        splitPane.getItems().addAll(mainSidebar, createMainContent());

        // Root pane avec overlay capability
        StackPane rootPane = new StackPane();
        rootPane.getChildren().addAll(splitPane, loadingIndicator);

        // Set up the scene
        Scene scene = new Scene(rootPane);
        Image icon = new Image(getClass().getResourceAsStream("/ENSET-Mohammedia2.png"));
        primaryStage.getIcons().add(icon);

        // Configure stage
        primaryStage.setTitle("ENSET GUIDE");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000); // Largeur initiale
        primaryStage.setHeight(790); // Hauteur initiale
        primaryStage.show();

        // Charger les conversations existantes depuis la base de données
        List<Conversation> conversations = dbManager.getAllConversations();

        if (conversations.isEmpty()) {
            // Créer une nouvelle conversation par défaut si aucune conversation n'existe
            currentConversationId = dbManager.createNewConversation("New Chat");
            if (currentConversationId != -1) {
                System.out.println("Première conversation créée avec l'ID : " + currentConversationId);
                addConversationToList(currentConversationId);
            } else {
                showError("Failed to create default conversation. Please try again.");
            }
        } else {
            // Afficher les conversations existantes
            for (Conversation conversation : conversations) {
                addConversationToList(conversation.getId(), conversation.getTitle());
            }

            // Charger la première conversation par défaut
            if (!conversations.isEmpty()) {
                currentConversationId = conversations.get(0).getId();
                loadConversation(currentConversationId);
            }
        }
    }






    private VBox createSideBar() {
        VBox sideBar = new VBox(10);
        sideBar.setPadding(new Insets(15));
        sideBar.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-border-color: #dee2e6;" +
                        "-fx-border-width: 0 1 0 0;"+
                        "-fx-background-radius: 3;"
                        +"-fx-border-radius : 3;"
        );
        sideBar.setMaxWidth(230);
        sideBar.setMinWidth(230);
        sideBar.setPrefWidth(230);

        // Créer le bouton "New Chat"
        Button newChatButton = createNewChatButton();

        // Créer le ToggleButton
        ToggleButton toggleButton = createToggleButton();

        // Conteneur pour aligner "New Chat" et le ToggleButton horizontalement
        HBox buttonContainer = new HBox(100); // Espacement de 60 entre les boutons
        buttonContainer.getChildren().addAll(newChatButton, toggleButton);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        // Ajouter le conteneur de boutons à la barre latérale
        sideBar.getChildren().add(buttonContainer);

        // Créer la liste des conversations
        conversationList = new VBox(8);
        conversationList.setPadding(new Insets(10, 0, 0, 0));
        ScrollPane conversationScroll = new ScrollPane(conversationList);
        conversationScroll.setFitToWidth(true);
        conversationScroll.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );

        VBox.setVgrow(conversationScroll, Priority.ALWAYS);
        sideBar.getChildren().add(conversationScroll);

        return sideBar;
    }

    private ToggleButton createToggleButton() {
        // Charger l'image depuis les ressources
        Image image = new Image(getClass().getResourceAsStream("/icons8-arrière-64.png"));
        ImageView imageView = new ImageView(image);

        // Redimensionner l'image si nécessaire
        imageView.setFitWidth(24); // Largeur de l'icône
        imageView.setFitHeight(24); // Hauteur de l'icône

        // Créer le ToggleButton avec l'icône
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(imageView); // Ajouter l'image au bouton

        // Appliquer un style au bouton
        toggleButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 5px;" +
                        "-fx-cursor: hand;"
        );

        // Ajouter une infobulle (tooltip)
        Tooltip tooltip = new Tooltip("Toggle Settings");
        tooltip.setFont(Font.font("Arial", 12));
        Tooltip.install(toggleButton, tooltip);

        // Effet de survol
        toggleButton.setOnMouseEntered(e -> {
            toggleButton.setStyle(
                    "-fx-background-color: #e0e0e0;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 5px;" +
                            "-fx-cursor: hand;"
            );
        });

        toggleButton.setOnMouseExited(e -> {
            toggleButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 5px;" +
                            "-fx-cursor: hand;"
            );
        });

        // Gérer l'action du ToggleButton
        toggleButton.setOnAction(e -> toggleConversationPanel());

        return toggleButton;
    }

    private VBox createIconSidebar() {
        VBox iconSidebar = new VBox(10); // Espacement vertical de 10 entre les icônes
        iconSidebar.setPadding(new Insets(15));
        iconSidebar.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-border-color: #dee2e6;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        // Définir une largeur fixe pour le sidebar des icônes
        iconSidebar.setMinWidth(70); // Largeur minimale de 70 pixels
        iconSidebar.setPrefWidth(70); // Largeur préférée de 70 pixels
        iconSidebar.setMaxWidth(70); // Largeur maximale de 70 pixels

        // Charger les images PNG
        Image newChatImage = new Image(getClass().getResourceAsStream("/icons8-ajouter-au-chat-50.png"));
        Image settingsImage = new Image(getClass().getResourceAsStream("/icons8-arrière-64(1).png"));


        // Icône 1 : Nouvelle conversation
        Button newChatIcon = createIconButtonWithImage(newChatImage, "New chat");
        newChatIcon.setOnAction(e -> startNewConversation());

        // Icône 2 : Paramètres
        Button openIcon = createIconButtonWithImage(settingsImage, "open sidebar");
        openIcon.setOnAction(e -> toggleConversationPanel());


        // Ajouter les icônes au sidebar
        iconSidebar.getChildren().addAll(openIcon,newChatIcon);

        return iconSidebar;
    }

    private Button createIconButtonWithImage(Image image, String tooltip) {
        Button button = new Button();
        ImageView imageView = new ImageView(image);

        // Redimensionner l'image si nécessaire
        imageView.setFitWidth(24); // Largeur de l'icône
        imageView.setFitHeight(24); // Hauteur de l'icône

        // Ajouter l'image au bouton
        button.setGraphic(imageView);

        // Style du bouton
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 5px;" +
                        "-fx-cursor: hand;"
        );

        // Ajouter une infobulle (tooltip)
        Tooltip tooltipObj = new Tooltip(tooltip);
        tooltipObj.setFont(Font.font("Arial", 12));
        Tooltip.install(button, tooltipObj);

        return button;
    }



    private void toggleConversationPanel() {
        if (isConversationPanelVisible) {
            // Remplacer le sidebar principal par le sidebar des icônes
            splitPane.getItems().set(0, iconSidebar);
        } else {
            // Remplacer le sidebar des icônes par le sidebar principal
            splitPane.getItems().set(0, mainSidebar);
        }
        isConversationPanelVisible = !isConversationPanelVisible; // Inverser l'état
    }

    private Button createIconButton(String icon, String tooltip) {
        Button button = new Button(icon);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 10px;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 18px;" +
                        "-fx-text-fill: #666666;"
        );

        // Ajouter une infobulle (tooltip)
        Tooltip tooltipObj = new Tooltip(tooltip);
        tooltipObj.setFont(Font.font("Arial", 12));
        Tooltip.install(button, tooltipObj);

        return button;
    }


    private Button createNewChatButton() {
        Button newChatButton = new Button("New");

// Create a Text node for the "+" icon
        Text plusIcon = new Text("+");
        plusIcon.setFont(Font.font("Arial", FontWeight.BOLD, 13)); // Set font size and weight
        plusIcon.setFill(Color.WHITE); // Set text color to white

// Set the Text node as the graphic for the button
        newChatButton.setGraphic(plusIcon);
         // Add plus icon
        newChatButton.setMaxWidth(Double.MAX_VALUE);
        newChatButton.setPadding(new Insets(10));
        newChatButton.setStyle(
                "-fx-background-color: #0a4b83;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        // Add hover effect
        newChatButton.setOnMouseEntered(e ->
                newChatButton.setStyle(newChatButton.getStyle() + "-fx-background-color: #0d5ca0;"));
        newChatButton.setOnMouseExited(e ->
                newChatButton.setStyle(newChatButton.getStyle() + "-fx-background-color: #0a4b83;"));

        newChatButton.setOnAction(e -> startNewConversation());

        return newChatButton;
    }

    private VBox createMainContent() {
        // Create header
        HBox header = createHeader();
        header.setBorder(null);

        // Create chat area
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));

        // Configure ScrollPane
        scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setBackground(new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(10),
                Insets.EMPTY
        )));
        scrollPane.setStyle(
                "-fx-background: white;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;"
        );

        // Create message input area
        HBox messageInputContainer = createMessageInputContainer(chatBox);
        messageInputContainer.setBorder(null);

        // Create PDF path label
        pdfPathLabel = new Label();
        pdfPathLabel.setAlignment(Pos.CENTER);
        pdfPathLabel.setFont(Font.font("Arial", 14));
        pdfPathLabel.setPadding(new Insets(10));
        pdfPathLabel.setBackground(new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(5),
                Insets.EMPTY
        )));
        pdfPathLabel.setTextFill(Color.BLACK);
        pdfPathLabel.setWrapText(true);
        pdfPathLabel.setVisible(false);

        // Add shadow effects
        DropShadow inputShadow = new DropShadow();
        inputShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        inputShadow.setRadius(10);
        inputShadow.setOffsetY(2);
        messageInputContainer.setEffect(inputShadow);

        DropShadow scrollShadow = new DropShadow();
        scrollShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        scrollShadow.setRadius(10);
        scrollShadow.setOffsetY(2);
        scrollPane.setEffect(scrollShadow);

        // Create main container
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        mainContainer.setBackground(new Background(new BackgroundFill(
                Color.web("#0a4b83"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        mainContainer.setAlignment(Pos.CENTER);

        // Add components to main container
        mainContainer.getChildren().addAll(header, scrollPane, messageInputContainer, pdfPathLabel);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }


    private HBox createHeader() {
        // Main header container
        HBox header = new HBox();
        header.setBorder(null);
        header.setAlignment(Pos.CENTER); // Center the content
        header.setPadding(new Insets(10)); // Padding around header
        header.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        header.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");


        // Configure logo
        ImageView logoImage = new ImageView(new Image(new File("C:\\Users\\HP\\OneDrive\\Bureau\\test\\rag_first\\src\\main\\resources\\ENSET-Mohammedia2.png").toURI().toString()));
        logoImage.setFitHeight(45); // Default height for the logo
        logoImage.setPreserveRatio(true); // Maintain aspect ratio

        // Create text label
        Label guideLabel = new Label("GUIDE");
        guideLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        guideLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 35)); // Default font size
        guideLabel.setTextFill(Color.web("#0a4b83"));

        // Add logo and text to header
        header.getChildren().addAll(logoImage, guideLabel);
        header.setSpacing(5); // Spacing between logo and text

        // Responsiveness: Adjust logo size dynamically
        header.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            if (width < 400) {
                logoImage.setFitHeight(25); // Smaller logo for narrow widths
                guideLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
            } else if (width < 700) {
                logoImage.setFitHeight(30); // Medium size
                guideLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 25));
            } else {
                logoImage.setFitHeight(45); // Default size
                guideLabel.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 35));
            }
        });

        return header;
    }





    private HBox createMessageInputContainer(VBox chatBox) {
        // Message input area
        messageArea = new TextArea();
        messageArea.setBorder(null);
        messageArea.setPromptText("Enter your message...");
        messageArea.setPrefRowCount(1);
        messageArea.setFont(Font.font("Arial", 14));
        messageArea.setWrapText(true);
        messageArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        messageArea.setPadding(new Insets(10));

        // Create the record button
        Button recordButton = createRecordButton();

        // Create other buttons (send, load PDF, clear)
        sendButton = createStyledButton(">", Color.web("#0a4b83"), "Send message");
        Button loadPdfButton = createStyledButton("+", Color.DARKGRAY, "Load PDF file");
        Button clearButton = createStyledButton("🗑", Color.RED, "Delete history");

        // Set button actions
        sendButton.setOnAction(event -> sendPrompt(messageArea, chatBox, sendButton));
        loadPdfButton.setOnAction(event -> loadAndDisplayPDFPath(chatBox));
        clearButton.setOnAction(event -> handleClearAction(chatBox));

        // Handle Enter key press in the message area
        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                sendPrompt(messageArea, chatBox, sendButton);
            }
        });

        // Create button container
        HBox buttonContainer = new HBox(5);
        buttonContainer.getChildren().addAll(sendButton, loadPdfButton, clearButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(0, 0, 0, 5));

        // Create main container for message input area
        HBox messageInputContainer = new HBox(10);
        messageInputContainer.setAlignment(Pos.CENTER);
        messageInputContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        messageInputContainer.setPadding(new Insets(10));
        messageInputContainer.setBorder(null);

        // Add the record button, message area, and button container
        messageInputContainer.getChildren().addAll(recordButton, messageArea, buttonContainer);
        HBox.setHgrow(messageArea, Priority.ALWAYS);

        // Add shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        messageInputContainer.setEffect(dropShadow);

        return messageInputContainer;
    }

    private void startVoiceRecording() {
        // Start recording and get the recognized text
        String recognizedText = speechToText.startVoiceRecording();

        // Insert the recognized text into the message area
        if (recognizedText != null && !recognizedText.isEmpty()) {
            Platform.runLater(() -> {
                messageArea.appendText(recognizedText + " ");
            });
        }
    }
    private Button createRecordButton() {
        // Load the microphone icon
        Image micImage = new Image(getClass().getResourceAsStream("/icons8-microphone-48.png"));
        ImageView micIcon = new ImageView(micImage);

        // Increase icon size
        micIcon.setFitWidth(32);  // Increased from 24
        micIcon.setFitHeight(32); // Increased from 24

        // Create the record button
        Button recordButton = new Button();
        recordButton.setGraphic(micIcon);
        recordButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 8px;" + // Increased padding
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER;" // Ensure center alignment
        );

        // Set minimum size to ensure consistent spacing
        recordButton.setMinSize(48, 48); // Added minimum size
        recordButton.setMaxSize(48, 48); // Added maximum size to maintain square shape

        // Add a tooltip
        Tooltip tooltip = new Tooltip("Record Voice");
        tooltip.setFont(Font.font("Arial", 12));
        Tooltip.install(recordButton, tooltip);

        // Add hover effect
        recordButton.setOnMouseEntered(e -> {
            recordButton.setStyle(
                    "-fx-background-color: #e0e0e0;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 8px;" +
                            "-fx-cursor: hand;" +
                            "-fx-alignment: CENTER"
            );
        });

        recordButton.setOnMouseExited(e -> {
            recordButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 8px;" +
                            "-fx-cursor: hand;" +
                            "-fx-alignment: CENTER"
            );
        });

        // Handle the record button click
        recordButton.setOnAction(e -> startVoiceRecording());

        return recordButton;
    }

    // Ajouter cette nouvelle méthode pour créer des boutons stylisés avec effets de survol
    private Button createStyledButton(String text, Color baseColor, String tooltipText) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        button.setTextFill(Color.WHITE);
        button.setMinSize(40, 40);
        button.setMaxSize(40, 40);

        // Création des backgrounds pour différents états
        BackgroundFill normalFill = new BackgroundFill(baseColor, new CornerRadii(5), Insets.EMPTY);
        BackgroundFill hoverFill = new BackgroundFill(baseColor.brighter(), new CornerRadii(5), Insets.EMPTY);
        BackgroundFill pressedFill = new BackgroundFill(baseColor.darker(), new CornerRadii(5), Insets.EMPTY);

        Background normalBg = new Background(normalFill);
        Background hoverBg = new Background(hoverFill);
        Background pressedBg = new Background(pressedFill);

        // Appliquer le style normal
        button.setBackground(normalBg);

        // Ajout des effets de survol
        button.setOnMouseEntered(e -> {
            button.setBackground(hoverBg);
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setBackground(normalBg);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        button.setOnMousePressed(e -> button.setBackground(pressedBg));
        button.setOnMouseReleased(e -> button.setBackground(normalBg));

        // Ajout d'une infobulle (tooltip)
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setFont(Font.font("Arial", 12));
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(button, tooltip);

        // Ajustement de la couleur du texte pour le bouton PDF
        if (baseColor == Color.LIGHTGRAY) {
            button.setTextFill(Color.BLACK);
        }

        return button;
    }

    private String extractContextFromMessage(String message) {
        // Extraire les premiers mots du message (par exemple, les 5 premiers mots)
        String[] words = message.split("\\s+");
        int maxWords = Math.min(5, words.length); // Limiter à 5 mots
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            context.append(words[i]).append(" ");
        }
        return context.toString().trim(); // Retirer l'espace final
    }

    private void handleClearAction(VBox chatBox) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Clear History");
        alert.setContentText("Are you sure you want to clear all chat history for this conversation?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20px;" +
                        "-fx-font-family: 'System';"
        );

        dialogPane.lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-radius: 4px;"
        );

        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-radius: 4px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear database history
            dbManager.clearConversationHistory(currentConversationId);
            dbManager.clearDatabase(currentConversationId);

            // Réinitialiser le titre de la conversation
            dbManager.updateConversationTitle(currentConversationId, "New Chat");

            // Mettre à jour le titre dans l'interface utilisateur
            updateConversationTitleInList(currentConversationId, "New Chat");

            // Clear UI
            chatBox.getChildren().clear();
            messageArea.clear();
            pdfPathLabel.setVisible(false);

            // Show confirmation message
            HBox confirmationBox = createMessageBox("Chat history cleared.", Color.GREENYELLOW, Pos.CENTER);
            chatBox.getChildren().add(confirmationBox);

            // Add fade-out animation for confirmation message
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), confirmationBox);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> chatBox.getChildren().remove(confirmationBox));
            fadeOut.play();
        }
    }



    private void confirmAndDeleteConversation(int conversationId, HBox conversationContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Conversation");
        alert.setContentText("Are you sure you want to delete this conversation?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20px;" +
                        "-fx-font-family: 'System';"
        );

        dialogPane.lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-radius: 4px;"
        );

        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-radius: 4px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isDeleted = dbManager.deleteConversation(conversationId);
            if (isDeleted) {
                // Supprimer le conteneur de la conversation de la liste
                conversationList.getChildren().remove(conversationContainer);

                // Si la conversation supprimée est celle actuellement affichée, vider la boîte de chat
                if (conversationId == currentConversationId) {
                    chatBox.getChildren().clear();
                    messageArea.clear();
                    pdfPathLabel.setVisible(false);
                }
            } else {
                showError("Failed to delete the conversation. Please try again.");
            }
        }
    }


    private void showError(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(errorMessage);

            // Appliquer un style moderne à la boîte de dialogue
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/style.css").toExternalForm() // Lien vers un fichier CSS externe
            );
            dialogPane.getStyleClass().add("error-dialog");

            // Style personnalisé pour les boutons
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            Node okButtonNode = dialogPane.lookupButton(okButton);
            okButtonNode.setStyle(
                    "-fx-background-color: #ff4444;" + // Couleur de fond rouge
                            "-fx-text-fill: white;" +          // Texte blanc
                            "-fx-font-size: 14px;" +           // Taille de police
                            "-fx-padding: 8px 16px;" +         // Rembourrage
                            "-fx-background-radius: 5;" +      // Coins arrondis
                            "-fx-border-radius: 5;" +          // Coins arrondis pour la bordure
                            "-fx-cursor: hand;"                // Curseur en forme de main
            );

            // Ajouter un effet de survol au bouton
            okButtonNode.setOnMouseEntered(e -> {
                okButtonNode.setStyle(
                        "-fx-background-color: #ff6666;" + // Couleur de fond plus claire au survol
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 8px 16px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;" +
                                "-fx-cursor: hand;"
                );
            });

            okButtonNode.setOnMouseExited(e -> {
                okButtonNode.setStyle(
                        "-fx-background-color: #ff4444;" + // Revenir à la couleur d'origine
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 8px 16px;" +
                                "-fx-background-radius: 5;" +
                                "-fx-border-radius: 5;" +
                                "-fx-cursor: hand;"
                );
            });

            // Afficher la boîte de dialogue
            alert.showAndWait();
        });
    }

    private void loadConversation(int conversationId) {
        currentConversationId = conversationId; // Mettre à jour l'identifiant de conversation actuel
        chatBox.getChildren().clear();

        List<Message> messages = dbManager.getConversationMessages(conversationId);
        for (Message msg : messages) {
            HBox messageBox = createMessageBox(
                    msg.getText(),
                    msg.isUser() ? Color.LIGHTGRAY : Color.LIGHTBLUE,
                    msg.isUser() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT
            );
            chatBox.getChildren().add(messageBox);
        }

        scrollToBottom(chatBox);
    }

    private void addConversationToList(int conversationId) {
        addConversationToList(conversationId, "New Chat"); // Appelle la méthode avec un titre par défaut
    }

    private void addConversationToList(int conversationId, String title) {
        HBox conversationContainer = new HBox();
        conversationContainer.setUserData(conversationId);
        conversationContainer.setAlignment(Pos.CENTER_LEFT);
        conversationContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10;" +
                        "-fx-cursor: hand;"
        );

        // Titre de la conversation
        Label titleLabel = new Label(title.isEmpty() ? "New Chat" : title);
        titleLabel.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #333333;" +
                        "-fx-font-weight: bold;" +
                        "-fx-min-width: 150px;" + // Largeur minimale du Label
                        "-fx-padding: 0 97px 0 0;" // Padding à droite pour l'espace avant les trois points
        );

        // Bouton des trois points (menu contextuel)
        Button optionsButton = new Button("•••");
        optionsButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #666666;" +
                        "-fx-font-weight: bold;"
        );

        // Conteneur pour le titre et les trois points
        HBox titleContainer = new HBox(10); // Espacement de 10 pixels entre les éléments
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setMinWidth(170); // Largeur minimale du conteneur
        titleContainer.setPrefWidth(170); // Largeur préférée du conteneur
        titleContainer.getChildren().addAll(titleLabel, optionsButton);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle(
                "-fx-background-radius: 5;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-radius: 5;"
        );

        // Créer un Label pour "Renommer"
        Label renameLabel = new Label("Renommer");
        renameLabel.setStyle(
                "-fx-font-size: 10px; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-padding: 5px 10px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        // Créer un Label pour "Supprimer"
        Label deleteLabel = new Label("Supprimer");
        deleteLabel.setStyle(
                "-fx-font-size: 10px; " +
                        "-fx-text-fill: #ff4444; " +
                        "-fx-padding: 5px 10px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        // Effet de survol pour "Renommer"
        renameLabel.setOnMouseEntered(e -> {
            renameLabel.setStyle(
                    "-fx-font-size: 10px; " +
                            "-fx-text-fill: #333333; " +
                            "-fx-padding: 5px 10px; " +
                            "-fx-background-color: #f0f0f0; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );
        });

        renameLabel.setOnMouseExited(e -> {
            renameLabel.setStyle(
                    "-fx-font-size: 10px; " +
                            "-fx-text-fill: #333333; " +
                            "-fx-padding: 5px 10px; " +
                            "-fx-background-color: transparent; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );
        });

        // Effet de survol pour "Supprimer"
        deleteLabel.setOnMouseEntered(e -> {
            deleteLabel.setStyle(
                    "-fx-font-size: 10px; " +
                            "-fx-text-fill: #ff4444; " +
                            "-fx-padding: 5px 10px; " +
                            "-fx-background-color: #f0f0f0; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );
        });

        deleteLabel.setOnMouseExited(e -> {
            deleteLabel.setStyle(
                    "-fx-font-size: 10px; " +
                            "-fx-text-fill: #ff4444; " +
                            "-fx-padding: 5px 10px; " +
                            "-fx-background-color: transparent; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;"
            );
        });

        // Créer des CustomMenuItem pour encapsuler les Labels
        CustomMenuItem renameMenuItem = new CustomMenuItem(renameLabel);
        CustomMenuItem deleteMenuItem = new CustomMenuItem(deleteLabel);

        // Désactiver le comportement de fermeture du menu lors du clic
        renameMenuItem.setHideOnClick(false);
        deleteMenuItem.setHideOnClick(false);

        // Ajouter des actions aux Labels
        renameLabel.setOnMouseClicked(e -> renameConversation(conversationId, titleLabel));
        deleteLabel.setOnMouseClicked(e -> confirmAndDeleteConversation(conversationId, conversationContainer));

        // Ajouter les CustomMenuItem au ContextMenu
        contextMenu.getItems().addAll(renameMenuItem, deleteMenuItem);


        // Associer le ContextMenu au bouton optionsButton
        optionsButton.setOnAction(e -> contextMenu.show(optionsButton, Side.BOTTOM, 0, 0));

        // Ajouter le conteneur du titre et des trois points au conteneur principal
        conversationContainer.getChildren().add(titleContainer);

        // Gérer les événements de survol pour le conteneur
        conversationContainer.setOnMouseEntered(e ->
                conversationContainer.setStyle(
                        "-fx-background-color: #f0f0f0;" +
                                "-fx-border-color: #cccccc;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 10;" +
                                "-fx-cursor: hand;"
                ));
        conversationContainer.setOnMouseExited(e ->
                conversationContainer.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-border-color: #cccccc;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-padding: 10;" +
                                "-fx-cursor: hand;"
                ));

        // Charger la conversation lors du clic sur le conteneur
        conversationContainer.setOnMouseClicked(e -> loadConversation(conversationId));

        // Ajouter le conteneur à la liste des conversations
        conversationList.getChildren().add(0, conversationContainer);
    }
    private void renameConversation(int conversationId, Label conversationBtn) {
        // Créer un TextField pour l'édition en direct
        TextField textField = new TextField(conversationBtn.getText());
        textField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 10;"
        );

        // Remplacer le bouton par le TextField
        HBox parentContainer = (HBox) conversationBtn.getParent();
        int index = parentContainer.getChildren().indexOf(conversationBtn);
        parentContainer.getChildren().set(index, textField);

        // Focus sur le TextField pour permettre l'édition immédiate
        textField.requestFocus();

        // Gérer la fin de l'édition (lorsque l'utilisateur appuie sur Entrée ou perd le focus)
        textField.setOnAction(e -> {
            String newTitle = textField.getText().trim();
            if (!newTitle.isEmpty()) {
                // Mettre à jour le titre dans la base de données
                dbManager.updateConversationTitle(conversationId, newTitle);

                // Remplacer le TextField par le bouton mis à jour
                conversationBtn.setText(newTitle);
                parentContainer.getChildren().set(index, conversationBtn);
            }
        });

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Si le TextField perd le focus
                String newTitle = textField.getText().trim();
                if (!newTitle.isEmpty()) {
                    // Mettre à jour le titre dans la base de données
                    dbManager.updateConversationTitle(conversationId, newTitle);

                    // Remplacer le TextField par le bouton mis à jour
                    conversationBtn.setText(newTitle);
                    parentContainer.getChildren().set(index, conversationBtn);
                } else {
                    // Si le champ est vide, revenir au bouton sans modification
                    parentContainer.getChildren().set(index, conversationBtn);
                }
            }
        });
    }

    private void startNewConversation() {
        if (currentConversationId != -1) {
            // Sauvegarder la conversation actuelle avant de démarrer une nouvelle
            saveCurrentConversation();
        }

        chatBox.getChildren().clear();
        messageArea.clear();
        pdfPathLabel.setVisible(false);

        currentConversationId = dbManager.createNewConversation("New Chat");

        if (currentConversationId != -1) {
            addConversationToList(currentConversationId); // Utilise la version sans titre
        } else {
            showError("Failed to create new conversation. Please try again.");
        }
    }

    private void saveCurrentConversation() {
        // Sauvegarder les messages de la conversation actuelle
        List<Message> messages = dbManager.getConversationMessages(currentConversationId);
        // Vous pouvez ajouter ici une logique pour sauvegarder les messages si nécessaire
    }

    private void updateConversationTitleInList(int conversationId, String newTitle) {
        for (Node node : conversationList.getChildren()) {
            if (node instanceof HBox) {
                HBox conversationContainer = (HBox) node;
                Integer containerConversationId = (Integer) conversationContainer.getUserData();

                if (containerConversationId != null && containerConversationId == conversationId) {
                    // Accéder au HBox titleContainer qui est le premier enfant
                    HBox titleContainer = (HBox) conversationContainer.getChildren().get(0);
                    // Le Label est le premier enfant du titleContainer
                    Label titleLabel = (Label) titleContainer.getChildren().get(0);
                    titleLabel.setText(newTitle);
                    break;
                }
            }
        }
    }


    private ExecutorService executorService = Executors.newCachedThreadPool();

    private void sendPrompt(TextArea messageArea, VBox chatBox, Button sendButton) {
        String userMessage = messageArea.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        if (currentConversationId == -1) {
            showError("No conversation is active. Please start a new conversation.");
            return;
        }

        // Désactiver le bouton d'envoi
        sendButton.setDisable(true);

        // Vérifier si c'est le premier message de la conversation
        List<Message> existingMessages = dbManager.getConversationMessages(currentConversationId);
        boolean isFirstMessage = existingMessages.isEmpty();

        // Sauvegarder le message de l'utilisateur dans la conversation
        saveMessageToConversation(currentConversationId, userMessage, true);

        // Afficher immédiatement le message de l'utilisateur
        addUserMessageToChat(userMessage, chatBox);
        messageArea.clear();

        // Si c'est le premier message, mettre à jour le titre de la conversation
        if (isFirstMessage) {
            String newTitle = generateConversationTitle(userMessage);
            dbManager.updateConversationTitle(currentConversationId, newTitle);
            Platform.runLater(() -> {
                updateConversationTitleInList(currentConversationId, newTitle);
            });
        }

        // Afficher l'animation des points de chargement
        HBox loadingBox = createLoadingMessage();
        chatBox.getChildren().add(loadingBox);
        scrollToBottom(chatBox);

        // Créer et démarrer l'animation des points
        Timeline dotsTimeline = createDotsAnimation(loadingBox);
        dotsTimeline.play();

        // Obtenir la réponse du bot de manière asynchrone
        executorService.submit(() -> {
            String botResponse = getBotResponse(userMessage);

            Platform.runLater(() -> {
                // Supprimer l'animation des points
                dotsTimeline.stop();
                chatBox.getChildren().remove(loadingBox);

                // Sauvegarder et afficher la réponse du bot
                saveMessageToConversation(currentConversationId, botResponse, false);
                addBotResponseToChat(botResponse, chatBox, sendButton);

                // Réactiver le bouton d'envoi
                sendButton.setDisable(false);
            });
        });
    }

    private String generateConversationTitle(String message) {
        // Nettoyer le message des caractères spéciaux et des espaces supplémentaires
        String cleaned = message.replaceAll("[^a-zA-Z0-9\\s]", "").trim();

        // Diviser en mots
        String[] words = cleaned.split("\\s+");

        // Prendre les 4 premiers mots ou moins si le message est plus court
        StringBuilder title = new StringBuilder();
        int wordLimit = Math.min(4, words.length);

        for (int i = 0; i < wordLimit; i++) {
            // Capitaliser le premier caractère de chaque mot
            String word = words[i];
            if (!word.isEmpty()) {
                title.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
                if (i < wordLimit - 1) {
                    title.append(" ");
                }
            }
        }

        // Si le titre est trop long, le tronquer et ajouter "..."
        String finalTitle = title.toString();
        if (finalTitle.length() > 30) {
            finalTitle = finalTitle.substring(0, 27) + "...";
        }

        return finalTitle;
    }




    private static final Logger logger = Logger.getLogger(SimpleChatInterface.class.getName());

    private void saveMessageToConversation(int conversationId, String message, boolean isUser) {
        if (message == null || message.trim().isEmpty()) {
            logger.warning("Cannot save an empty message.");
            return;
        }

        if (conversationId <= 0) {
            logger.warning("Invalid conversation ID: " + conversationId);
            return;
        }

        try {
            boolean success = dbManager.saveMessage(conversationId, message, isUser);
            if (!success) {
                logger.severe("Failed to save the message.");
                showError("Failed to save the message. Please try again.");
            }
        } catch (Exception e) {
            logger.severe("An error occurred while saving the message: " + e.getMessage());
            showError("An error occurred while saving the message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveMessageToConversationAsync(int conversationId, String message, boolean isUser) {
        new Thread(() -> saveMessageToConversation(conversationId, message, isUser)).start();
    }



    private HBox createLoadingMessage() {
        HBox loadingBox = createMessageBox("", Color.LIGHTGREY, Pos.CENTER_LEFT);
        Text loadingText = new Text(".");
        ((TextFlow) loadingBox.getChildren().get(0)).getChildren().setAll(loadingText);
        return loadingBox;
    }

    private Timeline createDotsAnimation(HBox loadingBox) {
        Text loadingText = (Text) ((TextFlow) loadingBox.getChildren().get(0)).getChildren().get(0);
        return new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> loadingText.setText("..")),
                new KeyFrame(Duration.seconds(1.0), e -> loadingText.setText("....")),
                new KeyFrame(Duration.seconds(1.5), e -> loadingText.setText("......"))
        );
    }

    private void addUserMessageToChat(String message, VBox chatBox) {
        HBox userMessageBox = createMessageBox(message, Color.LIGHTGRAY, Pos.CENTER_RIGHT);
        chatBox.getChildren().add(userMessageBox);
        scrollToBottom(chatBox); // Faire défiler vers le bas après l'ajout du message
    }

    private void addBotResponseToChat(String response, VBox chatBox, Button sendButton) {
        HBox botMessageBox = createMessageBox("", Color.LIGHTBLUE, Pos.CENTER_LEFT);
        Text responseText = new Text("");
        ((TextFlow) botMessageBox.getChildren().get(0)).getChildren().setAll(responseText);
        chatBox.getChildren().add(botMessageBox);

        Timeline typingTimeline = new Timeline();
        for (int i = 0; i < response.length(); i++) {
            final int index = i;
            typingTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(30 * i), e -> {
                responseText.setText(response.substring(0, index + 1));
                scrollToBottom(chatBox); // Faire défiler vers le bas à chaque mise à jour du texte
            }));
        }
        typingTimeline.setOnFinished(e -> {
            sendButton.setDisable(false);
            scrollToBottom(chatBox); // Faire défiler vers le bas une dernière fois à la fin
        });
        typingTimeline.play();
    }

    private void scrollToBottom(VBox chatBox) {
        Platform.runLater(() -> {
            chatBox.applyCss();
            chatBox.layout();

            double targetVvalue = 1.0;
            double currentVvalue = scrollPane.getVvalue();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(100), new KeyValue(scrollPane.vvalueProperty(), targetVvalue))
            );
            timeline.play();
        });
    }

    private String getBotResponse(String userMessage) {
        // Récupérer le contexte de la conversation actuelle
        String context = dbManager.getConversationContext(currentConversationId);

        // Construire le prompt avec le contexte et le nouveau message
        String prompt = context != null ? context + "\n" + userMessage : userMessage;

        // Obtenir la réponse du modèle RAG en utilisant l'identifiant de conversation
        String response = mainn.askQuestion(prompt, currentConversationId);

        // Mettre à jour le contexte avec la nouvelle interaction
        String newContext = prompt + "\n" + response;
        dbManager.updateConversationContext(currentConversationId, newContext);

        return response;
    }

    private HBox createMessageBox(String message, Color backgroundColor, Pos alignment) {
        TextFlow messageFlow = new TextFlow();
        messageFlow.setStyle("-fx-font: 14 arial;");

        Text text = new Text(message);
        messageFlow.getChildren().add(text);

        messageFlow.setBackground(new Background(new BackgroundFill(
                backgroundColor, new CornerRadii(10), Insets.EMPTY)));
        messageFlow.setTextAlignment(TextAlignment.LEFT);
        messageFlow.setMaxWidth(500);
        messageFlow.setPadding(new Insets(10));

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



// Dans votre constructeur ou méthode d'initialisation

// Ajoutez le loadingIndicator à votre layout principal (supposons que c'est un StackPane rootPane)


    private void loadAndDisplayPDFPath(VBox chatBox) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load File");

        // Définir les filtres d'extension pour tous les types de fichiers souhaités
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.jpg", "*.jpeg", "*.png", "*.txt", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Word Files", "*.doc", "*.docx")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            sendButton.setDisable(true);
            String filePath = file.getAbsolutePath();

            // Démarrer l'indicateur de chargement
            loadingIndicator.start("Processing file...");

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                try (InputStream inputStream = new FileInputStream(file)) {
                    // Charger le fichier et associer les données à la conversation actuelle
                    boolean success = mainn.uploadUI(filePath, currentConversationId);
                    if (!success) {
                        showError("Failed to load file. Please try again.");
                        return;
                    }
                } catch (IOException e) {
                    showError("An error occurred while loading the file: " + e.getMessage());
                    return;
                }
                pdfPathLabel.setText("File loaded: " + filePath);
                pdfPathLabel.setVisible(true);

                // Arrêter l'indicateur de chargement
                loadingIndicator.stop();
                sendButton.setDisable(false);
            }));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            System.out.println("No file selected.");
        }
    }
    private void adjustLayout(double width, double height) {
        if (width < 400) {
            pdfPathLabel.setFont(Font.font("Arial", 10));
        } else {
            pdfPathLabel.setFont(Font.font("Arial", 14));
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}