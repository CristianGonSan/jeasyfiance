package com.comiftouch.jeasyfinance.controller.request;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Employee;
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

public class SearchEmployeeController extends UtilityHandler<Employee> implements Initializable {

    @FXML
    private TableView<Employee> table1;
    @FXML
    private Button confirmButton;
    @FXML
    TextField textSearch;


    private final ObservableList<Employee> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
    }

    private void initTable() {
        TableColumn<Employee, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> column2 = new TableColumn<>("Clave");
        column2.setCellValueFactory(new PropertyValueFactory<>("clue"));

        TableColumn<Employee, String> column3 = new TableColumn<>("Nombre");
        column3.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> column4 = new TableColumn<>("Apellidos");
        column4.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, Character> column7 = new TableColumn<>("Cargo");
        column7.setCellValueFactory(new PropertyValueFactory<>("position"));

        table1.getColumns().addAll(column1, column2, column3, column4);

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
    public void insertObjet(Employee objet) {

    }

    @Override
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

    public void search() {
        if (checkProcessing() || checkTask()) return;

        if (textSearch.getText().isEmpty()) {
            return;
        }

        task = Task.SEARCHING;

        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "employee")
                .add("operation", "search")
                .add("full_name", textSearch.getText());

        apiManager.makeRequest(apiData, Employee.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Employee> collection) {
                Platform.runLater(() -> {
                    Platform.runLater(() -> {
                        tableData.clear();
                        tableData.addAll(collection);
                        confirmButton.setDisable(true);
                        task = Task.NOTHING;
                    });
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
