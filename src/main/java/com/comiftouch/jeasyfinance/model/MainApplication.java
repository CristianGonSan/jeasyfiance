package com.comiftouch.jeasyfinance.model;

import com.comiftouch.jeasyfinance.controller.DependencyContainer;
import com.comiftouch.jeasyfinance.controller.StageManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import okhttp3.Call;

import java.io.IOException;

public class MainApplication extends Application {
    private DependencyContainer dependencyContainer;

    @Override
    public void start(Stage stage) {
        dependencyContainer = DependencyContainer.getInstance();
        dependencyContainer.registerDependency(new PropertiesManager(
                "/com/comiftouch/jeasyfinance/archives/config.properties", "JEasyFinance/config.properties"));

        StageManager stageManager = new StageManager(stage, "/com/comiftouch/jeasyfinance/view/");

        dependencyContainer.registerDependency(stageManager);

        stageManager.setTitle("Iniciar Sesión");
        stageManager.setScene("login/main");
        stageManager.setIcon("/com/comiftouch/jeasyfinance/view/images/logos/JEasyFinanceLogo.png");
        stageManager.setResizable(false);
        stageManager.showStage();

        dependencyContainer.registerDependency(getHostServices());

        stage.setOnCloseRequest(event -> {
            event.consume();
            Session session = Session.getInstance();

            if (session.isProcessing()) {
                return;
            }

            if (session.isActiveSession()) {
                if (!stageManager.showAlertConfirmation("Cerrar programa", "¿Desea cerrar el programa?")) {
                    return;
                }
            } else {
                session.destroy();
                stage.close();
            }

            session.logout(new BooleanCallBack() {
                @Override
                public void onSusses(int code) {
                    Platform.runLater(() -> {
                        Session.getInstance().destroy();
                        stageManager.getStage().close();
                    });
                }

                @Override
                public void onError(int code, String message) {
                    Platform.runLater(() -> {
                        switch (code) {
                            case 400 -> stageManager.showAlertError("Datos Incompletos.", message);
                            case 401 -> stageManager.showAlertInformation("Acceso Denegado", message);
                            case 500 -> {
                                stageManager.showAlertError("Error del Servidor", message);
                                if (stageManager.showAlertConfirmation("¿Desea forzar el cierre?.", "Si el servidor no responde, puede forzar el cierre del programa.")) {
                                    Session.getInstance().destroy();
                                    stageManager.getStage().close();
                                }
                            }
                            default ->
                                    dependencyContainer.getDependency(StageManager.class).showAlertError("Respuesta Indeterminada", "No lo se.");
                        }
                    });
                }

                @Override
                public void onFailed(Call call, IOException e) {
                    stageManager.showAlertError("No se pudo estableser la conexión con el Servidor.",
                            "Revise su conexión o intentelo mas tarde.");
                    if (stageManager.showAlertConfirmation("¿Desea forzar el cierre?.", "Si el servidor no responde, puede forzar el cierre del programa.")) {
                        Session.getInstance().destroy();
                        stageManager.getStage().close();
                    }
                }
            });
        });
    }

    public void launchMain() {
        launch();
    }
}