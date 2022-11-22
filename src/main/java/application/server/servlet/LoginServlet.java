package application.server.servlet;

import application.action.Action;
import application.action.FailureAction;
import application.action.LoginAction;
import application.action.UserInfoReplyAction;
import application.bean.FailureBean;
import application.bean.LoginBean;
import application.bean.UserInfoBean;
import application.server.ServerUserProfile;
import application.server.service.UserService;
import application.server.service.UserServiceImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class LoginServlet implements Servlet{
    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {
        log.debug("A dispose thread starts");
        LoginAction loginAction = (LoginAction) action;
        LoginBean loginBean = loginAction.getLoginBean();
        UserService userService = new UserServiceImpl();
        Optional<UserInfoBean> optional = userService.login(loginBean);

        Action respondAction;
        try {
            if (optional.isPresent()) {
                UserInfoBean userInfoBean = optional.get();
                profile.putSocketUserInfoMap(socket, userInfoBean);
                respondAction = new UserInfoReplyAction(userInfoBean);

                this.send(respondAction, socket);
                log.info("Login success, return " + userInfoBean);
            } else {
                respondAction = new FailureAction(new FailureBean("Wrong username or password"));
                this.send(respondAction, socket);
                // wrong try close the socket and release resource.
                log.debug("Wrong username or password");
            }
        } catch (IOException e) {
            handleException(socket, profile);
            e.printStackTrace();
        }
        log.debug("A dispose thread ends");
    }

    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {
        // remove stored Info
        profile.timeout(socket);
        /*
        * 关闭此套接字。

          当前在此套接字上的I / O操作中阻塞的任何线程将抛出SocketException 。

          关闭此套接字也将关闭套接字InputStream和OutputStream 。

          如果此套接字具有关联的通道，则通道也将关闭
          * */
    }
}
