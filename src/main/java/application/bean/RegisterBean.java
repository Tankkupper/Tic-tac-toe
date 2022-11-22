package application.bean;

import java.io.Serializable;

public class RegisterBean implements Serializable {
    private String userName;
    private String password;

    public RegisterBean(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "RegisterBean{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
