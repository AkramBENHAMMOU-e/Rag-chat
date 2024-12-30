package com.enset.test;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoadingIndicator extends StackPane {
    private final ProgressBar progressBar;
    private final Label statusLabel;
    private final ProgressIndicator spinner;
    private final VBox container;

    public LoadingIndicator() {
        // Create container
        container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(15));
        container.setMaxWidth(400);
        container.setMaxHeight(270);
        container.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
        );

        // Create spinner
        spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);
        spinner.setStyle(
                "-fx-progress-color: #2196F3;" +
                        "-fx-min-width: 50px;" +
                        "-fx-min-height: 50px;"
        );

        // Create progress bar
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        progressBar.setStyle(
                "-fx-accent: #2196F3;" +
                        "-fx-control-inner-background: #E3F2FD;"
        );

        // Create status label
        statusLabel = new Label("Loading PDF...");
        statusLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #424242;" +
                        "-fx-font-weight: bold;"
        );

        // Add all elements to container
        container.getChildren().addAll(spinner, statusLabel, progressBar);
        getChildren().add(container);

        // Initial setup
        setMouseTransparent(true);
        setVisible(false);
    }

    public void start(String message) {
        statusLabel.setText(message);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        setVisible(true);

        // Animation d'entrée
        container.setScaleX(0.7);
        container.setScaleY(0.7);
        container.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), container);
        scaleIn.setFromX(0.7);
        scaleIn.setFromY(0.7);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        ParallelTransition parallelIn = new ParallelTransition(fadeIn, scaleIn);
        parallelIn.play();
    }

    public void stop() {
        // Animation de sortie
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), container);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), container);
        scaleOut.setFromX(1);
        scaleOut.setFromY(1);
        scaleOut.setToX(0.7);
        scaleOut.setToY(0.7);

        ParallelTransition parallelOut = new ParallelTransition(fadeOut, scaleOut);
        parallelOut.setOnFinished(event -> setVisible(false));
        parallelOut.play();
    }
}
