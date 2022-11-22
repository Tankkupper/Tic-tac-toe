package application.action;

import application.bean.FailureBean;

public class FailureAction implements Action{
    private FailureBean failureBean;

    public FailureAction(FailureBean failureBean) {
        this.failureBean = failureBean;
    }

    public FailureBean getFailureBean() {
        return failureBean;
    }

    @Override
    public String toString() {
        return "FailureAction{" +
                "failureBean=" + failureBean +
                '}';
    }
}
