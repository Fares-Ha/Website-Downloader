package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Model;


import java.io.File;
import java.io.IOException;

public class Settings_Controller {
    Model m;

    public Model getM() {
        return m;
    }

    public void setM(Model m) {
        this.m = m;
    }


    @FXML
    private Button homeBtn;

    @FXML
    private Button tableBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button infoBtn;
    @FXML
    private Button choose_location_btn;
    @FXML
    private Label location_label;

    @FXML
    void home(ActionEvent event) throws IOException {
        Scene scene = homeBtn.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../views/home.fxml"));
        Parent p = loader.load();
        Controller c=(Controller) loader.getController();
        c.setM(getM());

        scene.setRoot(p);
    }

    @FXML
    void table_view(ActionEvent event) throws IOException {

        Scene scene = tableBtn.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        FXMLLoader loader= new FXMLLoader(getClass().getResource("../views/table.fxml"));
        Parent p =loader.load();
        Table_controller tc=(Table_controller) loader.getController();
        tc.setM(getM());
        tc.update_table();
        tc.refresh();
        scene.setRoot(p);

    }
    @FXML
    void settings(ActionEvent event) throws IOException {    }
    @FXML
    void info(ActionEvent event) throws IOException {    }

    @FXML
    void choose_location_btn_event(ActionEvent event) {
        DirectoryChooser dc=new DirectoryChooser();
        dc.setTitle("Choose location");
        File f=dc.showDialog((Stage)choose_location_btn.getScene().getWindow());
        if(f==null)
            return;
        String path=f.getAbsolutePath();
        location_label.setText(path+"\\");
    }
}
