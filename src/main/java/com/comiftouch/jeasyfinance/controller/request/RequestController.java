package com.comiftouch.jeasyfinance.controller.request;

import com.comiftouch.jeasyfinance.controller.*;
import com.comiftouch.jeasyfinance.controller.BasicApiControllerTool2;
import com.comiftouch.jeasyfinance.controller.loan.LoanApiController;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.GenericCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Loan;
import com.comiftouch.jeasyfinance.model.api.dataclass.LoanRequest;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RequestController extends BasicApiControllerTool2 implements Initializable {
    @FXML
    private TableView<LoanRequest> table1;
    @FXML
    private Pane toolPane;
    @FXML
    private TextField textSearch;
    @FXML
    private MenuItem item1, item2, item3, item4, item5;

    @FXML
    private DatePicker start, end;
    private FileChooser fileChooser;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Estructuras de interfaz
    private final ObservableList<LoanRequest> tableData = FXCollections.observableArrayList();

    //Variables de objeto
    private LoanRequest lastObjet;

    private RequestUtilityController requestUtilityController;
    private LoanApiController loanApiController;

    private boolean searchDataShow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
    }

    @Override
    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        super.injectDependency(stageManager, propertiesManager, session);

        start.setValue(LocalDate.now().minusMonths(1));
        end.setValue(LocalDate.now());
        fileChooser = stageManager.getFileChooser();

        Stage dialogStage = new Stage();

        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stageManager.getStage());

        FXMLManager fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        dialogStage.setScene(new Scene(fxmlManager.getParent("request/util")));

        requestUtilityController = fxmlManager.getController("request/util");
        requestUtilityController.injectDependency(stageManager, propertiesManager, session);
        requestUtilityController.injectDialogStage(dialogStage);

        Stage dialogStage1 = new Stage();

        dialogStage1.initStyle(StageStyle.UTILITY);
        dialogStage1.initModality(Modality.WINDOW_MODAL);
        dialogStage1.initOwner(stageManager.getStage());

        dialogStage1.setScene(new Scene(fxmlManager.getParent("loan/util")));

        loanApiController = fxmlManager.getController("loan/util");
        loanApiController.injectDependency(stageManager, propertiesManager, session);
        loanApiController.injectDialogStage(dialogStage1);

        update1();
    }

    private void initTables() {
        TableColumn<LoanRequest, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<LoanRequest, String> column2 = new TableColumn<>("Session ID");
        column2.setCellValueFactory(new PropertyValueFactory<>("sessionId"));

        TableColumn<LoanRequest, String> column3 = new TableColumn<>("Client");
        column3.setCellValueFactory(new PropertyValueFactory<>("clientFullName"));

        TableColumn<LoanRequest, Float> column4 = new TableColumn<>("Monto $");
        column4.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<LoanRequest, Float> column5 = new TableColumn<>("Interest %");
        column5.setCellValueFactory(new PropertyValueFactory<>("interest"));

        TableColumn<LoanRequest, String> column6 = new TableColumn<>("Fecha");
        column6.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<LoanRequest, String> column8 = new TableColumn<>("Registro");
        column8.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        table1.getColumns().addAll(column1, column2, column3, column4, column5, column6, column8);

        table1.setItems(tableData);
        table1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (task == Task.NOTHING) {
                if (newValue != null) {
                    lastObjet = newValue;
                    if (toolPane.isDisable()) toolPane.setDisable(false);

                    chan();
                }
            }
        });

        table1.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LoanRequest item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    switch (item.getRequestStatus()) {
                        case 0 -> setStyle("");
                        case 1 -> setStyle("-fx-background-color: #4CAF50;");
                        case 2 -> setStyle("-fx-background-color: #F44336;");
                        case 3 -> setStyle("-fx-background-color: #9E9E9E;");
                        case 4 -> setStyle("-fx-background-color: #2196F3;");
                    }
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
        requestUtilityController.setStatus(Task.INSERTING);

        LoanRequest object = requestUtilityController.showAndWait();

        if (object == null) return;

        tableData.add(object);

        lastObjet = object;
        table1.getSelectionModel().select(lastObjet);
    }

    public void edit() {
        requestUtilityController.setStatus(Task.EDITING);
        requestUtilityController.insertObjet(lastObjet);

        LoanRequest objet = requestUtilityController.showAndWait();

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
        apiData.add("group", "request")
                .add("operation", "search")
                .add("full_name", textSearch.getText());

        apiManager.makeRequest(apiData, LoanRequest.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<LoanRequest> collection) {
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

        apiData.add("group", "request")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", 0);

        apiManager.makeRequest(apiData, LoanRequest.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<LoanRequest> collection) {

                Platform.runLater(() -> {
                    clearTable1();
                    tableData.addAll(collection);

                    stageManager.showToast("Tabla Actualizada.");
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

        apiData.add("group", "request")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", searchDataShow ? 0 : tableData.size());

        apiManager.makeRequest(apiData, LoanRequest.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<LoanRequest> collection) {
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

        apiData.add("group", "request")
                .add("operation", "disable")
                .add("request_id", lastObjet.getClientId())
                .add("is_deleted", !lastObjet.isDeleted());

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

        apiData.add("group", "request")
                .add("operation", "delete")
                .add("request_id", lastObjet.getClientId());

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

    public void accept() {
        if (checkProcessing()) return;
        showLoan(lastObjet.getRequestId());
    }

    public void copy() {
        /*
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
         */
    }

    public void showLoan(int loanRequestId) {
        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "loan")
                .add("operation", "read_single")
                .add("request_id", loanRequestId);

        apiManager.makeRequest(apiData, Loan.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Loan> collection) {
                Platform.runLater(() -> {

                    if (collection.isEmpty()) {
                        loanApiController.setStatus(Task.INSERTING);
                        loanApiController.insertLoanRequest(lastObjet);
                        Loan loan = loanApiController.showAndWait();

                        if (loan == null) return;

                        table1.refresh();
                    } else {
                        loanApiController.setStatus(Task.ADDING);
                        loanApiController.insertLoanRequest(lastObjet);
                        loanApiController.insertObjet(collection.get(0));

                        loanApiController.showAndWait();
                        table1.refresh();
                    }
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

    public void change(ActionEvent actionEvent) {
        if (checkProcessing() || checkTask()) return;

        int status;

        Object source = actionEvent.getSource();
        if (source == item1) {
            status = 1;
        } else if (source == item2) {
            status = 2;
        } else if (source == item3) {
            status = 3;
        } else if (source == item4) {
            status = 4;
        } else {
            status = 0;
        }

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "request")
                .add("operation", "change_status")
                .add("request_id", lastObjet.getRequestId())
                .add("status", status);

        apiManager.makeRequest(apiData, new BooleanCallBack() {
            @Override
            public void onSusses(int code) {
                Platform.runLater(() -> {
                    lastObjet.setRequestStatus(status);
                    table1.refresh();
                    chan();
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

    private void chan() {
        switch (lastObjet.getRequestStatus()) {
            case 0: {
                item1.setDisable(false);
                item2.setDisable(false);
                item3.setDisable(false);
                item4.setDisable(false);
                item5.setDisable(true);
                break;
            }
            case 1: {
                item1.setDisable(true);
                item2.setDisable(false);
                item3.setDisable(false);
                item4.setDisable(false);
                item5.setDisable(false);
                break;
            }
            case 2: {
                item1.setDisable(false);
                item2.setDisable(true);
                item3.setDisable(false);
                item4.setDisable(false);
                item5.setDisable(false);
                break;
            }
            case 3: {
                item1.setDisable(false);
                item2.setDisable(false);
                item3.setDisable(true);
                item4.setDisable(false);
                item5.setDisable(false);
                break;
            }
            case 4: {
                item1.setDisable(false);
                item2.setDisable(false);
                item3.setDisable(false);
                item4.setDisable(true);
                item5.setDisable(false);
                break;
            }
        }
    }


    public void report(ActionEvent actionEvent) {
        if (checkProcessing()) return;

        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "report")
                .add("operation", "general")
                .add("start", start.getValue().format(formatter))
                .add("end", end.getValue().format(formatter));

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

                    fileChooser.setInitialFileName("Reporte de Préstamo " + start.getValue() + " - " + end.getValue() + ".xlsx");
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