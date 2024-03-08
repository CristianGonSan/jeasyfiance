package com.comiftouch.jeasyfinance.controller;

import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.IOException;

public class BasicApiControllerTool2 {
    protected Task task = Task.NOTHING;
    protected StageManager stageManager;
    protected PropertiesManager propertiesManager;
    protected Session session;
    protected APIManager apiManager;
    protected int packSize;
    private boolean injectedDependency;

    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        this.stageManager = stageManager;
        this.propertiesManager = propertiesManager;
        this.session = session;
        this.packSize = propertiesManager.getIntProperty( "packSize");

        this.apiManager = new APIManager(
                session.getClientHttp(),
                propertiesManager.getStringProperty("apiUrl"),
                session.getToken());

        injectedDependency = true;
    }

    protected boolean checkTask() {
        if (task != Task.NOTHING) {
            stageManager.showAlertWarning("Ocupado", "Termine la tarea actual.");
            return true;
        }

        return false;
    }

    protected boolean checkProcessing() {
        if (apiManager.isProcessing()) {
            stageManager.showAlertWarning("Procesando.", "Espere a que termine el proceso actual.");
            return true;
        }
        return false;
    }

    public boolean isInjectedDependency() {
        return injectedDependency;
    }

    public boolean validateFields(TextField... fields) {
        for (TextField i : fields) {
            if (i.getText().isEmpty()) return false;
        }
        return true;
    }

    protected void error(int code, String message) {
        Platform.runLater(() -> {
            switch (code) {
                case 400, 409 -> stageManager.showAlertError("Datos inválidos", message);
                case 401 -> stageManager.showAlertError("Acceso Denegado", message);
                case 500 -> stageManager.showAlertError("Error del Servidor", message);
                default ->
                        StageManager.getInstanceSingleton().showAlertError("Respuesta Indeterminada", "Esto no estaba previsto.");
            }
        });
    }

    protected void failure(IOException e) {
        Platform.runLater(() -> stageManager.showAlertError("No se pudo establecer la conexión con el Servidor.", e.getMessage()));
    }

    public Task getTask() {
        return task;
    }

    public StageManager getStageManager() {
        return stageManager;
    }

    public PropertiesManager getPropertiesManger() {
        return propertiesManager;
    }

    public Session getSession() {
        return session;
    }

    public APIManager getApiManager() {
        return apiManager;
    }

    public int getPackSize() {
        return packSize;
    }
}