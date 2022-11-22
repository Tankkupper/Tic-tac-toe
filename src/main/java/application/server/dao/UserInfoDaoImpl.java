package application.server.dao;

import application.bean.UserInfoBean;
import application.server.Util.DBUtil;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserInfoDaoImpl implements UserInfoDao {
    private DBUtil dbUtil = new DBUtil();
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    @Override
    public Optional<UserInfoBean> login(String name, String pass) throws Exception {
        int total = -1, win = -1, lose = -1, tie = -1;
        connection = dbUtil.getConnection();
        String sql = "select al.total, al.win, al.lose, al.tie from account a join account_log al " +
                "on a.username = al.username where a.username=? and password=?";
        preparedStatement =connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, pass);
        resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            total = resultSet.getInt(1);
            win = resultSet.getInt(2);
            lose = resultSet.getInt(3);
            tie = resultSet.getInt(4);
        }
        dbUtil.closeDBResource(connection, preparedStatement, resultSet);
        if (total == -1) {
            return Optional.empty();
        } else {
            return Optional.of(new UserInfoBean(name, total, win, lose, tie));
        }
    }

    @Override
    public boolean register(String name, String pass) throws Exception {
        int result = 0;
        connection = dbUtil.getConnection();
        String sql = "insert into account(username, password)  values (?, ?)";
        preparedStatement =connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, pass);
        try {
            result = preparedStatement.executeUpdate();
            // when insert duplicate key value, raise PSQLException
        } catch (PSQLException e) {
            // ERROR: duplicate key value violates unique constraint "account_pkey"
            log.debug("duplicate username occurs when register");
        }
        dbUtil.closeDBResource(connection, preparedStatement, resultSet);
        return result==1;
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println(new UserInfoDaoImpl().register("c", "b"));
//    }
}
