package com.enset.test;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.io.IOException;
import java.util.Optional;

public class SimpleChatInterface extends Application {

    private Label pdfPathLabel;
    private Button sendButton;
    TextArea messageArea;
    private Mainn mainn = new Mainn();
    private LoadingIndicator loadingIndicator;
    private ScrollPane scrollPane;


    @Override
    public void start(Stage primaryStage) {
        loadingIndicator = new LoadingIndicator();

        // Create header
        HBox header = createHeader();
        header.setBorder(null);

        // Create chat area
        VBox chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));

        // Configure ScrollPane with rounded corners
        scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        scrollPane.setStyle(
                "-fx-background: white;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;" +
                        // Style de la barre de défilement
                        "-fx-control-inner-background: white;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;"
        );
        scrollPane.getStyleClass().add("edge-to-edge");

        // Create message input area with buttons
        HBox messageInputContainer = createMessageInputContainer(chatBox);
        messageInputContainer.setBorder(null);
        messageInputContainer.setBackground(new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(10),
                Insets.EMPTY
        )));

        // PDF path label
        pdfPathLabel = new Label();
        pdfPathLabel.setFont(Font.font("Arial", 14));
        pdfPathLabel.setPadding(new Insets(10));
        pdfPathLabel.setBackground(new Background(new BackgroundFill(
                Color.WHITE,  // Couleur plus claire
                new CornerRadii(5),
                Insets.EMPTY
        )));
        pdfPathLabel.setTextFill(Color.BLACK);
        pdfPathLabel.setWrapText(true);
        pdfPathLabel.setVisible(false);

        // Create main container
        VBox mainContainer = new VBox();
        mainContainer.setBorder(null);
        mainContainer.getChildren().add(header);

        // Create content container
        VBox contentContainer = new VBox(20);
        contentContainer.setBorder(null);
        contentContainer.setPadding(new Insets(30));
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setBackground(new Background(new BackgroundFill(
                Color.web("#0a4b83"),
                new CornerRadii(0),  // Coins plus arrondis pour le conteneur principal
                Insets.EMPTY
        )));

        // Ajout d'un effet d'ombre pour le messageInputContainer
        DropShadow inputShadow = new DropShadow();
        inputShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        inputShadow.setRadius(10);
        inputShadow.setOffsetY(2);
        messageInputContainer.setEffect(inputShadow);

        // Ajout d'un effet d'ombre pour le scrollPane
        DropShadow scrollShadow = new DropShadow();
        scrollShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        scrollShadow.setRadius(10);
        scrollShadow.setOffsetY(2);
        scrollPane.setEffect(scrollShadow);

        // Add scrollPane, messageInputContainer and pdfPathLabel to contentContainer
        contentContainer.getChildren().addAll(scrollPane, messageInputContainer, pdfPathLabel);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(messageInputContainer, Priority.NEVER);
        VBox.setVgrow(pdfPathLabel, Priority.NEVER);

        // Add some margin to the components
        VBox.setMargin(scrollPane, new Insets(0, 0, 10, 0));
        VBox.setMargin(messageInputContainer, new Insets(0, 0, 10, 0));
        VBox.setMargin(pdfPathLabel, new Insets(0, 0, 0, 0));

        mainContainer.getChildren().add(contentContainer);
        VBox.setVgrow(contentContainer, Priority.ALWAYS);

        // Root pane with overlay capability
        StackPane rootPane = new StackPane();
        rootPane.getChildren().addAll(mainContainer, loadingIndicator);
        Image icon = new Image(getClass().getResourceAsStream("/ENSET-Mohammedia2.png"));
        // Set up the scene
        Scene scene = new Scene(rootPane);
        scene.widthProperty().addListener((observable, oldValue, newValue) ->
                adjustLayout(newValue.doubleValue(), scene.getHeight()));
        scene.heightProperty().addListener((observable, oldValue, newValue) ->
                adjustLayout(scene.getWidth(), newValue.doubleValue()));

        primaryStage.setTitle("ENSET GUIDE");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(790);
        primaryStage.show();
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
        // Message input area (reste inchangé)
        messageArea = new TextArea();
        messageArea.setBorder(null);
        messageArea.setPromptText("Enter your message...");
        messageArea.setPrefRowCount(1);
        messageArea.setFont(Font.font("Arial", 14));
        messageArea.setWrapText(true);
        messageArea.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        messageArea.setPadding(new Insets(10));

        // Création des boutons avec effets de survol
        sendButton = createStyledButton(">", Color.web("#0a4b83"), "Send message");
        Button loadPdfButton = createStyledButton("+", Color.DARKGRAY, "Load PDF file");
        Button clearButton = createStyledButton("🗑", Color.RED, "Delete history");

        // Set button actions
        sendButton.setOnAction(event -> sendPrompt(messageArea, chatBox, sendButton));
        loadPdfButton.setOnAction(event -> loadAndDisplayPDFPath(chatBox));
        clearButton.setOnAction(event -> handleClearAction(chatBox));

        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                sendPrompt(messageArea, chatBox, sendButton);
            }
        });

        // Create button container avec le nouvel ordre des boutons
        HBox buttonContainer = new HBox(5);
        buttonContainer.getChildren().addAll(sendButton, loadPdfButton, clearButton); // Nouvel ordre
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(0, 0, 0, 5));

        // Create main container for message input area
        HBox messageInputContainer = new HBox(10);
        messageInputContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        messageInputContainer.setPadding(new Insets(10));
        messageInputContainer.setBorder(null);
        messageInputContainer.getChildren().addAll(messageArea, buttonContainer);
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

    private void handleClearAction(VBox chatBox) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Clear Database");
        alert.setContentText("Are you sure you want to clear all data?");

        // Appliquer le style directement
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20px;" +
                        "-fx-font-family: 'System';"
        );

        // Style des boutons
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
            Mainn.clearDatabase();
            chatBox.getChildren().clear();
            messageArea.clear();
            pdfPathLabel.setVisible(false);
            HBox confirmationBox = createMessageBox("Database and chat history cleared.", Color.GREENYELLOW, Pos.CENTER);
            chatBox.getChildren().add(confirmationBox);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), confirmationBox);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> chatBox.getChildren().remove(confirmationBox));
            fadeOut.play();

        }
    }

    private void sendPrompt(TextArea messageArea, VBox chatBox, Button sendButton) {
        String userMessage = messageArea.getText();
        if (!userMessage.isEmpty()) {
            sendButton.setDisable(true);

            // Add user message
            HBox userMessageBox = createMessageBox(userMessage, Color.LIGHTGRAY, Pos.CENTER_RIGHT);
            chatBox.getChildren().add(userMessageBox);
            scrollToBottom(chatBox);
            messageArea.clear();

            // Create loading message with dots animation
            HBox loadingBox = createMessageBox("", Color.LIGHTGREY, Pos.CENTER_LEFT);
            Text loadingText = new Text(".");
            ((TextFlow)loadingBox.getChildren().get(0)).getChildren().setAll(loadingText);
            chatBox.getChildren().add(loadingBox);
            scrollToBottom(chatBox);

            // Dots animation
            Timeline dotsTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e -> loadingText.setText("..")),
                    new KeyFrame(Duration.seconds(1.0), e -> loadingText.setText("....")),
                    new KeyFrame(Duration.seconds(1.5), e -> loadingText.setText("......"))
            );
            dotsTimeline.setCycleCount(Timeline.INDEFINITE);
            dotsTimeline.play();

            // Get bot response in background
            Thread responseThread = new Thread(() -> {
                String botResponse = getBotResponse(userMessage);
                javafx.application.Platform.runLater(() -> {
                    // Stop dots animation
                    dotsTimeline.stop();
                    chatBox.getChildren().remove(loadingBox);

                    // Create bot message box
                    HBox botMessageBox = createMessageBox("", Color.LIGHTGREY, Pos.CENTER_LEFT);
                    Text responseText = new Text("");
                    ((TextFlow)botMessageBox.getChildren().get(0)).getChildren().setAll(responseText);
                    chatBox.getChildren().add(botMessageBox);

                    // Typing animation for response
                    Timeline typingTimeline = new Timeline();
                    final int[] charIndex = {0};

                    KeyFrame[] frames = new KeyFrame[botResponse.length()];
                    for (int i = 0; i < botResponse.length(); i++) {
                        final int index = i;
                        frames[i] = new KeyFrame(Duration.millis(30 * i), e -> {
                            responseText.setText(botResponse.substring(0, index + 1));
                            scrollToBottom(chatBox);
                        });
                    }

                    typingTimeline.getKeyFrames().addAll(frames);
                    typingTimeline.setOnFinished(e -> sendButton.setDisable(false));
                    typingTimeline.play();
                });
            });
            responseThread.start();
        } else {
            System.out.println("No message to send.");
        }
    }


    private void scrollToBottom(VBox chatBox) {
        Timeline scrollTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            chatBox.layout();
            scrollPane.setVvalue(1.0);
        }));
        scrollTimeline.play();
    }


    private String getBotResponse(String userMessage) {
        String response = mainn.askQuestion(userMessage);
        System.out.println("result : " + response);
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
        fileChooser.setTitle("Load PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            sendButton.setDisable(true);
            String filePath = file.getAbsolutePath();

            // Démarrer l'indicateur de chargement
            loadingIndicator.start("Processing PDF file...");

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                try {
                    mainn.uploadUI(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pdfPathLabel.setText("PDF loaded: " + filePath);
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