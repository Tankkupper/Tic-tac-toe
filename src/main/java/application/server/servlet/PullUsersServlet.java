package application.server.servlet;

import application.action.Action;
import application.action.UserInfoListReplyAction;
import application.bean.UserInfoBean;
import application.server.ServerUserProfile;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PullUsersServlet implements Servlet{
    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {
        log.debug("A dispose thread starts");
        // get userList from profile.SocketUserInfoMap
        List<UserInfoBean> list = new ArrayList<>(profile.lobbyUserSet());
        Action respond = new UserInfoListReplyAction(list);
        try {
            log.debug("PullUsersRely=" + list);
            send(respond, socket);
        } catch (IOException e) {
            log.warn("PullUsersRelyException, end a socket and clean");
            handleException(socket, profile);
        }
        log.debug("A dispose thread ends");
    }

    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {
        profile.timeout(socket);
    }
}
