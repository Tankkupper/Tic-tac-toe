package application.action;

import application.bean.RegisterBean;

public class RegisterAction implements Action {
    private RegisterBean registerBean;

    public RegisterAction(RegisterBean registerBean) {
        this.registerBean = registerBean;
    }

    public RegisterBean getRegisterBean() {
        return registerBean;
    }

    @Override
    public String toString() {
        return "RegisterAction{" +
                "registerBean=" + registerBean +
                '}';
    }
}
