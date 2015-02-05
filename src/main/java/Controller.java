import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller{
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
    Text mainText;

    Release newRelease = null;
    Downloader downloader = null;
    Version currentVersion = null;

    public void setLatestRelease(Release r){
        newRelease =r;
        if(currentVersion!=null && newRelease !=null){
            mainText.setText("A new version of VersaTile is available.");
            installBtn.setText("Update");
        }
    }

    public void setCurrentVersion(Version v){
        currentVersion = v;
        if(currentVersion!=null && newRelease !=null){
            mainText.setText("A new version of VersaTile is available.");
            installBtn.setText("Update");
        }
    }

    @FXML
    public void installVersaTile(){
        installBtn.setVisible(false);
        try {
            if(newRelease==null)
                newRelease = AutoUpdate.getLatestAvailableRelease();
            downloader = new Downloader(newRelease);
            progressBox.setVisible(true);
            progressBar.progressProperty().bind(downloader.progressProperty());
            progressBox.visibleProperty().bind(downloader.runningProperty());
            downloader.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    mainText.setText("VersaTile is ready to launch.");
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
