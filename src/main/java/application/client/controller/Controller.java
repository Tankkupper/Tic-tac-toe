package application.client.controller;

import javafx.fxml.Initializable;

public abstract class Controller implements Initializable {
    SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) { // if SceneManager and BaseController are in different packages, change visibility
        this.sceneManager = sceneManager;
    }
}
