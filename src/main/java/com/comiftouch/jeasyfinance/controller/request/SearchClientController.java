package com.comiftouch.jeasyfinance.controller.request;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchClientController extends UtilityHandler<Client> implements Initializable {
    @FXML
    private TableView<Client> table1;
    @FXML
    private Button confirmButton;
    @FXML
    TextField textSearch;

    private final ObservableList<Client> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
    }

    private void initTable() {
        TableColumn<Client, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        TableColumn<Client, String> column2 = new TableColumn<>("Nombre");
        column2.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Client, String> column3 = new TableColumn<>("Apellidos");
        column3.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        TableColumn<Client, Character> column5 = new TableColumn<>("Sexo");
        column5.setCellValueFactory(new PropertyValueFactory<>("sex"));

        TableColumn<Client, String> column6 = new TableColumn<>("Nacimiento");
        column6.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        table1.getColumns().addAll(column1, column2, column3, column5, column6);

        table1.setItems(tableData);

        table1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (task == Task.NOTHING) {
                if (newValue != null) {
                    lastObjet = newValue;
                    if (confirmButton.isDisable()) confirmButton.setDisable(false);
                }
            }
        });
    }
    @Override
    public void setStatus(Task task) {

    }

    @Override
    public void insertObjet(Client objet) {

    }

    public void search() {
        if (checkProcessing() || checkTask()) return;

        if (textSearch.getText().isEmpty()) {
            return;
        }

        task = Task.SEARCHING;

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "client")
                .add("operation", "search")
                .add("full_name", textSearch.getText());

        apiManager.makeRequest(apiData, Client.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Client> collection) {
                Platform.runLater(() -> {
                    tableData.clear();
                    tableData.addAll(collection);
                    confirmButton.setDisable(true);
                    task = Task.NOTHING;
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

    public void confirm() {
        dialogStage.close();
    }

    public void cancel() {
        if (checkProcessing()) return;
        dialogStage.close();
    }
}
