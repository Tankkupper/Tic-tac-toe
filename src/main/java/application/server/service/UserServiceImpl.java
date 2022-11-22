package application.server.service;

import application.bean.LoginBean;
import application.bean.RegisterBean;
import application.bean.UserInfoBean;
import application.server.dao.UserInfoDao;
import application.server.dao.UserInfoDaoImpl;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private UserInfoDao userInfoDao = new UserInfoDaoImpl();

    @Override
    public Optional<UserInfoBean> login(LoginBean loginBean) {
        Optional<UserInfoBean> userInfoBean;
        // one is found, the other is not found or connection fault which both are empty.
        try{
            userInfoBean = userInfoDao.login(loginBean.getName(), loginBean.getPassword());
        } catch (Exception e) {
            userInfoBean = Optional.empty();
            e.printStackTrace();
        }
        return userInfoBean;
    }

    @Override
    public Optional<UserInfoBean> register(RegisterBean registerBean) {
        // 需要检测账户和密码的合法性 反正注册失败就返回null
        try {
            if (userInfoDao.register(registerBean.getName(), registerBean.getPassword())) {
                return Optional.of(new UserInfoBean(registerBean.getName(), 0, 0, 0, 0));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
