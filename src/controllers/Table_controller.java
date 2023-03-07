package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import models.Downloadwebsite;
import models.Model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Table_controller implements Initializable {

    Model m;
    Timer timer=new Timer();


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
    private Button homeBtn;

    @FXML
    private Button tableBtn;

    @FXML
    private TableView<Downloadwebsite> table;

    @FXML
    private TableColumn<Downloadwebsite, String> website_col;

    @FXML
    private TableColumn<Downloadwebsite, String> status_col;

    @FXML
    private TableColumn<Downloadwebsite, LocalDateTime> date_col;

    @FXML
    private TableColumn<Downloadwebsite, Float> downloaded_col;

    @FXML
    private TableColumn<Downloadwebsite, Integer> down_speed_col;

    @FXML
    private ContextMenu context_menu;

    @FXML
    private MenuItem show_download_progress_context_item;

    @FXML
    private MenuItem resume_context_item;

    @FXML
    private MenuItem pause_context_item;

    @FXML
    private MenuItem delete_context_item;


    public Table_controller() throws IOException {
    }

    @FXML
    void home(ActionEvent event) throws IOException, InterruptedException {
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

    }


    public void update_table(){

//        website_col.setCellValueFactory(new  PropertyValueFactory<>("BaseURL"));
//        status_col.setCellValueFactory(new  PropertyValueFactory<>("status"));
//        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        date_col.setCellFactory((TableColumn<Downloadwebsite, LocalDateTime> column) -> {
//            return new TableCell<Downloadwebsite, LocalDateTime>() {
//                @Override
//                protected void updateItem(LocalDateTime item, boolean empty) {
//                    super.updateItem(item, empty);
//                    if (item == null || empty) {
//                        setText(null);
//                    } else {
//                        setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")));
//                    }
//                }
//            };
//        });
//
//        downloaded_col.setCellValueFactory(new PropertyValueFactory<>("down_percent"));
        ObservableList<Downloadwebsite> list= FXCollections.observableArrayList(m.getObs_list());
        table.setItems(list);
        table.refresh();
    }

    public void refresh() {
        timer.schedule( new TimerTask() { @Override public void run() {
            Platform.runLater(new Runnable() { @Override public void run() {
                update_table(); }});
        }}, 0,250 );
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        website_col.setCellValueFactory(new  PropertyValueFactory<>("BaseURL"));
        status_col.setCellValueFactory(new  PropertyValueFactory<>("status"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        date_col.setCellFactory((TableColumn<Downloadwebsite, LocalDateTime> column) -> {
            return new TableCell<Downloadwebsite, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")));
                    }
                }
            };
        });

        downloaded_col.setCellValueFactory(new PropertyValueFactory<>("down_per"));
        downloaded_col.setCellFactory((TableColumn<Downloadwebsite, Float> column) -> {
            return new TableCell<Downloadwebsite, Float>() {
                @Override
                protected void updateItem(Float item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(new DecimalFormat("##.##",new DecimalFormatSymbols(Locale.ENGLISH)).format(item) +" %");
                    }
                }
            };
        });
        down_speed_col.setCellValueFactory(new PropertyValueFactory<>("speed_limit"));
        down_speed_col.setCellFactory((TableColumn<Downloadwebsite, Integer> column) -> {
            return new TableCell<Downloadwebsite, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    }else if(item.intValue()==Integer.MAX_VALUE)
                        setText("Unlimited");
                    else {
                        setText(item.intValue()/1024+" kb");
                    }
                }
            };
        });

                context_menu.setOnShowing(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        Downloadwebsite selected = table.getSelectionModel().getSelectedItem();
//                        if (selected == null)
//                            context_menu.
//                        else
                        if (selected.getStatus().equals("completed")) {
                            show_download_progress_context_item.setDisable(true);
                            pause_context_item.setDisable(true);
                            resume_context_item.setDisable(true);
                        }
                        else if(selected.getIs_paused()==true)
                        {
                            pause_context_item.setDisable(true);
                            resume_context_item.setDisable(false);
                            show_download_progress_context_item.setDisable(false);

                        }
                        else if (selected.getIs_paused()==false){
                            resume_context_item.setDisable(true);
                            show_download_progress_context_item.setDisable(false);
                            pause_context_item.setDisable(false);

                        }
                    }
                });


    }

    @FXML
    void show_download_progress_context_item_event(ActionEvent event) throws IOException {
        Downloadwebsite selected=table.getSelectionModel().getSelectedItem();
        if(selected==null)
            return;
        else if(selected.getStatus().equals("completed")){
            Alert a=new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("This website has finished downloading");
            a.show();
            return;
        }
        AnchorPane pane = new AnchorPane();
        FXMLLoader loader=new FXMLLoader(getClass().getResource("../views/Downloading progress.fxml"));
        Parent p = loader.load();
        Downloading_controller c=(Downloading_controller) loader.getController();
        c.setD(selected);
        c.refresh();
        Scene scene = new Scene(p);
        Stage s = new Stage();
        s.setTitle(c.getD().getDomainName());
        s.setScene(scene);
        s.show();
        s.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                c.timer.cancel();
                s.close();
            }
        });


    }

    @FXML
    void resume_context_item_event(ActionEvent event) throws IOException, InterruptedException {
        Downloadwebsite selected = table.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        else if (selected.getStatus().equals("completed")) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("This website has finished downloading");
            a.show();
            return;
        }
        selected.setIs_paused(false);
        AnchorPane pane = new AnchorPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Downloading progress.fxml"));
        Parent p = loader.load();
        Downloading_controller c = (Downloading_controller) loader.getController();
        c.setD(selected);
        c.getD().resume();
        c.refresh();
        Scene scene = new Scene(p);
        Stage s = new Stage();
        s.setTitle(c.getD().getDomainName());
        s.setScene(scene);
        s.show();
        s.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                c.timer.cancel();
                s.close();
            }
        });

    }
    @FXML
    void pause_context_item_event(ActionEvent event) throws IOException, InterruptedException {
        Downloadwebsite selected = table.getSelectionModel().getSelectedItem();

        if (selected == null)
            return;
        else if (selected.getStatus().equals("completed")) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("This website has finished downloading");
            a.show();
            return;
        }
        selected.setIs_paused(true);
        selected.getThread().stop();
    }

    @FXML
    void delete_context_item_event(ActionEvent event) throws IOException, InterruptedException {
        Downloadwebsite selected = table.getSelectionModel().getSelectedItem();

        if (selected == null)
            return;
        if(!selected.getStatus().equals("completed")&&selected.getIs_paused()==false)
            selected.getThread().stop();

        if(!selected.getSingle_page())
            m.getAll_base().remove(selected.getDomainName(selected.getBaseURL()));
        else
            m.getAll_base().remove(selected.getpageName(selected.getBaseURL()));
        File f=new File(selected.getDirs());
        if(f.exists())
            recursiveDelete(f);
        m.getObs_list().remove(m.getObs_list().indexOf(selected));
    }

    public static void recursiveDelete(File file) {
        //to end the recursive loop
        if (!file.exists())
            return;

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
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

    public void getStagePosition(javafx.scene.input.MouseEvent mouseEvent) {
        Stage stage =(Stage) upperBar.getScene().getWindow();
        xOffset=stage.getX() -  mouseEvent.getScreenX();
        yOffset=stage.getY() -  mouseEvent.getScreenY();

    }

    public void setStagePosition(javafx.scene.input.MouseEvent mouseEvent) {
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
