package application.server.servlet;

import application.action.Action;
import application.action.GameInitAction;
import application.action.MatchAction;
import application.bean.UserInfoBean;
import application.server.ServerUserProfile;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MatchServlet implements Servlet{
    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {
        MatchAction matchAction = (MatchAction) action;
        UserInfoBean userInfoBean = matchAction.getUserInfoBean();

        ReadWriteLock lock = new ReentrantReadWriteLock();

        lock.writeLock().lock();
        Optional<UserInfoBean> optional = profile.pollMatchQueue();
        if (optional.isPresent()) {
            // TODO: if someone send join two times,
            if (userInfoBean.equals(optional.get())) {
                log.debug(userInfoBean + " Leave matching queue");
                profile.removeFromMatchQueue(userInfoBean);
            } else {
                try {
                    Socket opponent = profile.getSocketByUser(optional.get()).get();
                    if (profile.newRoom(socket, opponent)) {
                        try {
                            ServerUserProfile.Room room = profile.getRoom(socket).get();
                            send(new GameInitAction(room.who(socket), profile.getUserBySocket(opponent).get()), socket);
                            send(new GameInitAction(room.who(opponent), userInfoBean), opponent);
                        } catch (IOException e) {
                            // throw new RuntimeException(e);
                            // some wrong
                            // handleException(s);
                            // here we do not handle the exception, in GameServlet we will handle it
                            // return;
                        }


                        log.info("A new game start!");
                    } else {
                        log.error("Unknown error!");
                    }
                } catch (Exception e) {
                    log.error("Here is no opponent");
                }


            }

        } else {
            // no user in match
            log.debug(userInfoBean + " Enter matching queue.");
            profile.addMatchQueue(userInfoBean);
        }
        lock.writeLock().unlock();

    }

    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {

    }
}
