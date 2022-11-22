package application.server.servlet;

import application.action.Action;
import application.action.PlayAction;
import application.action.PlayReplyAction;
import application.bean.PositionBean;
import application.bean.State;
import application.server.ServerUserProfile;
import application.server.dao.UserInfoDao;
import application.server.dao.UserInfoDaoImpl;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class GameServlet implements Servlet{
    UserInfoDao userInfoDao = new UserInfoDaoImpl();
    @Override
    public void dispose(Action action, Socket socket, ServerUserProfile profile) {
        log.debug("Game dispose thread starts");
        PlayAction playAction = (PlayAction) action;
        PositionBean positionBean = playAction.getPositionBean();
        Optional<ServerUserProfile.Room> roomOptional = profile.getRoom(socket);


        if (roomOptional.isPresent()) {

            State state = roomOptional.get().play(socket, positionBean);
            Socket opponent = roomOptional.get().getOpponent(socket);


            try {
                switch (state) {
                    case WIN:
                        log.debug("Win");
                        send(new PlayReplyAction(positionBean, State.WIN), socket);
                        send(new PlayReplyAction(positionBean, State.LOSE), opponent);
                        endGame(socket, State.WIN, opponent, State.LOSE, profile);
                        break;
                    case DRAW:
                        log.debug("Draw");
                        send(new PlayReplyAction(positionBean, State.DRAW), socket);
                        send(new PlayReplyAction(positionBean, State.DRAW), opponent);
                        endGame(socket, State.DRAW, opponent, State.DRAW, profile);
                        break;
                    case UNDEFINED:
                        log.debug("Undefined");
                        send(new PlayReplyAction(positionBean, State.UNDEFINED), socket);
                        send(new PlayReplyAction(positionBean, State.UNDEFINED), opponent);
                        break;
                    case INVALID:
                        log.debug("Invalid");
                        send(new PlayReplyAction(positionBean, State.INVALID), socket);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                // must someone disconnected
                //throw new RuntimeException(e);
                try {
                    send(new PlayReplyAction(positionBean, State.LOSS_WIN), socket);
                    // if success means opponent lost
                    endGame(socket, State.WIN, opponent, State.LOSE, profile);
                } catch (IOException ex) {
                    try {
                        send(new PlayReplyAction(positionBean, State.LOSS_WIN), opponent);
                        endGame(socket, State.LOSE, opponent, State.WIN, profile);
                    } catch (IOException exception) {
                        // all lost game draws
//                        throw new RuntimeException(exception);
                        endGame(socket, State.DRAW, opponent, State.DRAW, profile);
                    }
                }
            }
        } else {
            log.error("Wrong some thing, the room miss");
            handleException(socket, profile);
        }
    }

    private void endGame(Socket aa, State sa, Socket bb, State sb, ServerUserProfile profile)  {
        // TODO:
        profile.removeRoom(aa);
        try {
            dao(aa, sa, profile);
            dao(bb, sb, profile);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            log.error("Wrong connection!");
        }
    }

    private void dao(Socket bb, State sb, ServerUserProfile profile) throws Exception {
        String bname = profile.getUserBySocket(bb).get().getUserName();
        switch (sb) {
            case WIN:
                userInfoDao.win(bname);
                break;
            case DRAW:
                userInfoDao.tie(bname);
                break;
            case LOSE:
                userInfoDao.lose(bname);
                break;
        }
    }


    @Override
    public void handleException(Socket socket, ServerUserProfile profile) {
        //TODO:
        profile.timeout(socket);
    }
}
