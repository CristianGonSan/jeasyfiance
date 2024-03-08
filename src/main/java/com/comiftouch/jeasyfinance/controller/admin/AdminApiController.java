package com.comiftouch.jeasyfinance.controller.admin;

import com.comiftouch.jeasyfinance.controller.*;
import com.comiftouch.jeasyfinance.controller.BasicApiControllerTool2;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.*;
import com.comiftouch.jeasyfinance.model.api.dataclass.SessionRecord;
import com.comiftouch.jeasyfinance.model.api.dataclass.UserSystem;
import com.comiftouch.jeasyfinance.model.api.session.Session;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.Call;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminApiController extends BasicApiControllerTool2 implements Initializable {
    //Interface
    @FXML
    private TableView<UserSystem> table1;
    @FXML
    private TableView<SessionRecord> table2;
    @FXML
    private Pane toolPane;
    @FXML
    private Button disableButton;
    @FXML
    private TextField textSearch;


    //Estructuras de interfaz
    private final ObservableList<UserSystem> tableData = FXCollections.observableArrayList();
    private final ObservableList<SessionRecord> tableData2 = FXCollections.observableArrayList();

    //Variables de objeto
    private UserSystem lastObjet;
    private UtilityHandler<UserSystem> utilityApiController;
    private boolean searchDataShow;

    @Override
    public void injectDependency(StageManager stageManager, PropertiesManager propertiesManager, Session session) {
        super.injectDependency(stageManager, propertiesManager, session);

        Stage dialogStage = new Stage();

        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stageManager.getStage());

        FXMLManager fxmlManager = new FXMLManager("/com/comiftouch/jeasyfinance/view/");
        dialogStage.setScene(new Scene(fxmlManager.getParent("admin/util")));

        utilityApiController = fxmlManager.getController("admin/util");
        utilityApiController.injectDependency(stageManager, propertiesManager, session);
        utilityApiController.injectDialogStage(dialogStage);

        update1();
        update2();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
    }

    private void initTables() {
        TableColumn<UserSystem, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<UserSystem, String> column2 = new TableColumn<>("Username");
        column2.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UserSystem, Integer> column3 = new TableColumn<>("Nivel");
        column3.setCellValueFactory(new PropertyValueFactory<>("level"));

        TableColumn<UserSystem, String> column4 = new TableColumn<>("Nombre");
        column4.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<UserSystem, String> column5 = new TableColumn<>("Apellidos");
        column5.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<UserSystem, String> column6 = new TableColumn<>("Teléfono");
        column6.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<UserSystem, String> column7 = new TableColumn<>("Correo");
        column7.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<UserSystem, String> column8 = new TableColumn<>("Región");
        column8.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<UserSystem, LocalDateTime> column9 = new TableColumn<>("Registro");
        column9.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        TableColumn<UserSystem, Boolean> column10 = new TableColumn<>("Activo");
        column10.setCellValueFactory(new PropertyValueFactory<>("deleted"));


        table1.getColumns().addAll(column1, column2, column3, column4, column5, column6, column7, column8, column9, column10);

        table1.setItems(tableData);

        table1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (task == Task.NOTHING) {
                if (newValue != null) {
                    lastObjet = newValue;

                    if (toolPane.isDisable()) toolPane.setDisable(false);

                    if (lastObjet.isDeleted()) {
                        disableButton.setText("Habilitar");
                    } else {
                        disableButton.setText("Deshabilitar");
                    }
                }
            }
        });

        table1.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(UserSystem item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else if (item.isDeleted()) {
                    setStyle("-fx-background-color: #FF0000;");
                } else {
                    setStyle("");
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

        TableColumn<SessionRecord, Integer> column11 = new TableColumn<>("ID");
        column11.setCellValueFactory(new PropertyValueFactory<>("sessionId"));

        TableColumn<SessionRecord, Integer> column12 = new TableColumn<>("ID de Usuario");
        column12.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<SessionRecord, String> column13 = new TableColumn<>("Usuario");
        column13.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<SessionRecord, String> column14 = new TableColumn<>("Inicio");
        column14.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<SessionRecord, String> column15 = new TableColumn<>("Final");
        column15.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        table2.getColumns().addAll(column11, column12, column13, column14, column15);

        table2.setItems(tableData2);
    }

    public void add() {
        utilityApiController.setStatus(Task.INSERTING);
        UserSystem object = utilityApiController.showAndWait();

        if (object == null) return;

        tableData.add(object);

        lastObjet = object;
        table1.getSelectionModel().select(lastObjet);
    }

    public void edit() {
        utilityApiController.setStatus(Task.EDITING);
        utilityApiController.insertObjet(lastObjet);

        UserSystem objet = utilityApiController.showAndWait();

        if (objet == null) return;
        objet.transfer(lastObjet);
        table1.refresh();
    }

    public void changePassword() {
        if (lastObjet.isDeleted()) {
            stageManager.showAlertWarning("Usuario deshabilitado.", "Habilítelo primero.");
            return;
        }

        utilityApiController.setStatus(Task.CHANCING);
        utilityApiController.insertObjet(lastObjet);

        utilityApiController.showAndWait();
    }

    public void search() {
        if (checkProcessing() || checkTask()) return;

        if (textSearch.getText().isEmpty()) {
            return;
        }

        task = Task.SEARCHING;

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "admin")
                .add("operation", "search")
                .add("username", textSearch.getText());

        apiManager.makeRequest(apiData, UserSystem.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<UserSystem> collection) {
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

        apiData.add("group", "admin")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", 0);

        apiManager.makeRequest(apiData, UserSystem.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<UserSystem> collection) {

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

        apiData.add("group", "admin")
                .add("operation", "get_pack")
                .add("limit", packSize)
                .add("offset", searchDataShow ? 0 : tableData.size());

        apiManager.makeRequest(apiData, UserSystem.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<UserSystem> collection) {
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

    public void updateTable2() {
        if (checkProcessing()) return;
        update2();
    }

    private void update2() {
        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "admin")
                .add("operation", "get_pack_record")
                .add("limit", packSize)
                .add("offset", 0);

        apiManager.makeRequest(apiData, SessionRecord.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<SessionRecord> collection) {
                Platform.runLater(() -> {
                    clearTable2();
                    tableData2.addAll(collection);
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

    public void more2() {
        if (checkProcessing()) return;

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "admin")
                .add("operation", "get_pack_record")
                .add("limit", packSize)
                .add("offset", tableData2.size());

        apiManager.makeRequest(apiData, SessionRecord.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<SessionRecord> collection) {
                Platform.runLater(() -> {
                    if (collection.isEmpty()) {
                        stageManager.showToast("No hay mas datos");
                        return;
                    }

                    tableData2.addAll(collection);

                    stageManager.showToast(collection.size() + " Registros mas");
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

    public void clearTable2() {
        tableData2.clear();
        System.gc();
    }

    public void changeDisable() {
        if (!stageManager.showAlertConfirmation(
                lastObjet.isDeleted() ? "Habilitar Usuario." : "Deshabilitar Usuario.",
                lastObjet.isDeleted() ? "¿Desea habilitar al usuario seleccionado?" : "¿Desea deshabilitar al usuario seleccionado?")) {
            return;
        }

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "admin")
                .add("operation", "disable")
                .add("user_id", lastObjet.getUserId())
                .add("is_deleted", !lastObjet.isDeleted());

        apiManager.makeRequest(apiData, new BooleanCallBack() {
            @Override
            public void onSusses(int code) {
                Platform.runLater(() -> {
                    lastObjet.setDeleted(!lastObjet.isDeleted());
                    disableButton.setText(lastObjet.isDeleted() ? "Habilitar" : "Deshabilitar");
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

        apiData.add("group", "admin")
                .add("operation", "delete")
                .add("user_id", lastObjet.getUserId());

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
        String sb = "ID: " + lastObjet.getUserId() +
                "\nUsuario: " + lastObjet.getUsername() +
                "\nNombre: " + lastObjet.getFirstName() + " " + lastObjet.getLastName() +
                "\nTeléfono: " + lastObjet.getPassword() +
                "\nCorreo: " + lastObjet.getEmail() +
                "\nRegión: " + lastObjet.getRegion() +
                "\nNivel: " + lastObjet.getLevel() +
                "\nRegistro: " + lastObjet.getRegistrationDate();

        ClipboardContent content = new ClipboardContent();
        content.putString(sb);
        Clipboard.getSystemClipboard().setContent(content);

        stageManager.showToast("Copiado al portapapeles");
    }
}