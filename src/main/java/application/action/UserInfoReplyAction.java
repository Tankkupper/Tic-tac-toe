package application.action;

import application.bean.UserInfoBean;

public class UserInfoReplyAction implements Action{
    private UserInfoBean userInfoBean;

    public UserInfoReplyAction(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }
}
