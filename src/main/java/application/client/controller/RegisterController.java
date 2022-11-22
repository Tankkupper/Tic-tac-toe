package application.client.controller;

import application.bean.RegisterBean;
import application.bean.UserInfoBean;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterController extends Controller{
    @FXML
    private Label windowClose;
    @FXML
    private Circle headCircle;
    @FXML
    private Button register;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordAgain;
    @FXML
    private Tooltip registerTip;
    @FXML
    private Text login;
    @FXML
    private Pane side;
    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowClose.setOnMouseClicked(event ->
        {
            Platform.exit();
            System.out.println("click windowClose label");});
        register.setOnMouseClicked(event -> {
            System.out.println("click register button");
        });
        Image img = new Image("/img/user-circle.jpg");
        headCircle.setFill(new ImagePattern(img));
        Platform.runLater(() -> username.requestFocus());
        username.textProperty().addListener(this::usernameListener);

        Tooltip.uninstall(register, registerTip);
        registerTip.hide();
        registerTip.setAutoHide(true);
        registerTip.setGraphic(new ImageView(new Image("img/warn.png")));
        register.setOnMouseClicked(this::registerHandler);

        login.setOnMouseClicked(e -> sceneManager.switchScene("loginUI.fxml"));
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
    private void registerHandler(MouseEvent event) {

        String user = username.getText();
        String pass = password.getText();
        String passAgain = passwordAgain.getText();
        if (isConnecting.get()) {
            return;
        }
        isConnecting.set(true);
        if (user == null || user.equals("")) {
            registerTip.setText("The username can not be empty");
            Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
        } else if (user.length() > 5) {
            registerTip.setText("The constraint on username length is 5");
            Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
        } else if (pass == null || pass.equals("")) {
            registerTip.setText("The password can not be empty");
            Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
        } else if (passAgain == null || passAgain.equals("")) {
            registerTip.setText("Enter password again");
            Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
        } else if (!pass.equals(passAgain)){
            registerTip.setText("The passwords are not equal");
            Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
        } else {
            if (!SocketManager.initSocket()) {
                registerTip.setText("Abnormal server status:-1");
                Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
            } else {
                Optional<UserInfoBean> optional = SocketManager.registerAction(new RegisterBean(user, pass));
                if (!optional.isPresent()) {
                    registerTip.setText("Abnormal server status:-2");
                    Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
                } else {
                    UserInfoBean userInfoBean = optional.get();
                    if (!userInfoBean.isValid()) {
                        registerTip.setText("Illegal username or password");
                        Platform.runLater(() -> registerTip.show(sceneManager.getRootStage()));
                    } else {
                        // TODO: change scene
                        sceneManager.switchScene("loginUI.fxml");
                        System.out.println("Success to register, receive " + userInfoBean);
                    }
                }
            }
        }
        isConnecting.set(false);
    }
}
