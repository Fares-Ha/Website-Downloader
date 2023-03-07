import controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import models.Model;

import java.io.File;

public class mainClass extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Scene scene=null;
        FXMLLoader loader =new FXMLLoader();
        Parent root=null;
        loader.setLocation(getClass().getResource("views/home.fxml"));
        root=loader.load();
        Controller c=(Controller) loader.getController();
        File f =new File("save.ser");
        if (f.exists())
            c.setM(Model.load());
        else
            c.setM(new Model());


        scene=new Scene(root);
        stage.setTitle("Website Downloader");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                for(int i=0;i<c.getM().getObs_list().size();i++){
                    if(c.getM().getObs_list().get(i).getIs_paused()==false){
                        c.getM().getObs_list().get(i).setIs_paused(true);
                        c.getM().getObs_list().get(i).getThread().stop();
                    }
                }
                c.getM().save();
                Platform.exit();
                System.exit(0);
            }
        });


    }

    public static void main(String[] args) {

        launch(args);
    }
}
