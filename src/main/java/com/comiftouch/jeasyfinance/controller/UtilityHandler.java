package com.comiftouch.jeasyfinance.controller;

import javafx.stage.Stage;

public abstract class UtilityHandler<T> extends BasicApiControllerTool2 {
    protected Stage dialogStage;
    protected T lastObjet;

    protected boolean injectedStage;

    public void injectDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setTitle("Entrada de Datos.");

        dialogStage.setOnCloseRequest(event -> {
            if (checkProcessing()) {
                event.consume();
                return;
            }

            lastObjet = null;
        });
        injectedStage = true;
    }

    public boolean isInjectedStage() {
        return injectedStage;
    }

    public abstract void setStatus(Task task);

    public abstract void insertObjet(T objet);

    /**
     * Muestra el diálogo y espera hasta que el usuario interactúe con él.
     * Antes de llamar a este método, asegúrese de haber inicializado las propiedades
     * 'dialogStage', 'api', y 'stageManager' utilizando los métodos correspondientes.
     *
     * @return El objeto UserSystem resultante de la interacción del usuario.
     * @throws IllegalStateException Si alguna de las propiedades críticas no ha sido inicializada.
     */
    public T showAndWait() {
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
}
