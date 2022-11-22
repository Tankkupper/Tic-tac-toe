package application.client;

import application.client.controller.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

public class Main extends Application {
    public static Logger log = Logger.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) {
        try {

            // StageStyle.UNDECORATED
            // StageStyle.TRANSPARENT
            primaryStage.initStyle(StageStyle.TRANSPARENT);
//            FXMLLoader fxmlLoader = new FXMLLoader();
//
//            fxmlLoader.setLocation(getClass().getClassLoader().getResource("loginUI.fxml"));
//            Pane root = fxmlLoader.load();
//            System.out.println(root.getId());
//            Scene scene = new Scene(root);
//            scene.getStylesheets().add(
//                    Objects.requireNonNull(
//                            Main.class.getResource(
//                                    "/com/only/common/css/Only.css")).toString());
//            LoginController loginController = fxmlLoader.getController();
//            loginController.setSceneManager(new SceneManager(primaryStage));
            SceneManager sceneManager = new SceneManager(primaryStage);
            sceneManager.switchScene("loginUI.fxml");
            primaryStage.setTitle("Tic Tac Toe");
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //System.out.println(Thread.currentThread().getName());
        launch(args);
        //System.out.println("a");
    }
}
