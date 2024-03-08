package com.comiftouch.jeasyfinance.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class StageManager {
    private String fxmlPaths = "/view/";
    private final HashMap<String, Scene> scenes = new HashMap<>();
    private final Stage stage;

    private static StageManager stageManager;

    private Alert alertInformation, alertWarning, alertError, alertConfirmation;
    private final FileChooser fileChooser = new FileChooser();
    private final Label labelPopup = new Label();
    private final Popup miniPopup = new Popup();
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), evt -> miniPopup.hide()));

    public StageManager(Stage stage, String fxmlPaths) {
        this.stage = stage;
        this.fxmlPaths = fxmlPaths;
        init();
    }

    public StageManager(Stage stage) {
        this.stage = stage;
        init();
    }

    public static void initInstanceSingleton(Stage stage, String fxmlPaths) {
        if (stageManager == null) {
            stageManager = new StageManager(stage, fxmlPaths);
        }
    }

    public static void initInstanceSingleton(Stage stage) {
        if (stageManager == null) {
            stageManager = new StageManager(stage);
        }
    }

    private void init() {
        labelPopup.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff; -fx-font-size: 20px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-opacity: 0.6;");
        miniPopup.getContent().add(labelPopup);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
    }

    public static StageManager getInstanceSingleton() {
        return stageManager;
    }

    public static void destroyInstance() {
        stageManager = null;
    }

    private Scene loadFxml(String fxml) {
        String path = fxmlPaths + fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));

        try {
            Parent parent = fxmlLoader.load();
            return new Scene(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScene(String sceneName) {
        Scene scene = scenes.get(sceneName);

        if (scene == null) {
            scene = loadFxml(sceneName);
        }

        scenes.put(sceneName, scene);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public Scene getScene(String sceneName) {
        Scene scene = scenes.get(sceneName);

        if (scene == null) {
            scene = loadFxml(sceneName);
        }

        return scene;
    }

    public void setIcon(String path) {
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource(path)).openStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAlertInformation(String header, String content){
        if (alertInformation == null) {
            alertInformation = new Alert(Alert.AlertType.INFORMATION);
        }

        alertInformation.setTitle("Información!");
        alertInformation.setHeaderText(header);
        alertInformation.setContentText(content);

        alertInformation.showAndWait();
    }

    public void showAlertWarning(String header, String content) {
        if (alertWarning == null) {
            alertWarning = new Alert(Alert.AlertType.WARNING);
        }

        alertWarning.setTitle("Advertencia!");
        alertWarning.setHeaderText(header);
        alertWarning.setContentText(content);

        alertWarning.showAndWait();
    }

    public void showAlertError(String header, String content) {
        if (alertError == null) {
            alertError = new Alert(Alert.AlertType.ERROR);
        }

        alertError.setTitle("Error!");
        alertError.setHeaderText(header);
        alertError.setContentText(content);

        alertError.showAndWait();
    }

    public boolean showAlertConfirmation(String header, String content) {
        if (alertConfirmation == null) {
            alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        }

        alertConfirmation.setTitle("Confirmar!");
        alertConfirmation.setHeaderText(header);
        alertConfirmation.setContentText(content);

        return alertConfirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public void showToast(String text) {
        labelPopup.setText(text);

        timeline.play();

        miniPopup.show(getCurrentScene().getWindow());
    }

    public void showStage() {
        stage.show();
    }

    public void hideStage() {
        stage.hide();
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public void setResizable(boolean value) {
        stage.setResizable(value);
    }

    public void removeScene(String sceneName) {
        scenes.remove(sceneName);
    }

    public Scene getCurrentScene() {
        return stage.getScene();
    }

    public Stage getStage() {
        return stage;
    }

    public String getFxmlPaths() {
        return fxmlPaths;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }
}

