package application.action;

import application.bean.PositionBean;
import application.bean.State;

public class PlayReplyAction implements Action{
    PositionBean positionBean;
    State state;

    public PlayReplyAction(PositionBean positionBean, State state) {
        this.positionBean = positionBean;
        this.state = state;
    }

    public PositionBean getPositionBean() {
        return positionBean;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "PlayReplyAction{" +
                "positionBean=" + positionBean +
                ", state=" + state +
                '}';
    }
}
