package application.action;

import application.bean.UserInfoBean;

import java.util.List;

public class UserInfoListReplyAction implements Action{
    private List<UserInfoBean> list;

    public UserInfoListReplyAction(List<UserInfoBean> list) {
        this.list = list;
    }

    public List<UserInfoBean> getList() {
        return list;
    }
}
