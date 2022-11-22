package application.action;

import application.bean.UserInfoBean;

public class MatchAction implements Action{
    private UserInfoBean userInfoBean;

    // if true means someone join the matching queue.
    // False means someone will leave matching queue.
    private boolean match;

    public MatchAction(UserInfoBean userInfoBean) {
        this.userInfoBean = userInfoBean;
    }

    public void setJoin(){
        match = true;
    }

    public void setLeave() {
        match = false;
    }

    public boolean isJoinQueue() {
        return match;
    }

    public UserInfoBean getUserInfoBean() {
        return userInfoBean;
    }

    @Override
    public String toString() {
        return "MatchAction{" +
                "userInfoBean=" + userInfoBean +
                ", match=" + match +
                '}';
    }
}
