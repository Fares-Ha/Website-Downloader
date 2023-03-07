package controllers;

import com.jfoenix.controls.JFXTimePicker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import models.Downloadwebsite;
import models.Model;
import models.Scheduler;
import models.URL_validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    Model m;
    URL_validator validator=new URL_validator();

    public Model getM() {
        return m;
    }

    public void setM(Model m) {
        this.m = m;
    }


    double xOffset;
    double yOffset;

    @FXML
    private Button minimizeBtn;
    @FXML
    private HBox upperBar;

    @FXML
    TextField textField;
 @FXML
    Button startBtn;
    @FXML
    private Button homeBtn;

    @FXML
    private Button tableBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Button infoBtn;

    @FXML
    private Label location_label;

    @FXML
    private Button choose_location_btn;

    @FXML
    private JFXTimePicker start_time_picker;

    @FXML
    private JFXTimePicker end_time_picker;

    @FXML
    private CheckBox scheduling_check_box;

    @FXML
    private CheckBox single_page_check_box;


    @FXML
    void home(ActionEvent event) throws IOException {

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
    void choose_location_btn_event(ActionEvent event) {
        DirectoryChooser dc=new DirectoryChooser();
        dc.setTitle("Choose location");
        File f=dc.showDialog((Stage)choose_location_btn.getScene().getWindow());
        if(f==null)
            return;
        String path=f.getAbsolutePath();
        location_label.setText(path+"\\");
    }




    public void startBtnEvent(ActionEvent actionEvent) throws IOException, InterruptedException {
        String string=textField.getText();
        if(!validator.isUrl(string)){
            string =validator.getURLFromCustomURL(string);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("checking URL");
            a.show();
            string =validator.getURLFromCustomURL(string);
            a.close();
            if(validator.getResponseCode(string)==404){
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setContentText("Invalid URL");
                b.show();
                return;
            }
        }
        System.out.println(start_time_picker.getValue());
        Downloadwebsite D;
        if(scheduling_check_box.isSelected()){
            if(start_time_picker.getValue()==null||end_time_picker.getValue()==null){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Pick a start time and end time or disable scheduling");
                a.show();
                return;
            }
            D=new Downloadwebsite(string,location_label.getText(),new Scheduler(true,start_time_picker.getValue(),end_time_picker.getValue()),single_page_check_box.isSelected());

        }
        else
            D=new Downloadwebsite(string,location_label.getText(),new Scheduler(false),single_page_check_box.isSelected());
            D=m.getURL(D);
            if (D!=null){
                AnchorPane pane = new AnchorPane();
                FXMLLoader loader=new FXMLLoader(getClass().getResource("../views/Downloading progress.fxml"));
                Parent p = loader.load();
                Downloading_controller c=(Downloading_controller) loader.getController();
                c.setD(D);
                c.refresh();
                Scene scene = new Scene(p);
                Stage s = new Stage();
                s.setTitle(c.getD().getDomainName());
                s.setScene(scene);
                s.initStyle(StageStyle.UNDECORATED);
                s.show();

//                s.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                    @Override
//                    public void handle(WindowEvent windowEvent) {
//                        c.timer.cancel();
//                        s.close();
//                    }
//                });
            }





    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(m==null)
            m=new Model();
        String home=System.getProperty("user.home");
        location_label.setText(home+"\\Desktop\\");

        start_time_picker.setDisable(true);
        end_time_picker.setDisable(true);
        scheduling_check_box.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(scheduling_check_box.isSelected()){
                    start_time_picker.setDisable(false);
                    end_time_picker.setDisable(false);
                }

                else{
                    start_time_picker.setDisable(true);
                    end_time_picker.setDisable(true);
                }

            }
        });
    }




    public void closeWindows(ActionEvent actionEvent) {
        for(int i=0;i<getM().getObs_list().size();i++){
            if(getM().getObs_list().get(i).getIs_paused()==false){
                getM().getObs_list().get(i).setIs_paused(true);
                getM().getObs_list().get(i).getThread().stop();
            }
        }
        getM().save();
        Platform.exit();
        System.exit(0);

    }

    public void minimizeWindows(ActionEvent actionEvent) {
        Stage stage =(Stage) minimizeBtn.getScene().getWindow();

        // is stage minimizable into task bar. (true | false)
        stage.setIconified(true);

    }

    public void getStagePosition(MouseEvent mouseEvent) {
        Stage stage =(Stage) upperBar.getScene().getWindow();
        xOffset=stage.getX() -  mouseEvent.getScreenX();
        yOffset=stage.getY() -  mouseEvent.getScreenY();

    }

    public void setStagePosition(MouseEvent mouseEvent) {
        Stage stage =(Stage) upperBar.getScene().getWindow();
        stage.setX(mouseEvent.getScreenX()+xOffset);
        stage.setY(mouseEvent.getScreenY()+yOffset);
        stage.setOpacity(0.7);
    }


    public void resetOpacity(MouseEvent mouseEvent) {
        Stage stage =(Stage) upperBar.getScene().getWindow();
        stage.setOpacity(1);
    }
}
