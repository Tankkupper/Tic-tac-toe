package application.bean;

import java.io.Serializable;

public class UserInfoBean implements Serializable {
    private String userName;
    private int total;
    private int win;
    private int lose;
    private int tie;

    public UserInfoBean(String userName, int total, int win, int lose, int tie) {
        this.userName = userName;
        this.total = total;
        this.win = win;
        this.lose = lose;
        this.tie = tie;
    }

    public UserInfoBean(){};

    public boolean isValid(){
        return userName != null;

    }

    public String getUserName() {
        return userName;
    }

    public int getTotal() {
        return total;
    }

    public int getWin() {
        return win;
    }

    public int getLose() {
        return lose;
    }

    public int getTie() {
        return tie;
    }

    @Override
    public String toString() {
        return String.format("%-5s-%3d/%2d/%2d/%2d", userName,total, win, lose, tie);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return userName.equals(((UserInfoBean) obj).userName);
    }
}
