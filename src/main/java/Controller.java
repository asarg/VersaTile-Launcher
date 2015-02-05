import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import sun.tools.jar.resources.jar;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Controller {
    @FXML
    Button installBtn;

    @FXML
    Button launchBtn;

    @FXML
    HBox progressBox;

    @FXML
    Label downloadLabel;

    @FXML
    ProgressBar progressBar;

    @FXML
    public void installVersaTile(){
        installBtn.setVisible(false);
        try {
            Release latest = AutoUpdate.getLatestAvailableRelease();
            Downloader downloader = new Downloader(latest);
            progressBox.setVisible(true);
            progressBar.progressProperty().bind(downloader.progressProperty());
            progressBox.visibleProperty().bind(downloader.runningProperty());
            downloader.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    launchBtn.setVisible(true);
                }
            });
            new Thread(downloader).start();
            downloadLabel.setText("Downloading " + downloader.fileName);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML void launchVersaTile(){
        System.out.println("launch");
        try {
            AutoUpdate.startLatest(null);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
