package tmall.util;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 15001 on 2017/7/23.
 */
public class DBUtil {
    private static final String IP = "127.0.0.1";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";
    private static final String DATABASE = "tmall";
    private static final String ENCODING = "UTF-8";
    private static final int PORT = 3306;


    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        String Url = String.format("jdbc:mysql://%S:%d/%s?characterEncoding=%s",IP,PORT,DATABASE,ENCODING);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  connection;

    }

    // 单元测试方法
    @Test
    public void test() {
        Connection connection = DBUtil.getConnection();
        if(connection != null){
            System.out.println("True");
        }
    }


}
