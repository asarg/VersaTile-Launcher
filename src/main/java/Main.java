import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/VersaTileLauncher.fxml"));
        primaryStage.setTitle("Welcome to VersaTile");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 707, 517));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Version v = AutoUpdate.getVersion();
        if(v!=null) {
            System.out.println("Installed " + v.toString());
            Release newRelease = AutoUpdate.checkForUpdates();
            if(newRelease!=null){
                System.out.println("New Release Available");
            }else {
                System.out.println("Up to date");
                try {
                    AutoUpdate.launchLatest(args);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }else {
            launch(args);
        }
    }
}
