package application.server.servlet;

import application.action.*;
import application.bean.FailureBean;
import application.bean.RegisterBean;
import application.bean.UserInfoBean;
import application.server.ServerUserProfile;
import application.server.service.UserService;
import application.server.service.UserServiceImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class RegisterServlet implements Servlet{
    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {
        log.debug("A dispose thread starts");
        RegisterAction registerAction = (RegisterAction) action;
        RegisterBean registerBean = registerAction.getRegisterBean();
        UserService userService = new UserServiceImpl();
        Optional<UserInfoBean> optional = userService.register(registerBean);

        Action respondAction;
        try {
            // null is wrong register present is good register
            if (optional.isPresent()) {
                UserInfoBean userInfoBean = optional.get();
                profile.putSocketUserInfoMap(socket, userInfoBean);
                respondAction = new UserInfoReplyAction(userInfoBean);

                // may throw exception
                this.send(respondAction, socket);
                log.info("Register success");
            } else {
                respondAction = new FailureAction(new FailureBean("Illegal username or password"));
                log.debug("Illegal username or password");
                // may throw exception
                this.send(respondAction, socket);
                // wrong try close the socket and release resource.
                handleException(socket, profile);
            }
        } catch (IOException e) {
            log.debug("A IOException occur when respond a registerAction");
            handleException(socket, profile);
            // e.printStackTrace();
        }
        log.debug("A dispose thread ends");
    }

    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {
        profile.timeout(socket);
    }
}
