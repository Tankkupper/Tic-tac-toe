package application.server;

import application.action.Action;
import application.action.PlayReplyAction;
import application.bean.PositionBean;
import application.bean.State;
import application.bean.TYPE;
import application.bean.UserInfoBean;
import application.server.Util.BiMap;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static application.bean.TYPE.*;

public class ServerUserProfile {
    // add in Server.java when a new connection coming, a new Thread start()
    private Map<Socket, Thread> socketThreadMap;

    // add in Servlet when a new login in, lobby
    private BiMap<Socket, UserInfoBean> lobbySocketWithUser;

    // first socket is inviter second socket is invitee
    private BiMap<Socket, Socket> invitation;

    private BlockingQueue<UserInfoBean> matchQueue;

    // store Game information
    private Map<Socket, Room> socketRoomMap;
    private Logger log;

    private Random random;

    public class Room {
        private Socket circle;
        private Socket cross;
        private TYPE[][] chessboard;

        private TYPE turn = CIRCLE;

        private int cnt = 0;

        public Room(Socket a, Socket b){
            if (random.nextBoolean()) {
                circle = a;
                cross = b;
            } else {
                circle = b;
                cross = a;
            }
            chessboard = new TYPE[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    chessboard[i][j] = EMPTY;
                }
            }
        }
        public TYPE who(Socket socket) {
            if (socket.equals(circle)) {
                return CIRCLE;
            }

            if (socket.equals(cross)) {
                return CROSS;
            }

            return EMPTY;
        }
        public boolean valid(Socket socket, PositionBean position) {
            if (who(socket).equals(turn)) {
                if (chessboard[position.getR()][position.getC()] == EMPTY) {
                    cnt++;
                    chessboard[position.getR()][position.getC()] = turn;
                    log.debug(Arrays.deepToString(chessboard));
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public Socket getOpponent(Socket socket) {
            if (cross.equals(socket)) {
                return circle;
            } else {
                return cross;
            }
        }

        public State play(Socket socket, PositionBean position) {
            if (!valid(socket, position)) {
                return State.INVALID;
            } else {
                if ((chessboard[0][0].equals(turn) && chessboard[0][1].equals(turn) && chessboard[0][2].equals(turn)) ||
                        (chessboard[1][0].equals(turn) && chessboard[1][1].equals(turn) && chessboard[1][2].equals(turn)) ||
                        (chessboard[2][0].equals(turn) && chessboard[2][1].equals(turn) && chessboard[2][2].equals(turn)) ||
                        (chessboard[0][0].equals(turn) && chessboard[1][0].equals(turn) && chessboard[2][0].equals(turn)) ||
                        (chessboard[0][1].equals(turn) && chessboard[1][1].equals(turn) && chessboard[2][1].equals(turn)) ||
                        (chessboard[0][2].equals(turn) && chessboard[1][2].equals(turn) && chessboard[2][2].equals(turn)) ||
                        (chessboard[0][0].equals(turn) && chessboard[1][1].equals(turn) && chessboard[2][2].equals(turn)) ||
                        (chessboard[2][0].equals(turn) && chessboard[1][1].equals(turn) && chessboard[0][2].equals(turn))

                ) {
                    reverse();
                    return State.WIN;
                } else {
                    if (cnt != 9) {
                        reverse();
                        return State.UNDEFINED;
                    } else {
                        reverse();
                        return State.DRAW;
                    }
                }
            }
        }


        private void reverse() {
            if (turn.equals(CIRCLE)) {
                turn = CROSS;
            } else {
                turn = CIRCLE;
            }
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Room room = (Room) o;
            return circle.equals(room.circle) && cross.equals(room.cross);
        }

        @Override
        public int hashCode() {
            return Objects.hash(circle, cross);
        }

        @Override
        public String toString() {
            return "Room{" +
                    "circle=" + circle +
                    ", cross=" + cross +
                    ", turn=" + turn +
                    ", cnt=" + cnt +
                    '}';
        }
    }

    public ServerUserProfile() {
        socketThreadMap = new ConcurrentHashMap<>();
        lobbySocketWithUser = new BiMap<>(HashMap::new);
        log = Logger.getLogger(Server.class);
        invitation = new BiMap<>(HashMap::new);
        random = new Random();
        matchQueue = new LinkedBlockingQueue<>();
        socketRoomMap = new ConcurrentHashMap<>();
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

    public void removeFromMatchQueue(UserInfoBean userInfoBean) {
        matchQueue.remove(userInfoBean);
    }

    public void addMatchQueue(UserInfoBean userInfoBean) {
        matchQueue.add(userInfoBean);
    }

    public Optional<UserInfoBean> pollMatchQueue() {
        return Optional.ofNullable(matchQueue.poll());
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

    public Optional<Room> getRoom(Socket socket) {
        return Optional.ofNullable(socketRoomMap.get(socket));
    }

    public Room removeRoom(Socket any) {
        Room room = socketRoomMap.get(any);
        if (room != null) {
            socketRoomMap.remove(room.circle);
            socketRoomMap.remove(room.cross);
        }
        return room;
    }
    public boolean newRoom(Socket a, Socket b) {
        if (socketRoomMap.containsKey(a) || socketRoomMap.containsKey(b)) {
            return false;
        }
        Room room = new Room(a, b);
        socketRoomMap.put(a, room);
        socketRoomMap.put(b, room);
        return true;
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
        shotdown(socket);
        log.warn(socket + " timeout");

        Room room = removeRoom(socket);
        if (room != null) {
            log.debug("A room is deleted " + room);
            Socket opponent = room.getOpponent(socket);
            try {
                send(new PlayReplyAction(new PositionBean(-1, -1, EMPTY), State.LOSS_WIN), socket);
                log.debug("send to " + socket);
                // if success means opponent lost
                endGame(socket, State.WIN, opponent, State.LOSE, this);
            } catch (IOException ex) {
                try {
                    send(new PlayReplyAction(new PositionBean(-1, -1, EMPTY), State.LOSS_WIN), opponent);
                    log.debug("send to " + socket);
                    endGame(socket, State.LOSE, opponent, State.WIN, this);
                } catch (IOException exception) {
                    // all lost game draws

                    endGame(socket, State.DRAW, opponent, State.DRAW, this);
                }
            }
        }
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
    }

    private void endGame(Socket aa, State sa, Socket bb, State sb, ServerUserProfile profile) {
        // TODO:
    }
    void send(Action action, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                        socket.getOutputStream()));
        oos.writeObject(action);
        oos.flush();
    }
}
