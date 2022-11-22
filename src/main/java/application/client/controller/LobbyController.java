package application.client.controller;

import application.action.*;
import application.bean.PositionBean;
import application.bean.State;
import application.bean.TYPE;
import application.bean.UserInfoBean;
import application.client.Main;
import application.server.Util.BiMap;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class LobbyController extends Controller{
    @FXML
    private Circle myCircle;
    @FXML
    private Circle rivalCircle;
    @FXML
    private ListView<UserInfoBean> listView;
    private ObservableList<UserInfoBean> observableList;
    @FXML private ImageView img1_1;@FXML private ImageView img1_2;@FXML private ImageView img1_3;
    @FXML private ImageView img2_1;@FXML private ImageView img2_2;@FXML private ImageView img2_3;
    @FXML private ImageView img3_1;@FXML private ImageView img3_2;@FXML private ImageView img3_3;
    @FXML
    private Button refresh;
    @FXML
    private Button match;
    @FXML
    private Text myName;
    @FXML
    private Text rivalName;
    @FXML
    private Text myTotal;
    @FXML
    private Text myWin;
    @FXML
    private Text myLose;
    @FXML
    private Text myTie;
    @FXML
    private Text rivalTotal;
    @FXML
    private Text rivalWin;
    @FXML
    private Text rivalLose;
    @FXML
    private Text rivalTie;
    @FXML
    private Label renew;
    @FXML
    private Label clock;
    @FXML
    private Pane side;

    @FXML
    private ImageView myChess;
    @FXML
    private Label windowClose;

    private double xOffset = 0;
    private double yOffset = 0;

    private final GameService gameService;

    private Logger log = Logger.getLogger(Main.class);

    public LobbyController(){
        gameService = new GameService();
        SocketManager.setGameService(gameService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windowClose.setOnMouseClicked(event ->
        {                SocketManager.close();
            log.info("Click windowClose label");
            SocketManager.close();
            Platform.exit();
        });

        mapInit();
        refresh.setVisible(false);


        img1_1.setOnMouseClicked(this::play);
        img1_2.setOnMouseClicked(this::play);
        img1_3.setOnMouseClicked(this::play);
        img2_1.setOnMouseClicked(this::play);
        img2_2.setOnMouseClicked(this::play);
        img2_3.setOnMouseClicked(this::play);
        img3_1.setOnMouseClicked(this::play);
        img3_2.setOnMouseClicked(this::play);
        img3_3.setOnMouseClicked(this::play);

        setListViewFactory();
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);

        refresh.setOnMouseClicked(this::refreshChessBoard);
        match.setOnMouseClicked(event -> gameService.matching());

        renew.setOnMouseClicked(this::renewList);
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
        Platform.runLater(this::laterNewText);
    }

    private void refreshChessBoard(MouseEvent event) {
        for(Map.Entry<ImageView, PositionBean> entry : map.entrySet()) {
            entry.getKey().setImage(null);
        }

//        for (int i = 0; i < chessBoard.length; i++) {
//            for (int j = 0; j < chessBoard[1].length; j++) {
//                chessBoard[i][j] = false;
//            }
//        }
    }
    private void renewList(MouseEvent event) {
        if (!SocketManager.isIsAlive()) {
            log.info("Login in again");
            if (!SocketManager.initSocket()) {
                SocketManager.serverClose();
                return;
            }
            SocketManager.loginAgain();
            SocketManager.starToListen();
        }
        SocketManager.pullUserInfoListAction();
        if (!SocketManager.initSocket()) {
            SocketManager.serverClose();
        }
    }
    private void updateListView(List<UserInfoBean> list){
        Platform.runLater(() -> {
            log.info("Renew ListView");
            // clear and set
            observableList.setAll(list);
        });
    }

    private void setListViewFactory() {
        listView.setCellFactory(lv -> {
            ListCell<UserInfoBean> cell = new ListCell<UserInfoBean>() {
                @Override
                public void updateItem(UserInfoBean item, boolean empty) {
                    super.updateItem(item, empty);

                    // must be, otherwise the item deleted can not update
                    if (empty && item == null) {
                        setGraphic(null);
                        setText(null);
                    }

                    if (item != null) {
                        HBox hBox = new HBox(10);
                        //setText(item.toString());
                        Button button = new Button();
                        button.setText("Engage");
                        button.setFont(new Font("Consolas", 12));
                        //button.setOnMouseClicked((e) -> System.out.println("Invite: " + item.getUserName()));

                        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //gameService.invited(new InviteAction(item));
                                //gameService.failInvitation(new FailureInvitationAction(new FailureBean("YOU CAN NOT INVITE YOURSELF")));
                                gameService.invite(item);
                            }
                        });

                        Label text = new Label();
                        text.setText(item.toString());
                        text.setFont(new Font("Consolas", 15));



                        AnchorPane apLeft = new AnchorPane();
                        HBox.setHgrow(apLeft, Priority.ALWAYS);//Make AnchorPane apLeft grow horizontally
                        AnchorPane apRight = new AnchorPane();

                        hBox.getChildren().add(apLeft);
                        hBox.getChildren().add(apRight);

                        apLeft.getChildren().add(text);
                        apRight.getChildren().add(button);

                        hBox.setMaxHeight(25);
                        hBox.setMaxWidth(220);
                        this.setPrefHeight(25);
                        this.setPrefWidth(220);
                        this.setGraphic(hBox);

                        this.setContentDisplay(ContentDisplay.RIGHT);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                UserInfoBean item = cell.getItem();
                if (item != null) {
                    System.out.println(item);
                }
            });
            return cell ;
        });
    }
    private void laterNewText() {
        UserInfoBean userInfoBean = sceneManager.getUserInfoBean();
        myName.setText(userInfoBean.getUserName());
        myTotal.setText(userInfoBean.getTotal() + "");
        myWin.setText(userInfoBean.getWin() + "");
        myLose.setText(userInfoBean.getLose() + "");
        myTie.setText(userInfoBean.getTie() + "");
        myCircle.setFill(new ImagePattern(new Image("img/user_blue_64.png")));
        renew.setGraphic(new ImageView(new Image("img/renew.png")));
    }

    private void cancleSelectedItem() {
        int index = listView.getSelectionModel().getSelectedIndex();
        System.out.println("Platform.later():" + Thread.currentThread().getName() + "  || " + index);
        try {
            listView.getSelectionModel().clearSelection(index);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Some wrong things");
        }
    }



    public void play(MouseEvent event) {
        if (!gameService.hasGaming) {
            return;
        }

        ImageView imageView = (ImageView) event.getTarget();
        PositionBean positionBean = map.getByKey(imageView);
        SocketManager.playAction(positionBean);

        if (!SocketManager.isIsAlive()) {
            SocketManager.serverClose();
            gameService.hasGaming = false;
            log.info("Here server occurs some wrong!");
            return;
        }
        //System.out.println(imageView);
//        if (playImageHereWithTest(imageView)) {
//            if (myTYPE == TYPE.CIRCLE) {
//                imageView.setImage(new Image("img/circle128.png"));
//                myTYPE = TYPE.CROSS;
//            } else {
//                imageView.setImage(new Image("img/cross128.png"));
//                myTYPE = TYPE.CIRCLE;
//            }
//        }
    }


//    private boolean isStart = false;
//
//    TYPE myTYPE = TYPE.CIRCLE;
//    boolean[][] chessBoard = new boolean[3][3];

    BiMap<ImageView, PositionBean> map = new BiMap<>(HashMap::new);

    private void mapInit() {
        map.put(img1_1, new PositionBean(0, 0, gameService.myTurn));
        map.put(img1_2, new PositionBean(0, 1, gameService.myTurn));
        map.put(img1_3, new PositionBean(0, 2, gameService.myTurn));
        map.put(img2_1, new PositionBean(1, 0, gameService.myTurn));
        map.put(img2_2, new PositionBean(1, 1, gameService.myTurn));
        map.put(img2_3, new PositionBean(1, 2, gameService.myTurn));
        map.put(img3_1, new PositionBean(2, 0, gameService.myTurn));
        map.put(img3_2, new PositionBean(2, 1, gameService.myTurn));
        map.put(img3_3, new PositionBean(2, 2, gameService.myTurn));
    }

//    private boolean checkPotionValid(PostionBean postion) {
//        return chessBoard[postion.getR()-1][postion.getC()-1];
//    }
//
//    private boolean playImageHereWithTest(ImageView image) {
//        PostionBean postion = map.getByKey(image);
//        if (!checkPotionValid(postion)) {
//            chessBoard[postion.getR()-1][postion.getC()-1] = true;
//            return true;
//        } else {
//            return false;
//        }
//    }

//    private void dispose(Action action) {
//        gameService.dispose(action);
//    }

    class GameService {
        boolean hasInviting = false;
        boolean hasGaming = false;
        boolean matching = false;
        private Thread clockThread = null;

        private TYPE myTurn;

        private int clockTime;
        public void dispose(Action action){
            if (action instanceof InviteAction) {
                invited((InviteAction) action);
            } else if (action instanceof FailureInvitationAction) {
                failInvitation((FailureInvitationAction) action);
            }
        }

        public void invite(UserInfoBean userInfoBean){
            if (hasGaming) {
                return;
            }

            if (matching) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("You should cancel matching before inviting someone");
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                ok.setText("OK");
                alert.show();
                return;
            }

            if (hasInviting) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("You are inviting someone, please wait");
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                ok.setText("OK");
                alert.show();
                return;
            }

            hasInviting = true;
            SocketManager.inviteAction(userInfoBean);
            // if the server is closed
            if (!SocketManager.isIsAlive()) {
                SocketManager.serverClose();
                hasInviting = false;
                return;
            }


            // after 15s no reply, can invite more.
            clockTime = 15;
            clockThread = new Thread(this::clock);
            clockThread.setName("ClockThread");
            clockThread.start();
        }

        private void clock(){
            log.info("The clock Thread starts");
            int cnt = clockTime;
            Platform.runLater(() -> {
                {
                    clock.setText(15 + "");
                    clock.setVisible(true);}
            });
            while (cnt >= 0 && !Thread.currentThread().isInterrupted()) {
                cnt--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    clock.setVisible(false);
                    // when a sleeping thread is interrupted, the isInterrupted becomes false;
                    Thread.currentThread().interrupt();
                    //e.printStackTrace();
                }
                int finalCnt = cnt;
                Platform.runLater(() -> clock.setText(finalCnt + ""));

                // clock to zero notice you can invite again
                if (cnt == 0) {
                    Platform.runLater(() -> {
                        clock.setVisible(false);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText("Invitation expired");
                        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        ok.setText("OK");
                        alert.show();
                    });
                }
            }
            hasInviting = false;
            log.info("The clock Thread ends");
        }

        void invited(InviteAction action) {
            UserInfoBean userInfoBean = action.who();

            // if someone playing, ignore the invitation
            if (hasGaming) {
                return;
            }

            Platform.runLater(() -> {
                // invitationAlert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("New Invitation Letter");
                alert.setHeaderText("[" + userInfoBean.getUserName() +" ] invite you to play");
                alert.setContentText("Click 'OK' to accept, 'Cancel' to reject");
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);

                // if receive when matching


                ok.setText("OK");
                ok.setOnAction((event) -> {
                    log.info("Accept invitation");
                    // send reply
                    // TODO: setThe match and engage buttons invalid
                    SocketManager.inviteAction(userInfoBean);
                    if (matching) {
                        matching();
                    }
                });
                Button cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                // setOnMouseClicked seems not work
                cancel.setText("Cancel");
                cancel.setOnAction(event -> {
                    log.info("Reject invitation");
                });
                alert.show();
            });
            hasInviting = false;
            //sceneManager.getRoot().getChildren().add(btn1);
        }

        void failInvitation(FailureInvitationAction action) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(action.getFailureBean().getFailInfo());
                    alert.show();
                }
            });
            try {
                clockThread.interrupt();
                log.debug("Interrupt clockThread");
                hasInviting = false;
            } catch (Exception e) {
                log.debug("Interrupt clockThread fails");
                //e.printStackTrace();
            }
        }

        void listReply(UserInfoListReplyAction action) {
            updateListView(action.getList());
        }

        void matching() {
            if (hasGaming) {
                return;
            }


            // if you are inviting someone
            if (hasInviting) {
                Platform.runLater(() -> {
                    // invitationAlert
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Suspension");
                    alert.setHeaderText("You are inviting someone");
                    alert.setContentText("Click 'OK' to accept");
                    Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                    ok.setText("OK");
                    ok.setOnAction((event) -> {
                        log.info("You are inviting someone");
                    });
                    alert.show();
                });
            } else {

                // set button according different schedule
                if (matching) {
                    matching = false;
                    Platform.runLater(() -> {
                        match.setText("Match");
                    });
                } else {
                    matching = true;
                    Platform.runLater(() -> {
                        match.setText("Cancel");
                    });
                }
                log.info("matching again");
                SocketManager.matchAction(sceneManager.getUserInfoBean());
                if (!SocketManager.isIsAlive()) {
                    SocketManager.serverClose();
                    hasInviting = false;
                    return;
                }
            }
        }

        void init(GameInitAction gameInitAction) {
            for(Map.Entry<ImageView, PositionBean> entry : map.entrySet()) {
                entry.getKey().setImage(null);
            }

            hasGaming = true;
            matching = false;
            Platform.runLater(() -> {
                match.setText("Match");
            });
            hasInviting = false;
            while (clockThread != null  && !clockThread.isInterrupted()) {
                try {
                    clockThread.interrupt();
                    log.debug("Interrupt clockThread");
                    hasInviting = false;
                    clockThread = null;
                } catch (Exception e) {
                    log.debug("Interrupt clockThread fails");
                    //e.printStackTrace();
                }
            }

            UserInfoBean opponent = gameInitAction.getOpponent();
            myTurn = gameInitAction.getTurn();
            Platform.runLater(() -> {
                setEnemy(opponent);
                if (myTurn == TYPE.CIRCLE) {
                    myChess.setImage(new Image("img/circle128.png"));

                } else {
                    myChess.setImage(new Image("img/cross128.png"));

                }
            });
//            img1_1.setOnMouseClicked(this::play);
//            img1_2.setOnMouseClicked(this::play);
//            img1_3.setOnMouseClicked(this::play);
//            img2_1.setOnMouseClicked(this::play);
//            img2_2.setOnMouseClicked(this::play);
//            img2_3.setOnMouseClicked(this::play);
//            img3_1.setOnMouseClicked(this::play);
//            img3_2.setOnMouseClicked(this::play);
//            img3_3.setOnMouseClicked(this::play);
            for(Map.Entry<ImageView, PositionBean> entry : map.entrySet()) {
                entry.getValue().setType(myTurn);
            }
        }


        private void setEnemy(UserInfoBean userInfoBean) {
            rivalName.setText(userInfoBean.getUserName());
            rivalTotal.setText(userInfoBean.getTotal() + "");
            rivalWin.setText(userInfoBean.getWin() + "");
            rivalLose.setText(userInfoBean.getLose() + "");
            rivalTie.setText(userInfoBean.getTie() + "");
            rivalCircle.setFill(new ImagePattern(new Image("img/user_red_64.png")));
        }


        public void put(PlayReplyAction action) {
            log.info("Put chess");
            PositionBean position = action.getPositionBean();
            State state = action.getState();
            switch (state) {
                case WIN:
                    Platform.runLater(() -> {
                        setImage(position);
                        myWin.setText(Integer.parseInt(myWin.getText()) + 1 + "");
                        myTotal.setText(Integer.parseInt(myTotal.getText()) + 1 + "");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText(myName.getText() + " Win!");
                        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        ok.setText("OK");
                        alert.show();
                    });
                    hasGaming = false;
                    break;
                case LOSE:
                    Platform.runLater(() -> {
                        setImage(position);
                        myLose.setText(Integer.parseInt(myLose.getText()) + 1 + "");
                        myTotal.setText(Integer.parseInt(myTotal.getText()) + 1 + "");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText(myName.getText() + " Lose!");
                        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        ok.setText("OK");
                        alert.show();
                        hasGaming = false;
                    });
                    break;
                case DRAW:
                    Platform.runLater(() -> {
                        myTie.setText(Integer.parseInt(myTie.getText()) + 1 + "");
                        myTotal.setText(Integer.parseInt(myTotal.getText()) + 1 + "");
                        setImage(position);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText("Draw!");
                        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        ok.setText("OK");
                        alert.show();
                        hasGaming = false;
                    });
                    break;
                case LOSS_WIN:
                    Platform.runLater(() -> {
                        myWin.setText(Integer.parseInt(myWin.getText()) + 1 + "");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText("The opponent run away, " + myName.getText() + " win!");
                        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        ok.setText("OK");
                        alert.show();
                        hasGaming = false;
                    });
                    break;
                case UNDEFINED:
                    Platform.runLater(() -> {setImage(position);});
                    break;
                case INVALID:

                    break;
                default:
                    break;
            }
        }

        private void setImage(PositionBean position) {
            ImageView imageView = map.getByValue(position);
            if (position.getType() == TYPE.CIRCLE) {
                imageView.setImage(new Image("img/circle128.png"));

            } else {
                imageView.setImage(new Image("img/cross128.png"));

            }
        }
    }
}
