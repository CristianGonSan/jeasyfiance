package com.comiftouch.jeasyfinance.controller.loan;

import com.comiftouch.jeasyfinance.controller.Task;
import com.comiftouch.jeasyfinance.controller.UtilityHandler;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.CollectionCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.GenericCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.Loan;
import com.comiftouch.jeasyfinance.model.api.dataclass.LoanRequest;
import com.comiftouch.jeasyfinance.model.api.dataclass.Pay;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoanApiController extends UtilityHandler<Loan> implements Initializable {
    @FXML
    private DatePicker date;
    @FXML
    private TextField count;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TableView<Pay> table1;
    @FXML
    private CheckBox warranty;
    @FXML
    private HBox toolPane;
    @FXML
    private Button report;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final FileChooser fileChooser = new FileChooser();

    private Pay lastPay;
    private LoanRequest loanRequest;

    private LocalDate star;
    private LocalDate end;
    private int periodDay;

    private float total;

    private final ObservableList<Pay> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        count.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!Character.isDigit(event.getCharacter().charAt(0))) {
                event.consume();
            }
        });

        date.setValue(LocalDate.now());
        ObservableList<String> combo = FXCollections.observableArrayList();

        combo.addAll("Semanal", "Catorcenal", "Quincenal", "Mensual");
        comboBox.setItems(combo);

        comboBox.getSelectionModel().select(0);

        initTables();
    }

    private void initTables() {
        TableColumn<Pay, Integer> column1 = new TableColumn<>("ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("payId"));

        TableColumn<Pay, String> column2 = new TableColumn<>(" Fecha");
        column2.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Pay, String> column3 = new TableColumn<>("Monto");
        column3.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Pay, String> column4 = new TableColumn<>("Registro");
        column4.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        table1.getColumns().addAll(column1, column2, column3, column4);

        table1.setItems(tableData);

        table1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (task == Task.NOTHING) {
                if (newValue != null) {
                    lastPay = newValue;
                }
            }
        });

        table1.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Pay item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.isPaid()) {
                        setStyle("-fx-background-color: #99cc66;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        table1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (lastPay != null) {
                    if (loanRequest.getRequestStatus() == 0) return;

                    boolean flag = lastPay.isPaid();

                    if (stageManager.showAlertConfirmation(
                            flag ? "Desmarcar!" : "Marcar!",
                            flag ? "¿Desea desmarcar como pagado?" : "¿Desea marcar como pagado?")) {

                        APIManager.APIData apiData = new APIManager.APIData(true);

                        apiData.add("group", "pay")
                                .add("operation", "change-status")
                                .add("pay_id", lastPay.getPayId())
                                .add("status", !flag);

                        apiManager.makeRequest(apiData, new BooleanCallBack() {
                            @Override
                            public void onSusses(int code) {
                                Platform.runLater(() -> {
                                    lastPay.setPaid(!flag);
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
                }
            }
        });
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
    }

    @Override
    public Loan showAndWait() {
        return super.showAndWait();
    }

    @Override
    public void setStatus(Task task) {
        this.task = task;

        boolean b = (task == Task.INSERTING);

        toolPane.setDisable(!b);
        warranty.setDisable(!b);
        report.setDisable(b);

        clear();
    }

    @Override
    public void insertObjet(Loan objet) {
        lastObjet = objet;

        if (task == Task.ADDING) {
            setObjet(objet);
        }
    }

    public void insertLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
        total = loanRequest.getAmount();
        total += total * loanRequest.getInterest() / 100;
    }

    public void generate() {
        if (count.getText().isEmpty()) {
            stageManager.showAlertWarning("Campo Vació!", "Ingrese una cantidad valida.");
            return;
        }

        tableData.clear();

        int period = Integer.parseInt(count.getText());
        periodDay = period;

        float subAmount = total / period;

        int type = comboBox.getSelectionModel().getSelectedIndex();

        LocalDate localDate = LocalDate.now();
        star = localDate;
        switch (type) {
            case 0: {
                Pay pay = new Pay();
                pay.setAmount(subAmount);
                pay.setDate(localDate.toString());
                tableData.add(pay);

                for (int i = 0; i < period; i++) {
                    localDate = localDate.plusWeeks(1);
                    pay = new Pay();
                    pay.setAmount(subAmount);
                    pay.setDate(localDate.toString());
                    tableData.add(pay);
                }
                break;
            }
            case 1: {
                Pay pay;

                for (int i = 0; i < period; i++) {
                    localDate = localDate.plusWeeks(2);
                    pay = new Pay();
                    pay.setAmount(subAmount);
                    pay.setDate(localDate.toString());
                    tableData.add(pay);
                }
                break;
            }
            case 2: {
                Pay pay;
                for (int i = 0; i < period; i++) {
                    if (localDate.getDayOfMonth() > 15) {
                        if (localDate.lengthOfMonth() >= 30) {
                            localDate = localDate.withDayOfMonth(30);
                        } else {
                            localDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
                        }
                        pay = new Pay();
                        pay.setAmount(subAmount);
                        pay.setDate(localDate.format(formatter));
                        tableData.add(pay);

                        localDate = localDate.withDayOfMonth(15);
                        localDate = localDate.plusMonths(1);
                    } else {
                        pay = new Pay();
                        pay.setAmount(subAmount);
                        pay.setDate(localDate.format(formatter));
                        tableData.add(pay);

                        localDate = localDate.withDayOfMonth(16);
                    }
                }

                break;
            }
            case 3: {
                Pay pay = new Pay();
                pay.setAmount(100);
                pay.setDate(localDate.toString());
                tableData.add(pay);

                for (int i = 0; i < period; i++) {
                    localDate = localDate.plusMonths(1);
                    pay = new Pay();
                    pay.setAmount(subAmount);
                    pay.setDate(localDate.toString());
                    tableData.add(pay);
                }
                break;
            }
        }

        end = localDate;
    }

    public void clear() {
        date.setValue(LocalDate.now());
        count.clear();
        comboBox.getSelectionModel().select(0);
        warranty.setSelected(false);
        tableData.clear();
    }

    private Loan getObjet() {
        Loan objet = new Loan();

        objet.setRequestId(loanRequest.getRequestId());
        objet.setStartDate(star.toString());
        objet.setEndDate(end.toString());
        objet.setWarranty(warranty.isSelected());
        objet.setPeriodDay(periodDay);

        return objet;
    }

    private void setObjet(@NotNull Loan objet) {
        warranty.setSelected(objet.isWarranty());
        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "pay")
                .add("operation", "search")
                .add("loan_id", lastObjet.getLoanId());

        apiManager.makeRequest(apiData, Pay.class, new CollectionCallBack<>() {
            @Override
            public void onSusses(int code, String message, ArrayList<Pay> collection) {
                Platform.runLater(() -> {
                    tableData.clear();
                    count.setText(String.valueOf(collection.size()));
                    tableData.addAll(collection);

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
        if (checkProcessing()) return;

        if (tableData.isEmpty()) {
            stageManager.showAlertWarning("Tabla Vacía!", "Genere los datos de pagos.");
            return;
        }

        switch (task) {
            case INSERTING -> {
                APIManager.APIData apiData = new APIManager.APIData(true);

                apiData.add("group", "loan")
                        .add("operation", "create")
                        .add("loan", getObjet())
                        .add("payments", tableData);

                apiManager.makeRequest(apiData, Loan.class, new CollectionCallBack<>() {
                    @Override
                    public void onSusses(int code, String message, ArrayList<Loan> collection) {
                        Platform.runLater(() -> {
                            lastObjet = collection.get(0);
                            loanRequest.setRequestStatus(1);
                            task = Task.NOTHING;

                            dialogStage.close();
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
            case ADDING -> {

            }
        }

    }

    public void cancel() {
        if (checkProcessing()) return;
        lastObjet = null;
        dialogStage.close();
    }

    public void report() {
        if (checkProcessing()) return;

        APIManager.APIData apiData = new APIManager.APIData(true);
        apiData.add("group", "report")
                .add("operation", "single")
                .add("loan_id", lastObjet.getLoanId());

        fileChooser.setInitialFileName("Calendario SolicitudId-" + loanRequest.getRequestId() + ".csv");
        File selectedFile = fileChooser.showSaveDialog(stageManager.getStage());

        if (selectedFile == null) return;

        apiManager.makeRequest(apiData, new GenericCallBack() {
            @Override
            public void onSusses(int code, JsonNode jsonNode) {
                Platform.runLater(() -> {
                    JsonNode data = jsonNode.get("content").get("data").get(0);

                    try (FileWriter fileWriter = new FileWriter(selectedFile); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                        bufferedWriter.write("Cliente:, " + data.get("client").asText());
                        bufferedWriter.newLine();
                        bufferedWriter.write("Monto:, " + data.get("amount").asText());
                        bufferedWriter.newLine();
                        bufferedWriter.write("Fecha:, " + data.get("start_date").asText());
                        bufferedWriter.newLine();
                        bufferedWriter.newLine();

                        bufferedWriter.write("No., id, Fecha de Inicio, Monto, Estado");
                        bufferedWriter.newLine();
                        int count = 1;
                        for (Pay pay : tableData) {
                            bufferedWriter.write(count++
                                    + "," + pay.getPayId()
                                    + "," + pay.getDate()
                                    + "," + pay.getAmount()
                                    + "," + (pay.isPaid() ? "Pagado" : "Pendiente"));
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