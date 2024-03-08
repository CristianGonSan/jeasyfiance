package com.comiftouch.jeasyfinance.controller.employee;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Employee;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EmployeeUtilityController extends UtilityHandler<Employee> implements Initializable {
    @FXML
    private TextField text1, text2, text3, text4, text5, text6;
    private TextField[] textFields = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFields = new TextField[]{text1, text2, text3, text4, text5, text6};

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
    }

    public void insertObjet(Employee objet) {
        lastObjet = objet;
        setObjet(objet);
    }

    /**
     * Muestra el diálogo y espera hasta que el usuario interactúe con él.
     * Antes de llamar a este método, asegúrese de haber inicializado las propiedades
     * 'dialogStage', 'api', y 'stageManager' utilizando los métodos correspondientes.
     * @return El objeto UserSystem resultante de la interacción del usuario.
     * @throws IllegalStateException Si alguna de las propiedades críticas no ha sido inicializada.
     */
    public Employee showAndWait() {
        if (dialogStage == null) {
            throw new IllegalStateException("El diálogo no puede mostrarse porque 'dialogStage' no ha sido inicializado.");
        }
        if (apiManager == null) {
            throw new IllegalStateException("El diálogo no puede mostrarse porque 'api' no ha sido inicializado.");
        }
        if (stageManager == null) {
            throw new IllegalStateException("El diálogo no puede mostrarse porque 'stageManager' no ha sido inicializado.");
        }

        dialogStage.showAndWait();
        return lastObjet;
    }

    public void clear() {
        for (TextField textField : textFields) {
            textField.clear();
        }
    }

    private Employee getObjet() {
        Employee objet = new Employee();

        objet.setClue(text1.getText());
        objet.setFirstName(text2.getText());
        objet.setLastName(text3.getText());
        objet.setPhoneNumber(text4.getText());
        objet.setEmail(text5.getText());
        objet.setPosition(text6.getText());

        return objet;
    }

    private void setObjet(@NotNull Employee objet) {
        text1.setText(objet.getClue());
        text2.setText(objet.getFirstName());
        text3.setText(objet.getLastName());
        text4.setText(objet.getPhoneNumber());
        text5.setText(objet.getEmail());
        text6.setText(objet.getPosition());

    }

    public void confirm() {
        if (checkProcessing()) return;

        switch (task) {
            case INSERTING -> {
                if (!validateFields(text2, text3)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llene los campos obligatorios.");
                    return;
                }

                APIManager.APIData apiData = new APIManager.APIData(true);
                apiData.add("group", "employee")
                        .add("operation", "create")
                        .add("employee", getObjet());

                apiManager.makeRequest(apiData, Employee.class, new CollectionCallBack<>() {
                    @Override
                    public void onSusses(int code, String message, ArrayList<Employee> collection) {
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
                if (!validateFields(text2, text3)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llené los campos obligatorios.");
                    return;
                }

                Employee updateObjet = new Employee();
                lastObjet.transfer(updateObjet);
                getObjet().transfer(updateObjet);

                APIManager.APIData apiData = new APIManager.APIData(true);
                apiData.add("group", "employee")
                        .add("operation", "update")
                        .add("employee", updateObjet);

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
        }
    }

    public void cancel() {
        if (checkProcessing()) return;
        lastObjet = null;
        dialogStage.close();
    }
}
