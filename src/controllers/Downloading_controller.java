package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Downloadwebsite;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Downloading_controller implements Initializable {

    public Downloadwebsite getD() {
        return d;
    }

    public void setD(Downloadwebsite d) {
        this.d = d;
    }

    Downloadwebsite d;
    Timer timer;

    double xOffset;
    double yOffset;

    @FXML
    private Button minimizeBtn;
    @FXML
    private HBox upperBar;
    @FXML
    private Label website_field;

    @FXML
    private Label status_field;

    @FXML
    private Label downloaded_field;

    @FXML
    private Label down_percent_field;

    @FXML
    private Label data_rate_field;

    @FXML
    private Label size_field;

    @FXML
    private CheckBox checkbox;

    @FXML
    private TextField limit_text_field;

    @FXML
    private Label elapsed_time_field;

    @FXML
    private Label estimated_time_field;

    @FXML
    private ProgressBar progress_bar;

    @FXML
    private ProgressBar progress_bar1;

    @FXML
    private Button pause_resume_btn;


    public void refresh(){

        if(d.getSpeed_limit().intValue()!=Integer.MAX_VALUE){
            checkbox.setSelected(true);
            limit_text_field.setText(String.valueOf(d.getSpeed_limit().intValue()/1024));
        }
        timer = new Timer();
        timer.schedule(new TimerTask() { @Override public void run() {
            Platform.runLater(new Runnable() { @Override public void run() {
                if(d.getStatus().equals("completed")){
                    timer.cancel();
                    Stage s =(Stage)progress_bar.getScene().getWindow();
                    s.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            play("noti.wav");
                        }
                    }).start();

                    try {
                        Desktop.getDesktop().open(new File(d.getDirs()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                website_field.setText("Website : \t"+d.getDomainName());
                status_field.setText("Status : \t"+d.getStatus());
                set_size_field();
                set_downloaded_field();
                down_percent_field.setText("Downloaded percent : \t"+new DecimalFormat("##.##",new DecimalFormatSymbols(Locale.ENGLISH)).format(d.getDown_per())+" %");
                set_data_rate_field();
                if(!d.getStatus().equals("completed")){
                    elapsed_time_field.setText("Elapsed time : \t"+ LocalTime.ofSecondOfDay(Duration.between(d.getDate(), LocalDateTime.now()).getSeconds()));
                    estimated_time_field.setText("estimated time : \t"+ LocalTime.ofSecondOfDay((long) (d.getSize()/d.getSpeed_limit().intValue()-d.getDownloaded()/d.getSpeed_limit().intValue())));

                }

                progress_bar.setProgress(d.getDown_per()/100);
                progress_bar1.setProgress(((float)d.getConverted_links()/d.getLinks().size()));
                if(!checkbox.isSelected()||limit_text_field.getText().equals(""))
                    d.setSpeed_limit(new Integer(Integer.valueOf(Integer.MAX_VALUE)));
                else
                    d.setSpeed_limit(new Integer(Integer.valueOf(limit_text_field.getText())*1024));

            }});
        }}, 0,250 );


    }
    public void set_size_field(){
        if(d.getSize()>(1024*1024*1024))
            size_field.setText("Size : \t"+new DecimalFormat("###.###",new DecimalFormatSymbols(Locale.ENGLISH)).format(d.getSize()/(1024*1024*1024))+" Gb");
        else
            size_field.setText("Size : \t"+new DecimalFormat("###.###",new DecimalFormatSymbols(Locale.ENGLISH)).format(d.getSize()/(1024*1024))+" Mb");

    }
    public void set_downloaded_field(){
        if(d.getSize()>(1024*1024*1024))
            downloaded_field.setText("Downloaded : \t"+new DecimalFormat("###.###",new DecimalFormatSymbols(Locale.ENGLISH)).format(d.getDownloaded()/(1024*1024*1024))+" Gb");
        else
            downloaded_field.setText("Downloaded : \t"+new DecimalFormat("###.###",new DecimalFormatSymbols(Locale.ENGLISH)).format(d.getDownloaded()/(1024*1024))+" Mb");

    }
    public void set_data_rate_field(){
        if(d.getSpeed_limit().intValue()==Integer.MAX_VALUE)
            data_rate_field.setText("Data rate :\tUnlimited");
        else
            data_rate_field.setText("Data rate :\t"+d.getSpeed_limit().intValue()/1024+" Kb");

    }

    @FXML
    void pause_resume_btn_event(ActionEvent event) throws IOException, InterruptedException {
        if (d.getIs_paused()==true){
            pause_resume_btn.setText("Pause");
            d.setIs_paused(false);
            d.resume();
        }
        else {
            pause_resume_btn.setText("Resume");
            d.setIs_paused(true);
            d.getThread().stop();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        limit_text_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                limit_text_field.setText("");
                Alert a=new Alert(Alert.AlertType.ERROR);
                a.setContentText("Enter only numbers");
                a.show();
            }
        });
        progress_bar1.setStyle("-fx-accent: orange;");




//        limit_text_field.setOnKeyPressed(new EventHandler<KeyEvent>()
//        {
//            @Override
//            public void handle(KeyEvent ke)
//            {
//                if (ke.getCode().equals(KeyCode.ENTER))
//                {
//                    d.setSpeed_limit(new Integer(Integer.valueOf(limit_text_field.getText())*1024));
//
//                }
//            }
//        });
    }

    public static void play(String filename)
    {
        String strFilename = filename;

            File soundFile = new File(strFilename);


        AudioInputStream audioStream = null;
        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioFormat audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        SourceDataLine sourceLine = null;
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[128000];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }



    public void closeWindows(ActionEvent actionEvent) {
        this.timer.cancel();
        //Platform.exit();
        Stage stage=(Stage)progress_bar.getScene().getWindow();
        stage.close();


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
