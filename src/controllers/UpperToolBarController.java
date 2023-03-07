package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UpperToolBarController {

    double xOffset;
    double yOffset;

    @FXML
    private Button minimizeBtn;
    @FXML
    private HBox upperBar;


    public void closeWindows(ActionEvent actionEvent) {
        Platform.exit();
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


