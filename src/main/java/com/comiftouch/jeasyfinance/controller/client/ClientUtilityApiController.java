package com.comiftouch.jeasyfinance.controller.client;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientUtilityApiController extends UtilityHandler<Client> implements Initializable {
    @FXML
    private TextField text1, text2, text3, text4, text5, text6, text7;
    @FXML
    private TextArea textArea;
    @FXML
    private DatePicker date;
    @FXML
    private RadioButton sexM, sexF;

    private TextField[] textFields;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFields = new TextField[]{text1, text2, text3, text4, text5, text6};

        text6.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        text5.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!Character.isDigit(event.getCharacter().charAt(0))) {
                event.consume();
            }
        });
    }

    @Override
    public void setStatus(Task task) {
        this.task = task;
        clear();
    }

    public void insertObjet(Client objet) {
        lastObjet = objet;
        setObjet(objet);
    }

    public void clear() {
        for (TextField textField : textFields) {
            textField.clear();
        }
        date.setValue(LocalDate.now());
        sexM.setSelected(true);
    }

    private Client getObjet() {
        Client objet = new Client();

        objet.setFirstName(text1.getText());
        objet.setLastName(text2.getText());
        objet.setNationality(text3.getText());
        objet.setSex(sexM.isSelected() ? 'M' : 'F');
        objet.setBirthDate(date.getValue().format(formatter));
        objet.setCurp(text4.getText());
        objet.setDomicile(textArea.getText());
        objet.setPhoneNumber(text5.getText());
        objet.setEmail(text6.getText());
        objet.setCivilStatus(text7.getText());

        return objet;
    }

    private void setObjet(@NotNull Client objet) {
        text1.setText(objet.getFirstName());
        text2.setText(objet.getLastName());
        text3.setText(objet.getNationality());

        if (objet.getSex() == 'M') {
            sexM.setSelected(true);
        } else {
            sexF.setSelected(true);
        }

        date.setValue(LocalDate.parse(objet.getBirthDate()));

        text4.setText(objet.getCurp());
        textArea.setText(objet.getDomicile());
        text5.setText(objet.getPhoneNumber());
        text6.setText(objet.getEmail());
        text7.setText(objet.getCivilStatus());
    }

    public void confirm() {
        if (checkProcessing()) return;

        switch (task) {
            case INSERTING -> {
                if (!validateFields(text1, text2)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llene los campos obligatorios.");
                    return;
                }

                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "client")
                        .add("operation", "create")
                        .add("client", getObjet());

                apiManager.makeRequest(apiData, Client.class, new CollectionCallBack<>() {
                    @Override
                    public void onSusses(int code, String message, ArrayList<Client> collection) {
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
                if (!validateFields(text1, text2)) {
                    stageManager.showAlertWarning("Campos vacíos.", "Por favor, llené los campos obligatorios.");
                    return;
                }

                Client updateObjet = new Client();
                lastObjet.transfer(updateObjet);
                getObjet().transfer(updateObjet);

                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "client")
                        .add("operation", "update")
                        .add("client", updateObjet);

                apiManager.makeRequest(apiData, new BooleanCallBack() {
                    @Override
                    public void onSusses(int code) {
                        Platform.runLater(() -> {
                            lastObjet = updateObjet;

                            task = Task.NOTHING;

                            stageManager.showToast("Registro actualizado");
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
