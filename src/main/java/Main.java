import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Version v = AutoUpdate.getVersion();
        System.out.println("Installed " + v.toString());
        Release newRelease = null;

        //try to check internet for updates
        try {
            newRelease = AutoUpdate.checkForUpdates();
        }
        catch(Exception e){}

        //if problem occured checking updates but have a version, start it
        if(v!=null && newRelease==null) {
            try {
                AutoUpdate.startLatest(null);
            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }else { //else need to download a version to run, or update
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/VersaTileLauncher.fxml")
            );
            VBox root = (VBox) loader.load();
            Controller controller = loader.getController();
            controller.setLatestRelease(newRelease);
            controller.setCurrentVersion(v);
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
