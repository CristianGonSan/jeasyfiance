package com.comiftouch.jeasyfinance.controller;

import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import okhttp3.Call;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainApiController extends BasicApiControllerTool2 implements Initializable {
    @FXML
    private VBox container;
    @FXML
    private Button clientB, adminB, employeeB, requestB;
    private Button lastButtons;
    private FXMLManager fxmlManager;

    private final HashMap<Button, String> buttonStringHashMap = new HashMap<>();

    private StageManager stageManager;
    private Session session;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DependencyContainer dependencyContainer = DependencyContainer.getInstance();

        stageManager = dependencyContainer.getDependency(StageManager.class);
        propertiesManager = dependencyContainer.getDependency(PropertiesManager.class);
        session = dependencyContainer.getDependency(Session.class);

        session = Session.getInstance();

        fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        if (session.getUserSystem().getLevel() < 5) adminB.setDisable(true);

        buttonStringHashMap.put(clientB, "client/main");
        buttonStringHashMap.put(adminB, "admin/main");
        buttonStringHashMap.put(employeeB, "employee/main");
        buttonStringHashMap.put(requestB, "request/main");
    }


    public void change(ActionEvent actionEvent) {
        Button currentButton = (Button) actionEvent.getSource();

        if (buttonStringHashMap.containsKey(currentButton)) {
            if (lastButtons != null) lastButtons.setDisable(false);
            currentButton.setDisable(true);
            lastButtons = currentButton;

            container.getChildren().clear();

            Parent parent = fxmlManager.getParent(buttonStringHashMap.get(lastButtons));

            BasicApiControllerTool2 basicApiControllerTool2 = fxmlManager.getController(buttonStringHashMap.get(lastButtons));
            if (!basicApiControllerTool2.isInjectedDependency()) {
                basicApiControllerTool2.injectDependency(stageManager, propertiesManager, session);
            }

            container.getChildren().add(parent);
        }

    }

    public void logout() {
        if (!stageManager.showAlertConfirmation("Cerrar sesión", "¿Desea cerrar la sesión actual?")) {
            return;
        }

        session.logout(new BooleanCallBack() {
            @Override
            public void onSusses(int code) {
                Platform.runLater(() -> {
                    stageManager.setTitle("Iniciar Sesión");
                    stageManager.setScene("login/main");
                    stageManager.setResizable(false);
                    stageManager.removeScene("main");
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
