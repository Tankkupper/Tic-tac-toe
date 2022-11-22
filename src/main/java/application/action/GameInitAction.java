package application.action;

import application.bean.TYPE;
import application.bean.UserInfoBean;

public class GameInitAction implements Action{
    TYPE turn;
    UserInfoBean opponent;

    public GameInitAction(TYPE turn, UserInfoBean opponent) {
        this.turn = turn;
        this.opponent = opponent;
    }

    public TYPE getTurn() {
        return turn;
    }

    public UserInfoBean getOpponent() {
        return opponent;
    }

    @Override
    public String toString() {
        return "GameInitAction{" +
                "turn=" + turn +
                ", opponent=" + opponent +
                '}';
    }
}
