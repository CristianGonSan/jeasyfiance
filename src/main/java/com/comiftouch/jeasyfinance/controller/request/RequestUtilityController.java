package com.comiftouch.jeasyfinance.controller.request;

import com.comiftouch.jeasyfinance.controller.*;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Client;
import com.comiftouch.jeasyfinance.model.api.dataclass.Employee;
import com.comiftouch.jeasyfinance.model.api.dataclass.LoanRequest;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RequestUtilityController extends UtilityHandler<LoanRequest> implements Initializable {
    @FXML
    private TextField text1, text2, text3, text4, text5, text6, text7, urlField;
    @FXML
    private TextArea commentField;
    @FXML
    private DatePicker date;

    private TextField[] textFields = null;

    private SearchClientController searchClientUtility;
    private SearchEmployeeController searchEmployeeUtility;

    private int clientId, avalId, executiveId, supervisorId;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFields = new TextField[]{text1, text2, text3, text4, text5, text6};

        text5.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String s = text5.getText();
            char c = event.getCharacter().charAt(0);
            if (c == '.') {
                if (s.contains(".")) {
                    event.consume();
                }
            } else if (!Character.isDigit(c)) {
                event.consume();
            }
        });

        text6.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String s = text6.getText();
            char c = event.getCharacter().charAt(0);
            if (c == '.') {
                if (s.contains(".")) {
                    event.consume();
                }
            } else if (!Character.isDigit(c)) {
                event.consume();
            }
        });

        text6.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char c = event.getCharacter().charAt(0);
            if (!Character.isDigit(c)) {
                event.consume();
            }
        });

        text5.textProperty().addListener((observable, oldValue, newValue) -> text7.setText(String.valueOf(getTotal())));
        text6.textProperty().addListener((observable, oldValue, newValue) -> text7.setText(String.valueOf(getTotal())));
    }

    @Override
    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        super.injectDependency(stageManager, propertiesManager, session);

        Stage dialogStage1 = new Stage();

        dialogStage1.initStyle(StageStyle.UTILITY);
        dialogStage1.initModality(Modality.APPLICATION_MODAL);
        dialogStage1.initOwner(stageManager.getStage());

        FXMLManager fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        dialogStage1.setScene(new Scene(fxmlManager.getParent("request/search-client")));

        searchClientUtility = fxmlManager.getController("request/search-client");
        searchClientUtility.injectDependency(stageManager, propertiesManager, session);
        searchClientUtility.injectDialogStage(dialogStage1);

        Stage dialogStage2 = new Stage();

        dialogStage2.initStyle(StageStyle.UTILITY);
        dialogStage2.initModality(Modality.APPLICATION_MODAL);
        dialogStage2.initOwner(stageManager.getStage());

        dialogStage2.setScene(new Scene(fxmlManager.getParent("request/search-employee")));

        searchEmployeeUtility = fxmlManager.getController("request/search-employee");
        searchEmployeeUtility.injectDependency(stageManager, propertiesManager, session);
        searchEmployeeUtility.injectDialogStage(dialogStage2);
    }

    public void setStatus(Task task) {
        this.task = task;

        clear();
    }

    public void insertObjet(LoanRequest objet) {
        lastObjet = objet;
        setObjet(objet);
    }

    public void clear() {
        for (TextField textField : textFields) {
            textField.clear();
        }
        text5.setText("0.0");
        text6.setText("0.0");
        text7.setText("0.0");
        urlField.clear();
        commentField.clear();
        date.setValue(LocalDate.now());
        clientId = 0;
        avalId = 0;
        executiveId = 0;
        supervisorId = 0;
    }

    private LoanRequest getObjet() {
        LoanRequest objet = new LoanRequest();

        objet.setClientId(clientId);
        objet.setClientFullName(text1.getText());

        objet.setAvalId(avalId);
        objet.setAvalFullName(text2.getText());

        objet.setExecutiveId(executiveId);
        objet.setExecutiveFullName(text3.getText());

        objet.setSupervisorId(supervisorId);
        objet.setSupervisorFullName(text4.getText());

        objet.setAmount(getAmount());
        objet.setInterest(Float.parseFloat(text6.getText()));
        objet.setRequestDate(date.getValue().format(formatter));
        objet.setReferenceUrl(urlField.getText());
        objet.setCommentsUrl(commentField.getText());

        return objet;
    }

    private void setObjet(@NotNull LoanRequest objet) {
        clientId = objet.getClientId();
        text1.setText(objet.getClientFullName());

        avalId = objet.getAvalId();
        text2.setText(objet.getAvalFullName());

        executiveId = objet.getExecutiveId();
        text3.setText(objet.getExecutiveFullName());

        supervisorId = objet.getSupervisorId();
        text4.setText(objet.getSupervisorFullName());

        text5.setText(String.valueOf(objet.getAmount()));
        text6.setText(String.valueOf(objet.getInterest()));

        String referenceUrl = objet.getReferenceUrl();

        if (referenceUrl != null && !referenceUrl.isEmpty()) {
            urlField.setText(referenceUrl);
        } else {
            urlField.clear();
        }

        commentField.setText(objet.getCommentsUrl());
    }

    private float getAmount() {
        return text5.getText().isEmpty() ? 0 : Float.parseFloat(text5.getText());
    }

    private float getInterest() {
        return text6.getText().isEmpty() ? 0 : Float.parseFloat(text6.getText()) / 100;
    }

    private float getTotal() {
        float amount = getAmount();
        return amount + (amount * getInterest());
    }

    public void confirm() {
        if (checkProcessing()) return;

        if (clientId < 1) {
            stageManager.showAlertWarning("Datos incompletos", "Necesita seleccionar un Cliente");
            return;
        }

        switch (task) {
            case INSERTING -> {
                APIManager.APIData apiData = new APIManager.APIData(true);
                apiData.add("group", "request")
                        .add("operation", "create")
                        .add("request", getObjet());

                apiManager.makeRequest(apiData, LoanRequest.class, new CollectionCallBack<>() {
                    @Override
                    public void onSusses(int code, String message, ArrayList<LoanRequest> collection) {
                        Platform.runLater(() -> {
                            lastObjet = collection.get(0);
                            stageManager.showToast("Registro creado");
                            task = Task.NOTHING;
                            dialogStage.close();

                            System.gc();
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
                LoanRequest updateObjet = new LoanRequest();
                lastObjet.transfer(updateObjet);
                getObjet().transfer(updateObjet);

                APIManager.APIData apiData = new APIManager.APIData(true);
                apiData.add("group", "request")
                        .add("operation", "update")
                        .add("request", updateObjet);

                apiManager.makeRequest(apiData, new BooleanCallBack() {
                    @Override
                    public void onSusses(int code) {
                        Platform.runLater(() -> {
                            lastObjet = updateObjet;
                            stageManager.showToast("Registro actualizado");
                            task = Task.NOTHING;
                            dialogStage.close();

                            System.gc();
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

        System.gc();
    }

    public void selectClient() {
        Client newClient = searchClientUtility.showAndWait();

        if (newClient == null) {
            return;
        }
        clientId = newClient.getClientId();
        text1.setText(newClient.getFullName());
    }

    public void selectAval() {
        Client newAval = searchClientUtility.showAndWait();

        if (newAval == null) {
            return;
        }
        avalId = newAval.getClientId();
        text2.setText(newAval.getFullName());
    }

    public void selectExecutive() {
        Employee newExecutive = searchEmployeeUtility.showAndWait();

        if (newExecutive == null) {
            return;
        }

        executiveId = newExecutive.getEmployeeId();

        text3.setText(newExecutive.getFullName());
    }

    public void selectSupervisor() {
        Employee newSupervisor = searchEmployeeUtility.showAndWait();

        if (newSupervisor == null) {
            return;
        }

        supervisorId = newSupervisor.getEmployeeId();

        text4.setText(newSupervisor.getFullName());
    }

    public void open() {
        DependencyContainer.getInstance().getDependency(HostServices.class).showDocument(urlField.getText());
    }

    public void deleteAval() {
        text2.clear();
        avalId = -1;
    }

    public void deleteExecutive() {
        text3.clear();
        executiveId = -1;
    }

    public void deleteSupervisor() {
        text4.clear();
        supervisorId = -1;
    }
}
