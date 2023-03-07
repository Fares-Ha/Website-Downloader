package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Model;


import java.io.IOException;


public class NavBarController {

    Model m =new Model();

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


    public void home(ActionEvent actionEvent) throws IOException {
        Scene scene = homeBtn.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/home.fxml"));
        Parent p = loader.load();
        Controller c = (Controller) loader.getController();
        c.setM(getM());

        scene.setRoot(p);
    }

    public void table(ActionEvent actionEvent) throws IOException {

        Scene scene = tableBtn.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/table.fxml"));
        Parent p = null;
        p = loader.load();

        Table_controller tc = (Table_controller) loader.getController();
        tc.setM(getM());
       // tc.update_table();
        tc.refresh();
        scene.setRoot(p);

    }

    public void settings(ActionEvent actionEvent) throws IOException {
        System.out.println("ok im hear");
        Scene scene = settingsBtn.getScene();
        Window window = scene.getWindow();
        Stage stage = (Stage) window;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Settings_View.fxml"));
        Parent p = loader.load();
      //  Settings_controller settings_controller = (Settings_controller) loader.getController();
//        settings_controller.setM(this.getM());

        scene.setRoot(p);
    }

    public void info(ActionEvent actionEvent) throws IOException {
    }


}
