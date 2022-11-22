package application.server.dao;

import application.bean.UserInfoBean;
import application.server.Server;
import org.apache.log4j.Logger;

import java.util.Optional;

public interface UserInfoDao {
    Logger log = Logger.getLogger(Server.class);
    Optional<UserInfoBean> login(String name, String pass) throws Exception;

    boolean register(String name, String pass) throws Exception;

    public boolean win(String name) throws Exception;
    public boolean lose(String name) throws Exception;
    public boolean tie(String name) throws Exception;

}
