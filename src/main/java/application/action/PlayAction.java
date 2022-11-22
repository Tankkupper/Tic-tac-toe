package application.action;

import application.bean.PositionBean;

public class PlayAction implements Action{
    PositionBean positionBean;

    public PlayAction(PositionBean positionBean) {
        this.positionBean = positionBean;
    }

    public PositionBean getPositionBean() {
        return positionBean;
    }

    @Override
    public String toString() {
        return "PlayAction{" +
                "positionBean=" + positionBean +
                '}';
    }
}
