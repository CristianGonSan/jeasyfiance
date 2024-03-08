package com.comiftouch.jeasyfinance.controller.employee;

import com.comiftouch.jeasyfinance.controller.*;
import com.comiftouch.jeasyfinance.controller.BasicApiControllerTool2;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.GenericCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Employee;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Call;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EmployeeController extends BasicApiControllerTool2 implements Initializable {
    //Interface
    @FXML
    private TableView<Employee> table1;
    @FXML
    private Pane toolPane;
    @FXML
    private TextField textSearch;

    //Estructuras de interfaz
    private final ObservableList<Employee> tableData = FXCollections.observableArrayList();

    //Variables de objeto
    private Employee lastObjet;

    private EmployeeUtilityController employeeUtilityController;

    private FileChooser fileChooser;

    private boolean searchDataShow;

    @Override
    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        super.injectDependency(stageManager, propertiesManager, session);

        fileChooser = stageManager.getFileChooser();

        Stage dialogStage = new Stage();

        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stageManager.getStage());

        FXMLManager fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        dialogStage.setScene(new Scene(fxmlManager.getParent("employee/util")));

        employeeUtilityController = fxmlManager.getController("employee/util");
        employeeUtilityController.injectDependency(stageManager, propertiesManager, session);
        employeeUtilityController.injectDialogStage(dialogStage);

        update1();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
    }

    private void initTables() {
        TableColumn<Employee, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> column2 = new TableColumn<>("Clave");
        column2.setCellValueFactory(new PropertyValueFactory<>("clue"));

        TableColumn<Employee, String> column3 = new TableColumn<>("Nombre");
        column3.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> column4 = new TableColumn<>("Apellidos");
        column4.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> column5 = new TableColumn<>("Teléfono");
        column5.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Employee, String> column6 = new TableColumn<>("Correo");
        column6.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Employee, Character> column7 = new TableColumn<>("Cargo");
        column7.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, String> column8 = new TableColumn<>("Registro");
        column8.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        table1.getColumns().addAll(column1, column2, column3, column4, column5, column6, column7, column8);

        table1.setItems(tableData);

        table1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (task == Task.NOTHING) {
                if (newValue != null) {
                    lastObjet = newValue;

                    if (toolPane.isDisable()) toolPane.setDisable(false);
                }
            }
        });

        table1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (lastObjet != null) {
                    edit();
                }
            }
        });
    }

    public void add() {
        employeeUtilityController.setStatus(Task.INSERTING);

        Employee object = employeeUtilityController.showAndWait();

        if (object == null) return;

        tableData.add(object);

        lastObjet = object;
        table1.getSelectionModel().select(lastObjet);
    }

    public void edit() {
        employeeUtilityController.setStatus(Task.EDITING);
        employeeUtilityController.insertObjet(lastObjet);

        Employee objet = employeeUtilityController.showAndWait();

        if (objet == null) return;
        objet.transfer(lastObjet);
        table1.refresh();
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
                    clearTable1();
                    tableData.addAll(collection);

                    task = Task.NOTHING;
                    searchDataShow = true;
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

    public void updateTable1() {
        if (checkProcessing() || checkTask()) return;
        update1();
    }

    private void update1() {
        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "employee")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", 0);

        apiManager.makeRequest(apiData, Employee.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Employee> collection) {

                Platform.runLater(() -> {
                    clearTable1();
                    tableData.addAll(collection);
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

    public void more1() {
        if (checkProcessing() || checkTask()) return;

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "employee")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", searchDataShow ? 0 : tableData.size());

        apiManager.makeRequest(apiData, Employee.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Employee> collection) {
                Platform.runLater(() -> {
                    if (collection.isEmpty()) {
                        stageManager.showToast("No hay mas datos");
                        return;
                    }

                    if (searchDataShow) {
                        clearTable1();
                    }

                    tableData.addAll(collection);

                    stageManager.showToast(collection.size() + " Registros mas");

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

    public void clearTable1() {
        lastObjet = null;
        toolPane.setDisable(true);

        tableData.clear();
        searchDataShow = false;
        System.gc();
    }

    public void changeDisable() {
        if (!stageManager.showAlertConfirmation("Confirmación de Borrado Suave",
                "El elemento se eliminará suavemente, pero permanecerá en registros referenciados. ¿Continuar?")) {
            return;
        }

        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "employee")
                .add("operation", "disable")
                .add("employee_id", lastObjet.getEmployeeId())
                .add("is_deleted", true);

        apiManager.makeRequest(apiData, new BooleanCallBack() {
            @Override
            public void onSusses(int code) {
                Platform.runLater(() -> {
                    lastObjet.setDeleted(!lastObjet.isDeleted());
                    table1.refresh();
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

    public void delete() {
        if (!stageManager.showAlertConfirmation("Eliminar registro.", "¿Desea eliminar el registro seleccionado?")) {
            return;
        }

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "employee")
                .add("operation", "delete")
                .add("employee_id", lastObjet.getEmployeeId());

        apiManager.makeRequest(apiData, new BooleanCallBack() {
            @Override
            public void onSusses(int code) {
                Platform.runLater(() -> {
                    toolPane.setDisable(true);

                    tableData.remove(lastObjet);
                    table1.refresh();

                    stageManager.showToast("Registro eliminado");
                });
            }

            @Override
            public void onError(int code, String message) {
                if (code == 409) {
                    Platform.runLater(() -> {
                        stageManager.showAlertWarning("Registro en uso.",
                                "El registro seleccionado esta siendo usado por otros.\nEn su lugar, podemos hacer un \"Borrado Suave\".");
                        changeDisable();
                    });
                } else {
                    error(code, message);
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                failure(e);
            }
        });
    }

    public void copy() {
        String sb = "ID: " + lastObjet.getEmployeeId() +
                "\nClave: " + lastObjet.getClue() +
                "\nNombre: " + lastObjet.getFirstName() + " " + lastObjet.getLastName() +
                "\nTeléfono: " + lastObjet.getPhoneNumber() +
                "\nCorreo: " + lastObjet.getEmail() +
                "\nPuesto: " + lastObjet.getPosition() +
                "\nRegión: " + lastObjet.getRegion() +
                "\nRegistro: " + lastObjet.getRegistrationDate();

        ClipboardContent content = new ClipboardContent();
        content.putString(sb);
        Clipboard.getSystemClipboard().setContent(content);

        stageManager.showToast("Copiado al portapapeles");
    }

    public void report(ActionEvent actionEvent) {
        if (checkProcessing()) return;

        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "report")
                .add("operation", "employee")
                .add("employee_id", lastObjet.getEmployeeId());

        apiManager.makeRequest(apiData, new GenericCallBack() {
            @Override
            public void onSusses(int code, JsonNode jsonNode) {
                Platform.runLater(() -> {
                    JsonNode content = jsonNode.get("content");
                    int count = content.get("row_count").asInt();

                    if (count < 1) {
                        stageManager.showAlertWarning("Sin registros", "No se encontraron datos");
                        return;
                    }

                    fileChooser.setInitialFileName("Reporte por empleado - " + lastObjet.getFirstName() + " " + lastObjet.getLastName() + ".xlsx");
                    File selectedFile = fileChooser.showSaveDialog(stageManager.getStage());

                    if (selectedFile == null) return;

                    try (FileWriter fileWriter = new FileWriter(selectedFile); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                        bufferedWriter.write("id, Cliente id, Cliente, Inicio, Final, Desembolsado, Pagos, Monto, Garantía, Total con Interés, Promotor, Supervisor");
                        bufferedWriter.newLine();

                        JsonNode data = content.get("data");
                        for (JsonNode row : data) {
                            bufferedWriter.write("%s,%s,\"%s\",%s,%s,%s,%s,%s,%s,%s,\"%s\",\"%s\"".formatted(
                                    row.get("request_id").asText(),
                                    row.get("client_id").asText(),
                                    row.get("client").asText(),
                                    row.get("start_date").asText(),
                                    row.get("end_date").asText(),
                                    row.get("amount").asText(),
                                    row.get("pay_count").asText(),
                                    row.get("sub_amount").asText(),
                                    row.get("warranty").asBoolean() ? row.get("amount").asDouble() * .1 : 0,
                                    row.get("total").asText(),
                                    row.get("executive").asText(),
                                    row.get("supervisor").asText()));
                            bufferedWriter.newLine();
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    stageManager.showAlertInformation("Guardado", "El reporte ha sido guardado");
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
