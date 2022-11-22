package application.client.controller;

import application.action.*;
import application.bean.LoginBean;
import application.bean.RegisterBean;
import application.bean.UserInfoBean;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketManager {
    private static Socket socket;
    private static final String HOST = "localhost";
    private static final int PORT = 12324;
    private static BlockingQueue<Action> sendQueue;

    private static Thread sendThread;

    private static Thread listenThread;

    public static void setGameService(LobbyController.GameService gameService) {
        SocketManager.gameService = gameService;
    }

    private static LobbyController.GameService gameService;

    private static final long DELAY = 200;
    private static final long WAITING_TIME = 5000;


    private static Logger log = Logger.getLogger(Math.class);
    public static boolean initSocket(){
        if (socket != null && !socket.isClosed()) {
            return true;
        }
        try {
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout((int) WAITING_TIME);
            sendQueue = new ArrayBlockingQueue<>(10, true);
            sendThread = new Thread(new SenderTask());
            sendThread.setName("SenderThread");
            sendThread.start();
            listenThread = null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void starToListen() {
        if (listenThread == null || listenThread.isInterrupted()) {
            listenThread = new Thread(new  ListenerTask());
            listenThread.setName("ListenThread");
            listenThread.start();
            log.trace("here");
        }
    }
    public static void close() {
        exceptionHandle();
    }
    public static void pullUserInfoListAction() {
        Action pullAction = new PullUsersAction();
        sendQueue.add(pullAction);
//        UserInfoListReplyAction userList;
//        try {
//            userList = (UserInfoListReplyAction) SocketManager.receive();
//        } catch (IOException | ClassNotFoundException e) {
//            log.warn("PullUsersAction request fails");
//            return Optional.empty();
//        }
//        assert userList != null;
//        List<UserInfoBean> list = userList.getList();
//        return Optional.of(list);
    }
    public static Optional<UserInfoBean> registerAction(RegisterBean registerBean) {
        Action registerAction = new RegisterAction(registerBean);
        sendQueue.add(registerAction);
        Action receiveAction;
        try {
            receiveAction = receive();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Register receive none Object, switch to exception handle");
            exceptionHandle();
            return Optional.empty();
        }
        // if reply.isValid == false means Username or password is wrong
        if (receiveAction instanceof UserInfoReplyAction) {
            UserInfoReplyAction reply = (UserInfoReplyAction) receiveAction;
            return Optional.of(reply.getUserInfoBean());
        } else {
            FailureAction replay = (FailureAction) receiveAction;
            System.out.println(replay.getFailureBean().getFailInfo());
            //System.out.println(replay.getFailureBean().getFailInfo());

            // need to disconnect
            exceptionHandle();
            return Optional.of(new UserInfoBean());
        }
    }
    public static Optional<UserInfoBean> loginAction(LoginBean loginBean) {
        Action loginAction = new LoginAction(loginBean);
        sendQueue.add(loginAction);
        Action receiveAction;
        try {
            receiveAction = receive();
        } catch (IOException | ClassNotFoundException e) {
            // when a exception occurs, the server crashed
            exceptionHandle();
            return Optional.empty();
        }

        // if reply.isValid == false means Username or password is wrong
        if (receiveAction instanceof UserInfoReplyAction) {
            UserInfoReplyAction reply = (UserInfoReplyAction) receiveAction;
            return Optional.of(reply.getUserInfoBean());
        } else {
            FailureAction replay = (FailureAction) receiveAction;
            System.out.println(replay.getFailureBean().getFailInfo());
            //System.out.println(replay.getFailureBean().getFailInfo());

            // need to disconnect
            exceptionHandle();
            return Optional.of(new UserInfoBean());
        }
    }

    public static void inviteAction(UserInfoBean userInfoBean) {
        Action action = new InviteAction(userInfoBean);
        sendQueue.add(action);
    }
    private static Action receive() throws IOException, ClassNotFoundException {

        // TODO: when the server crash
        // throw IOException
        InputStream is = socket.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));

        // throw ClassNotFoundException
        return (Action) ois.readObject();
    }

    private static void exceptionHandle() {
        try {
            if (listenThread != null) {
                listenThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (sendThread != null) {
                sendThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (socket != null) {
                socket.close();
                log.debug("socket close");
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private class SenderTask implements Runnable {
        @Override
        public void run() {
            Action action;
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    action = sendQueue.remove();
                } catch (NoSuchElementException e) {
                    action = new KeepLivingAction();
                }


                send(action);
                // System.out.println("send " + action);
                //TODO: when the server crash
                try {
                    // sleep method throws a exception when receive an interrupt, and the flag will be set false.
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    log.warn("Sleep fails");
                    exceptionHandle();
                    //System.out.println(Thread.currentThread().isInterrupted());
                    //e.printStackTrace();
                }
            }
            log.debug("This thread ends");
        }


        private void send(Action action) {
            if (!(action instanceof KeepLivingAction)) {
                System.out.println("send " + action);
            }
            try {
                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
                oos.writeObject(action);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
                log.debug("Send fails");
                exceptionHandle();
            }
        }
    }

    static private class ListenerTask implements Runnable{
        @Override
        public void run() {
            log.info("Login success, listen to server action");

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    InputStream os = socket.getInputStream();
                    ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(os));
                    Action action = (Action) oos.readObject();
                    log.info("Receive: " + action);

                    // invitation fails or invitation
                    if (action instanceof FailureInvitationAction || action instanceof InviteAction) {
                        gameService.dispose(action);
                    } else if (action instanceof UserInfoListReplyAction){
                        // when receive a ListView List
                        gameService.listReply((UserInfoListReplyAction) action);
                    } else {
                        log.warn(action);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    log.trace("Nothing to read");
                    try {
                        // sleep method throws a exception when receive an interrupt, and the flag will be set false.
                        Thread.sleep(DELAY);
                    } catch (InterruptedException ee) {
                        log.debug("Sleep fails");
                        exceptionHandle();
                        //System.out.println(Thread.currentThread().isInterrupted());
                        ee.printStackTrace();
                    }
                }
            }
        }
    }
}

