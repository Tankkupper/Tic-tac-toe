package application.action;

import application.bean.LoginBean;

public class LoginAction implements Action {
    private LoginBean loginBean;

    public LoginAction(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    @Override
    public String toString() {
        return "LoginAction{" +
                "loginBean=" + loginBean +
                '}';
    }
}
