package application.bean;

import java.io.Serializable;

public class LoginBean implements Serializable {
    private String userName;
    private String password;

    public LoginBean(String userName, String password) {
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
        return "LoginBean{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

