package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.Downloadwebsite;

import java.net.URL;
import java.util.ResourceBundle;

public class Limit_controller implements Initializable {

    public Downloadwebsite getD() {
        return d;
    }

    public void setD(Downloadwebsite d) {
        this.d = d;
    }

    Downloadwebsite d;
    @FXML
    private TextField limit_field;

    @FXML
    void Limit_btn_event(ActionEvent event) {
        d.setSpeed_limit(new Integer(Integer.valueOf(limit_field.getText())*1024));
    }

    @FXML
    void Unlimit_btn_event(ActionEvent event) {
        d.setSpeed_limit(new Integer(Integer.MAX_VALUE));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        limit_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Alert a=new Alert(Alert.AlertType.ERROR);
                a.setContentText("Enter only numbers");
                a.show();
            }
        });
    }
}
