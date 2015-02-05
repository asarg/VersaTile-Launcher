import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Version v = AutoUpdate.getVersion();
        System.out.println("Installed " + v.toString());

        if(v!=null) {
            Task<Void> updater = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Release newRelease = AutoUpdate.checkForUpdates();
                    if(newRelease!=null){
                        System.out.println("New Release Available");
                        Downloader downloader = new Downloader(newRelease);
                        downloader.call();
                    }
                    return null;
                }
            };

            new Thread(updater).start();

            try {
                AutoUpdate.startLatest(null);
            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }else {
            Parent root = FXMLLoader.load(getClass().getResource("/VersaTileLauncher.fxml"));
            primaryStage.setTitle("Welcome to VersaTile");
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root, 707, 517));
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
