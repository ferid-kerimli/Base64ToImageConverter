package com.example.base64toimageconverter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class Base64ToImageConverter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        VBox vbox = new VBox(10);

        // Label for instructions
        Label lblInstructions = new Label("  Enter Base64 string here...");

        // TextArea for Base64 input
        TextArea txtBase64Input = new TextArea();
        txtBase64Input.setWrapText(true); // Ensures long base64 strings wrap to the next line

        Button btnConvert = new Button("Convert");
        Button btnClear = new Button("Clear");
        Text errorMessage = new Text();

        // Handle Convert button click
        btnConvert.setOnAction(e -> {
            String base64String = txtBase64Input.getText().trim();
            if (base64String.isEmpty()) {
                errorMessage.setFill(Color.RED);
                errorMessage.setText("Please enter a Base64 string.");
            } else {
                try {
                    Image image = base64ToImage(base64String);
                    showImageWindow(image, primaryStage);
                } catch (IllegalArgumentException ex) {
                    errorMessage.setFill(Color.RED);
                    errorMessage.setText("Invalid Base64 string.");
                }
            }
        });

        // Handle Clear button click
        btnClear.setOnAction(e -> {
            txtBase64Input.clear();
            errorMessage.setText("");
        });

        // Add all components to the VBox
        vbox.getChildren().addAll(lblInstructions, txtBase64Input, btnConvert, btnClear, errorMessage);

        Scene scene = new Scene(vbox, 450, 250);  // Adjusted height for better visibility
        primaryStage.setTitle("Base64 to Image Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showImageWindow(Image image, Stage primaryStage) {
        // New window to display the image
        Stage imageWindow = new Stage();

        // ImageView for displaying the image
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);  // Ensure the image maintains its aspect ratio
        imageView.setSmooth(true);         // Improve rendering quality
        imageView.setCache(true);          // Improve performance

        // Save button
        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> saveImage(image));

        // Create a BorderPane to hold the ImageView and Save button
        BorderPane borderPane = new BorderPane();

        // Set the ImageView in the center of the BorderPane
        borderPane.setCenter(imageView);

        // Set the Save button at the bottom of the BorderPane
        borderPane.setBottom(btnSave);

        // Ensure the button stays at the bottom with some spacing
        BorderPane.setAlignment(btnSave, javafx.geometry.Pos.CENTER);
        BorderPane.setMargin(btnSave, new javafx.geometry.Insets(10));

        // Ensure the image resizes with the window and adjusts for button space
        imageWindow.widthProperty().addListener((observable, oldValue, newValue) -> {
            imageView.setFitWidth(newValue.doubleValue());  // Resize image width
        });

        imageWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
            // Ensure the image height leaves room for the button at the bottom
            double availableHeight = newValue.doubleValue() - 70; // Account for button and margin
            if (availableHeight > 0) {
                imageView.setFitHeight(availableHeight); // Set the new image height
            }
        });

        // Set the scene with the BorderPane layout
        Scene imageScene = new Scene(borderPane, 800, 600); // Initial window size
        imageWindow.setTitle("Converted Image");
        imageWindow.setScene(imageScene);

        // Display the window
        imageWindow.show();

        // Hide the main window
        primaryStage.hide();

        // On close, show the main window again
        imageWindow.setOnCloseRequest(event -> primaryStage.show());
    }

    private Image base64ToImage(String base64String) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            return new Image(new ByteArrayInputStream(imageBytes));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 encoding", e);
        }
    }

    private void saveImage(Image image) {
        // Open file chooser for saving image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                // Convert JavaFX Image to BufferedImage
                BufferedImage bufferedImage = javafxToBufferedImage(image);

                // Save image in selected format
                String format = file.getName().endsWith(".png") ? "PNG" : "JPEG";
                ImageIO.write(bufferedImage, format, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage javafxToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                javafx.scene.paint.Color color = image.getPixelReader().getColor(x, y);
                bufferedImage.setRGB(x, y, color.hashCode());
            }
        }
        return bufferedImage;
    }
}
