package application.client.controller;

import application.bean.UserInfoBean;
import application.client.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SceneManager {
    private final Stage rootStage;

    private UserInfoBean userInfoBean;
    public SceneManager(Stage rootStage) {
        if (rootStage == null) {
            throw new IllegalArgumentException();
        }
        this.rootStage = rootStage;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    private final Map<String, Scene> scenes = new ConcurrentHashMap<>();

    public Pane getRoot() {
        return (Pane) rootStage.getScene().getRoot();
    }
    public Scene getScene(String url) {
        // System.out.println(Thread.currentThread().getName());
        // System.out.println(scenes.containsKey(url));
        Scene scene = scenes.computeIfAbsent(url, u -> {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource(u));
            try {

                Pane p = fxmlLoader.load();
                // all component in p can trigger this click event
                p.setOnMouseClicked(event -> {
                    if (event.getTarget() instanceof Pane) {
                        System.out.println(event.getTarget().getClass() + " get focus");
                        Pane p1 = (AnchorPane) event.getTarget();
                        p1.requestFocus();
                    }
                });
                Controller controller = fxmlLoader.getController();
                controller.setSceneManager(this);

                return new Scene(p);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        Main.class.getResource(
                                "/com/only/common/css/Only.css")).toString());
        return scene;
    }
    public void switchScene(String url) {
        Scene scene = getScene(url);
        rootStage.setScene(scene);
        rootStage.centerOnScreen();
    }

    public Stage getRootStage() {
        return rootStage;
    }
}
