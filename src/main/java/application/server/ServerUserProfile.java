package application.server;

import application.bean.UserInfoBean;
import application.server.Util.BiMap;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerUserProfile {
    // add in Server.java when a new connection coming, a new Thread start()
    private Map<Socket, Thread> socketThreadMap;

    // add in Servlet when a new login in, lobby
    private BiMap<Socket, UserInfoBean> lobbySocketWithUser;

    // first socket is inviter second socket is invitee
    private BiMap<Socket, Socket> invitation;
    private Logger log;

    private Random random;
    class Room{
        private Socket circle;
        private Socket cross;

        private int[][] chessboard;

        public Room(Socket a, Socket b){
            if (random.nextBoolean()) {
                circle = a;
                cross = b;
            } else {
                circle = b;
                cross = a;
            }
            chessboard = new int[3][3];
        }
    }
//    //读写锁
//    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
//    //获取写锁
//    private final Lock wlock = rwLock.writeLock();
//    //获取读锁
//    private final Lock rLock = rwLock.readLock();

    public ServerUserProfile() {
        socketThreadMap = new ConcurrentHashMap<>();
        lobbySocketWithUser = new BiMap<>(HashMap::new);
        log = Logger.getLogger(Server.class);
        invitation = new BiMap<>(HashMap::new);
        random = new Random();
    }

    // return a related inviter if exists
    public Socket checkInvited(Socket socket) {
        return invitation.getByValue(socket);
    }

    public void putNewInvitation(Socket inviter, Socket invitee) {
        invitation.put(inviter, invitee);
    }

    public void InvitationExpire(Socket socket) {
        invitation.remove(socket, socket);
    }

    public void putSocketThreadMap(Socket socket, Thread userThread){
        socketThreadMap.put(socket, userThread);
    }

    public Optional<UserInfoBean> getUserBySocket(Socket socket) {
        return Optional.ofNullable(lobbySocketWithUser.getByKey(socket));
    }

    public Optional<Socket> getSocketByUser(UserInfoBean userInfoBean) {
        return Optional.ofNullable(lobbySocketWithUser.getByValue(userInfoBean));
    }
    public Thread removeSocketThreadMap(Socket socket){
        return socketThreadMap.remove(socket);
    }

    public void putSocketUserInfoMap(Socket socket, UserInfoBean userInfoBean){
        lobbySocketWithUser.put(socket, userInfoBean);
    }

    public void removeSocketUserInfoMap(Socket socket){
        lobbySocketWithUser.removeByKey(socket);
    }

    public Set<UserInfoBean> lobbyUserSet() {
        return lobbySocketWithUser.getValueSet();
    }

    public void shotdown(Socket socket){
        try {
            socket.close();
            log.info("Close " + socket);
        } catch (Exception e) {
            log.warn(socket + " had been closed");
        }
    }
    public void timeout(Socket socket) {
        //TODO:
        // remove socket to thread
        log.trace("Here");
        Optional<Thread> thread = Optional.ofNullable(removeSocketThreadMap(socket));
        log.trace("Here");
        thread.ifPresent(Thread::interrupt);
        log.trace("Here");
        // remove socket to user, if not login, it does not matter
        removeSocketUserInfoMap(socket);
        log.trace("Here");
        shotdown(socket);
        log.warn(socket + " timeout");
    }
}
