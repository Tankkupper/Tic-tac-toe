package application.server.servlet;

import application.action.Action;
import application.action.FailureInvitationAction;
import application.action.InviteAction;
import application.bean.FailureBean;
import application.bean.UserInfoBean;
import application.server.ServerUserProfile;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class InvitationServlet implements Servlet{
    @Override
    public void dispose(Action action, Socket inviter, ServerUserProfile profile) {
        // you want to invite whe find socket and send a invitation.
        log.debug("A dispose thread starts");

        // whether this is a invitee

        Socket mayInviter = mayInviter = profile.checkInvited(inviter);
        // need to exclude invite self
        if (mayInviter != null && !((InviteAction) action).who().equals(profile.getUserBySocket(inviter).orElse(null))) {
            // TODO: Here a game start, switch to gameServlet, first send a state class initialize game, and check
            log.debug("Someone agree the invitation");
            log.debug("A dispose thread ends");
            return;
        }


        InviteAction inviteAction = (InviteAction) action;
        UserInfoBean userInfoBean = inviteAction.who();
        Optional<Socket> inviteeOptional = profile.getSocketByUser(userInfoBean);

        // when the target is living
        if (inviteeOptional.isPresent()) {
            Optional<UserInfoBean> inviterInfo = profile.getUserBySocket(inviter);

            // when the inviter is living
            if (inviterInfo.isPresent()) {
                if (inviteeOptional.get().equals(inviter)) {
                    extracted(inviter, profile, "You can not invite yourself");
                } else {
                    try {
                        send(new InviteAction(inviterInfo.get()), inviteeOptional.get());
                        log.debug("Send a invitation to " + inviteeOptional.get());
                        profile.putNewInvitation(inviter, inviteeOptional.get());
                        log.debug("Put a new Invitation");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    // sleep 12 s then the invitation expired
                                    Thread.sleep(12000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                profile.InvitationExpire(inviter);
                                log.debug("An invitation is expired");
                            }
                        }).start();
                    } catch (IOException e) {
                        extracted(inviter, profile, "Target invited offline, or playing now");
                    }
                }
            } else {
                log.warn("Why does not inviter userInfo appear in lobby map?" + inviter);
            }
        } else {
            extracted(inviter, profile, "Target invited offline, or playing now");
        }
        log.debug("A dispose thread ends");
    }

    private void extracted(Socket socket, ServerUserProfile profile, String info) {
        try {
            send(new FailureInvitationAction(
                    new FailureBean(
                            info)), socket);
            log.debug(info);
        } catch (IOException ee) {
            handleException(socket, profile);
            log.debug("A IOException occurs when disposing InvitationServlet");
        }
    }


    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {
        profile.timeout(socket);
    }
}
