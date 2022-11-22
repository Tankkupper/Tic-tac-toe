package application.action;

import application.bean.UserInfoBean;

public class InviteAction implements Action{
    private UserInfoBean userInfoBean;

    public InviteAction(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }


    public UserInfoBean who() {
        return userInfoBean;
    }

    @Override
    public String toString() {
        return "InviteAction{" + userInfoBean + "}";
    }
}
