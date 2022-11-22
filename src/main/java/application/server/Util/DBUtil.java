package application.server.Util;

import java.sql.*;

public class DBUtil {
    private Connection connection;
    private String host = "120.79.45.67";
    private String dbname = "Java2Assign2";
    private String port = "5432";
    private String name = "temp";
    private String password = "jkl123456";
    public Connection getConnection() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver.");
            return null;
        }
        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            this.connection = DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public void closeDBResource(Connection connection,
                                PreparedStatement preparedStatement,
                                ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
