package application.server;


import application.action.*;
import application.server.servlet.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private final int port;
    private boolean running;
    private final long receiveTimeDelay;
    private Thread listenThread;
    private HashMap<Class<?>, Servlet> actionToServlet;
    private ServerUserProfile profile;
    private int cnt;
    private Logger log;
    public Server(int port, long receiveTimeDelay) {
        this.cnt = 0;
        this.port = port;
        this.receiveTimeDelay = receiveTimeDelay;
        this.running = false;
        this.profile = new ServerUserProfile();
        actionToServletInitialize();
        log = Logger.getLogger(Server.class);
    }

    private void actionToServletInitialize() {
        //TODO:
        actionToServlet = new HashMap<>();
        actionToServlet.put(LoginAction.class, new LoginServlet());
        actionToServlet.put(RegisterAction.class, new RegisterServlet());
        actionToServlet.put(PullUsersAction.class, new PullUsersServlet());
        actionToServlet.put(InviteAction.class, new InvitationServlet());
    }

    public void launch() {
        if (running) {
            log.info("The server had launched");
            return;
        }

        //AtomicReference<ServerSocket> t = new AtomicReference<>();
        running = true;
        listenThread = new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port, 10)) {
                log.info("The server has launched!");
                // t.set(serverSocket);
                while (running && !Thread.currentThread().isInterrupted()) {
                    Socket inputSocket = serverSocket.accept();

                    // Set the I/O read/write timeout period
                    inputSocket.setSoTimeout((int) receiveTimeDelay);
                    log.info("A new connection, and a userThread starts");

                    // The following 3 lines code does not raise an exception
                    Thread userThread = new Thread(new UserThreadTask(inputSocket, receiveTimeDelay));
                    userThread.setName("UserTaskThread["+cnt+"]");
                    cnt++;
                    // contain socket to thread
                    profile.putSocketThreadMap(inputSocket, userThread);
                    userThread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        listenThread.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        listenThread.interrupt();
//        try {
//            t.get().close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        running = false;
//        System.out.println("close the server");
    }

    public void stop(){
        if (running){
            running = false;
        }
        if (listenThread != null) {
            listenThread.interrupt();
        }
    }


    private class UserThreadTask implements Runnable{
        private final Socket socket;
        private long lastReceiveTime;
        private final long receiveTimeDelay;

        public UserThreadTask(Socket socket, long receiveTimeDelay) {
            this.socket = socket;
            this.lastReceiveTime = System.currentTimeMillis();
            this.receiveTimeDelay = receiveTimeDelay;
        }

        @Override
        public void run() {
            while (Server.this.running && !Thread.currentThread().isInterrupted()) {
                //TODO:当Server crash/shutdown 的时候应当优雅的告诉用户服务器维护中
                if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
                    profile.timeout(socket);
                    //TODO: 超时应当加入等待队列 等用户重新连接
                    break;
                }
                try{
                    Action action = receive();
                    lastReceiveTime = System.currentTimeMillis();

                    // dispose the action, keeping living will get null
                    Servlet servlet = actionToServlet.get(action.getClass());
                    //System.out.println("Receive " + action);
                    if (servlet != null) {
                        log.info("Receive " + action);
                        // new a Thread to dispose this action, prevents the current reader thread from being blocked
                        Thread servletThread = new Thread(() -> servlet.dispose(action, socket, profile));
                        servletThread.setName("ServletThread");
                        servletThread.start();
                    }

                } catch (Exception e) {
                    log.warn("The socket seems to be closed");
                    //TODO： 此处应当有异常处理, 不同阶段处理也会不一样, 可能
                    try {
                        profile.timeout(socket);
                    }catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    break;
                }
            }
            log.warn("A UserThread ends");
        }

        private Action receive() throws IOException, ClassNotFoundException {
            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            return (Action) ois.readObject();
        }

    }


    public static void main(String[] args) {
        Server server = new Server(12324, 5000);
        server.launch();
    }
}
