package tmall.dao;

import org.junit.Test;
import tmall.bean.User;
import tmall.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDao {
    public int getTotal() {
        int total = 0;
        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT count(*) FROM user";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  total;
    }

    public void add(User user) {
        String sql = "INSERT INTO user(name,password) VALUES(?,?)";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
             preparedStatement.setString(1,user.getName());
             preparedStatement.setString(2,user.getPassword());

             preparedStatement.execute();
             ResultSet resultSet = preparedStatement.getGeneratedKeys();
             if(resultSet.next()) {
                 int id = resultSet.getInt(1);
                 user.setId(id);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   public void update(User user) {
        String sql = "UPDATE user SET name = ?,password = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getPassword());
            preparedStatement.setInt(3,user.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
   }

   public void delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
   }

   public User get(int id) {
       String sql = "SELECT * FROM user WHERE id = ?";
       User user = null;
       try( Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
           preparedStatement.setInt(1,id);
           ResultSet resultSet = preparedStatement.executeQuery();
           while(resultSet.next()){
               user = new User();
               user.setId(id);
               user.setName(resultSet.getString("name"));
               user.setPassword(resultSet.getString("password"));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return user;
   }

    public User get(String name,String password) {
        String sql = "SELECT * FROM user WHERE name = ?and password = ?";
        User user = null;
        try( Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User get(String name) {
        String sql = "SELECT * FROM user WHERE name = ?";
        User user = null;
        try( Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                user = new User();
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getInt("id"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    public List<User> list(int begin,int size){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id DESC LIMIT ?,?";

        try(Connection connection = DBUtil.getConnection();PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1,begin);
            statement.setInt(2,size);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> list() {
        return list(0,Short.MAX_VALUE);
    }
    public boolean isExist(String name) {
        User user = get(name);
        return user != null;
    }


}
