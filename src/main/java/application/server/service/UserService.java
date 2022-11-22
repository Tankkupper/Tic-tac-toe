package application.server.service;

import application.bean.LoginBean;
import application.bean.RegisterBean;
import application.bean.UserInfoBean;

import java.util.Optional;

public interface UserService {
    Optional<UserInfoBean> login(LoginBean loginBean);
    Optional<UserInfoBean> register(RegisterBean registerBean);

}
