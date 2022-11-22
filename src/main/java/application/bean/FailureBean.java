package application.bean;

import java.io.Serializable;

public class FailureBean implements Serializable {
    private String failInfo;

    public FailureBean(String failInfo) {
        this.failInfo = failInfo;
    }

    public String getFailInfo() {
        return failInfo;
    }

    @Override
    public String toString() {
        return "FailureBean{" +
                "failInfo='" + failInfo + '\'' +
                '}';
    }
}
