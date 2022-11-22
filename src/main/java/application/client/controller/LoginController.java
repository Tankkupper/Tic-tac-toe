package application.client.controller;

import application.bean.LoginBean;
import application.bean.UserInfoBean;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginController extends Controller implements Initializable {
    @FXML
    private Label windowClose;
    @FXML
    private Circle headCircle;
    @FXML
    private Button login;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Tooltip loginTip;
    @FXML
    private Text register;
    @FXML
    private Pane side;
    @FXML
    private Pane root;

    private double xOffset = 0;
    private double yOffset = 0;
    private Logger log = Logger.getLogger(Math.class);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowClose.setOnMouseClicked(event ->
        {                SocketManager.close();
            log.info("Click windowClose label");
            SocketManager.close();
            Platform.exit();
        });
        login.setOnMouseClicked(event -> {
            System.out.println("click login button");
        });
        Image img = new Image("/img/user-circle.jpg");
        headCircle.setFill(new ImagePattern(img));
        // let username TextField get focus
        Platform.runLater(() -> username.requestFocus());
        username.textProperty().addListener(this::usernameListener);


        Tooltip.uninstall(login, loginTip);
        loginTip.hide();
        loginTip.setAutoHide(true);
        loginTip.setGraphic(new ImageView(new Image("img/warn.png")));
        login.setOnMouseClicked(this::loginHandler);
        //login.setOnAction(e -> System.out.println("Action"));

        register.setOnMouseClicked((event) -> sceneManager.switchScene("registerUI.fxml"));



        side.setOnMouseDragged(event -> {
            double x = event.getScreenX();
            double y = event.getScreenY();
            Stage primaryStage = sceneManager.getRootStage();
            double nextX = x - xOffset;
            double nextY = y - yOffset;

            primaryStage.setX(nextX);
            primaryStage.setY(nextY);

        });
        side.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            //System.out.println(xOffset + "   " + yOffset);
        });

//        root.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });

//        KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
//        Platform.runLater(() -> sceneManager.getScene("loginUI.fxml").getAccelerators().put(
//                enter, () -> {
//                    //login.requestFocus();
//                    login.fire();
//                    login.isPressed();
//                }));//.getAccelerators().put(enter, () -> login.onMouseClickedProperty());
        // wrong code this time there is no sceneManager
//        Scene scene = sceneManager.getScene("loginUI.fxml");
//                scene.getAccelerators().put(enter, () -> {
//            login.onMouseClickedProperty();
//            System.out.println("Enter");
//        });
    }


    private void usernameListener(ObservableValue<? extends String> observable, String oldValue, String newValue){
        if (newValue.length() <= oldValue.length()) {
            return;
        }
        if(newValue.length()>10) {
            System.out.println("Too much character");
            username.setText(oldValue);
        } else if (!Character.isDigit(newValue.charAt(newValue.length()-1))
                && !Character.isLetter(newValue.charAt(newValue.length()-1))){
            System.out.println("The field only can contain letter and number");
            username.setText(oldValue);
        }
    }

    AtomicBoolean isConnecting = new AtomicBoolean(false);
    private void loginHandler(MouseEvent event) {

        String user = username.getText();
        String pass = password.getText();
        if (isConnecting.get()) {
            return;
        }
        if (user == null || user.equals("")) {
            loginTip.setText("The username can not be empty");
            Platform.runLater(() -> loginTip.show(sceneManager.getRootStage()));
        } else if (pass == null || pass.equals("")) {
            loginTip.setText("The password can not be empty");
            Platform.runLater(() -> loginTip.show(sceneManager.getRootStage()));
        } else {
            if (!SocketManager.initSocket()) {
                loginTip.setText("Abnormal server status");
                Platform.runLater(() -> loginTip.show(sceneManager.getRootStage()));
            } else {
                Optional<UserInfoBean> optional = SocketManager.loginAction(new LoginBean(user, pass));
                if (!optional.isPresent()) {
                    loginTip.setText("Abnormal server status");
                    Platform.runLater(() -> loginTip.show(sceneManager.getRootStage()));
                } else {
                    UserInfoBean userInfoBean = optional.get();
                    if (!userInfoBean.isValid()) {
                        loginTip.setText("Wrong username or password");
                        Platform.runLater(() -> loginTip.show(sceneManager.getRootStage()));
                    } else {
                        // TODO: change scene
                        SocketManager.starToListen();
                        sceneManager.setUserInfoBean(userInfoBean);
                        sceneManager.switchScene("lobbyUI.fxml");
                        System.out.println("Success to login, receive " + userInfoBean);
                    }
                }
            }
        }
    }
}
