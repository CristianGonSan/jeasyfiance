module com.comiftouch.jeasyfinance {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;

    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports com.comiftouch.jeasyfinance.model;
    exports com.comiftouch.jeasyfinance.controller;
    exports com.comiftouch.jeasyfinance.model.api.session;
    exports com.comiftouch.jeasyfinance.model.api.dataclass;
    exports com.comiftouch.jeasyfinance.model.api.actions;
    exports com.comiftouch.jeasyfinance.model.config;

    opens com.comiftouch.jeasyfinance.model.api.dataclass to com.fasterxml.jackson.databind, com.fasterxml.jackson.datatype.jsr310;
    opens com.comiftouch.jeasyfinance.model.api.actions to com.fasterxml.jackson.databind;

    opens com.comiftouch.jeasyfinance.model to javafx.fxml;
    opens com.comiftouch.jeasyfinance.view to javafx.fxml;
    opens com.comiftouch.jeasyfinance.view.request to javafx.fxml;
    opens com.comiftouch.jeasyfinance.view.loan to javafx.fxml;

    opens com.comiftouch.jeasyfinance.controller to javafx.fxml;
    opens com.comiftouch.jeasyfinance.model.api.session to javafx.fxml;

    exports com.comiftouch.jeasyfinance.controller.admin;
    opens com.comiftouch.jeasyfinance.controller.admin to javafx.fxml;
    exports com.comiftouch.jeasyfinance.controller.client;
    opens com.comiftouch.jeasyfinance.controller.client to javafx.fxml;
    exports com.comiftouch.jeasyfinance.controller.employee;
    opens com.comiftouch.jeasyfinance.controller.employee to javafx.fxml;
    exports com.comiftouch.jeasyfinance.controller.request;
    opens com.comiftouch.jeasyfinance.controller.request to javafx.fxml;
    exports com.comiftouch.jeasyfinance.controller.loan;
    opens com.comiftouch.jeasyfinance.controller.loan to javafx.fxml;
}