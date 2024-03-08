package com.comiftouch.jeasyfinance.controller;

import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigController extends UtilityHandler<Object> implements Initializable {
    @FXML
    private TextField url;
    @FXML
    private Slider slider;

    @Override
    public void setStatus(Task task) {

    }

    @Override
    public void insertObjet(Object objet) {

    }

    @Override
    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        super.injectDependency(stageManager, propertiesManager, session);

        url.setText(propertiesManager.getStringProperty("apiUrl"));
        slider.setValue(propertiesManager.getIntProperty("packSize", 50));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void save(ActionEvent actionEvent) {
        Properties properties = propertiesManager.getProperties();

        properties.setProperty("apiUrl", url.getText());

        String packSize = String.valueOf((int) slider.getValue());

        properties.setProperty("packSize", packSize);

        propertiesManager.store();

        stageManager.showToast("Configuración Guardada.");
    }
}
