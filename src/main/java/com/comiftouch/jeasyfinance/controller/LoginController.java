package com.comiftouch.jeasyfinance.controller;

import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Call;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button button;
    @FXML
    private Label label;

    private StageManager stageManager;
    private Session session;

    ConfigController configController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DependencyContainer dependencyContainer = DependencyContainer.getInstance();
        stageManager = dependencyContainer.getDependency(StageManager.class);
        session = Session.getInstance();
        DependencyContainer.getInstance().registerDependency(session);

        Stage dialogStage = new Stage();

        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stageManager.getStage());

        FXMLManager fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        dialogStage.setScene(new Scene(fxmlManager.getParent("login/config")));

        configController = fxmlManager.getController("login/config");
        configController.injectDependency(stageManager, dependencyContainer.getDependency(PropertiesManager.class), session);
        configController.injectDialogStage(dialogStage);
    }

    public void access() {
        if (session.isProcessing()) {
            return;
        }

        String user = userField.getText();
        String password = passwordField.getText();

        if (user.isEmpty() || password.isEmpty()) {
            label.setTextFill(Color.RED);
            label.setText("Llene los campos.");
            return;
        }

        button.setCursor(Cursor.WAIT);

        session.login(user, password, booleanCallBack);
    }

    private final BooleanCallBack booleanCallBack = new BooleanCallBack() {
        @Override
        public void onSusses(int code) {
            Platform.runLater(() -> {
                stageManager.setResizable(true);
                stageManager.setTitle("Menú Principal");
                stageManager.setScene("main");
                stageManager.getStage().setMaximized(false);
                stageManager.getStage().setMaximized(true);
                stageManager.removeScene("login");
            });
        }

        @Override
        public void onError(int code, String message) {
            Platform.runLater(() -> {
                switch (code) {
                    case 409 -> StageManager.getInstanceSingleton().showAlertWarning(
                            "Sesión activa", "Ya existe una sesión activa, ciérrela primero.");
                    case 400 -> stageManager.showAlertError("Datos Incompletos.", message);

                    case 401 -> Platform.runLater(() -> {
                        label.setTextFill(Color.RED);
                        label.setText(message);

                    });
                    case 500 -> StageManager.getInstanceSingleton().showAlertError("Error del Servidor", message);
                }
            });
        }

        @Override
        public void onFailed(Call call, IOException e) {
            Platform.runLater(() -> {
                stageManager.showAlertError("No se pudo establecer la conexión con el Servidor.", e.getMessage());
                button.setCursor(Cursor.HAND);
            });
        }
    };

    public void showConfig(ActionEvent actionEvent) {
        configController.showAndWait();
    }
}
