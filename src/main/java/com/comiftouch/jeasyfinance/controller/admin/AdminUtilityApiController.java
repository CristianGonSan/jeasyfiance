package com.comiftouch.jeasyfinance.controller.admin;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.UserSystem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Clase ClientUtilityApiController que extiende BasicControllerTool e implementa la interfaz Initializable.
 * Esta clase proporciona utilidades y requiere una inicialización adecuada antes de su uso.
 * Antes de llamar a ciertos métodos, asegúrese de haber inicializado las propiedades necesarias
 * utilizando los métodos correspondientes, como init e injectDependencies.
 */
public class AdminUtilityApiController extends UtilityHandler<UserSystem> implements Initializable {
    @FXML
    private TextField text1, text2, text3, text4, text5, text6;
    @FXML
    private PasswordField password1, password2;
    @FXML
    private Slider slider;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tab1, tab2;

    private TextField[] textFields = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFields = new TextField[]{text1, text2, text3, text4, text5, text6};

        text1.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        text4.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!Character.isDigit(event.getCharacter().charAt(0))) {
                event.consume();
            }
        });

        text5.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });
    }

    public void setStatus(Task task) {
        this.task = task;
        clear();

        switch (task) {
            case INSERTING -> {
                tab1.setDisable(false);
                tab2.setDisable(false);
                tabPane.getSelectionModel().select(0);
            }
            case EDITING -> {
                tab1.setDisable(false);
                tab2.setDisable(true);
                tabPane.getSelectionModel().select(0);
            }
            case CHANCING -> {
                tab1.setDisable(true);
                tab2.setDisable(false);
                tabPane.getSelectionModel().select(1);
            }
        }
    }

    public void insertObjet(UserSystem objet) {
        lastObjet = objet;
        setObjet(objet);
    }

    public void clear() {
        for (TextField textField : textFields) {
            textField.clear();
        }
        password1.clear();
        password2.clear();

        slider.setValue(1);
    }

    private UserSystem getObjet() {
        UserSystem objet = new UserSystem();

        objet.setUsername(text1.getText());
        objet.setFirstName(text2.getText());
        objet.setLastName(text3.getText());
        objet.setPhoneNumber(text4.getText());
        objet.setEmail(text5.getText());
        objet.setRegion(text6.getText());
        objet.setPassword(password1.getText());
        objet.setLevel((int) slider.getValue());

        return objet;
    }

    private void setObjet(@NotNull UserSystem objet) {
        text1.setText(objet.getUsername());
        text2.setText(objet.getFirstName());
        text3.setText(objet.getLastName());
        text4.setText(objet.getPhoneNumber());
        text5.setText(objet.getEmail());
        text6.setText(objet.getRegion());
        slider.setValue(objet.getLevel());
    }

    public void confirm() {
        if (checkProcessing()) return;

        switch (task) {
            case INSERTING -> {
                if (!validateFields(text1, text2, text6)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llene los campos obligatorios.");
                    return;
                }

                String password = password1.getText();

                if (password.length() < 8) {
                    stageManager.showAlertWarning("Contraseña Invalida", "La contraseña debe tener al menos 8 caracteres.");
                    return;
                }

                if (!password.equals(password2.getText())) {
                    stageManager.showAlertWarning("Contraseña Invalida", "Las contraseñas no Coinciden.");
                    return;
                }

                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "admin")
                        .add("operation", "create")
                        .add("user_system", getObjet());

                apiManager.makeRequest(apiData, UserSystem.class, new CollectionCallBack<>() {
                    @Override
                    public void onSusses(int code, String message, ArrayList<UserSystem> collection) {
                        Platform.runLater(() -> {
                            lastObjet = collection.get(0);
                            stageManager.showToast("Registro creado");

                            task = Task.NOTHING;
                            dialogStage.close();
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        error(code, message);
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        failure(e);
                    }
                });
            }

            case EDITING -> {
                if (!validateFields(text1, text2, text6)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llené los campos obligatorios.");
                    return;
                }

                UserSystem updateObjet = new UserSystem();
                lastObjet.transfer(updateObjet);
                getObjet().transfer(updateObjet);

                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "admin")
                        .add("operation", "update")
                        .add("user_system", updateObjet);

                apiManager.makeRequest(apiData, new BooleanCallBack() {
                    @Override
                    public void onSusses(int code) {
                        Platform.runLater(() -> {
                            lastObjet = updateObjet;
                            stageManager.showToast("Registro actualizado");

                            task = Task.NOTHING;
                            dialogStage.close();
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        error(code, message);
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        failure(e);
                    }
                });
            }

            case CHANCING -> {
                String password = password1.getText();

                if (password.length() < 8) {
                    stageManager.showAlertWarning("Contraseña Invalida", "La contraseña debe tener al menos 8 caracteres.");
                    return;
                }

                if (!password.equals(password2.getText())) {
                    stageManager.showAlertWarning("Contraseña Invalida", "Las contraseñas no Coinciden.");
                    return;
                }

                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "admin")
                        .add("operation", "update_password")
                        .add("user_id", lastObjet.getUserId())
                        .add("password", password);

                apiManager.makeRequest(apiData, new BooleanCallBack() {
                    @Override
                    public void onSusses(int code) {
                        Platform.runLater(() -> {
                            stageManager.showToast("Contraseña actualizada");
                            task = Task.NOTHING;
                            dialogStage.close();
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        error(code, message);
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        failure(e);
                    }
                });
            }
        }
    }

    public void cancel() {
        if (checkProcessing()) return;
        lastObjet = null;
        dialogStage.close();
    }
}
