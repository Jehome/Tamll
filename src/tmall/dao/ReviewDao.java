package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Review;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/25.
 */
public class ReviewDao {

    public int getTotal() {
        int total = 0;
        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT COUNT (*) FROM review";
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    public int getTotal(int pid) {
        int total = 0;
        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT COUNT (*) FROM review WHERE pid = "+pid;
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Review review) {

        String sql = "INSERT INTO review (content, uid, pid, createDate) VALUES (?,?,?,?)";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1,review.getContent());
            preparedStatement.setInt(2,review.getUser().getId());
            preparedStatement.setInt(3,review.getProduct().getId());
            preparedStatement.setTimestamp(4, DateUtil.dateToTimeStamp(review.getCreateDate()));

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                review.setId(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update (Review review) {
        String sql = "UPDATE review SET content = ?,uid = ?,pid = ?,createDate = ? WHERE id = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1,review.getContent());
            preparedStatement.setInt(2,review.getUser().getId());
            preparedStatement.setInt(3,review.getProduct().getId());
            preparedStatement.setTimestamp(4, DateUtil.dateToTimeStamp(review.getCreateDate()));
            preparedStatement.setInt(5,review.getId());


            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try(Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()) {

            String sql = "DELETE  FROM  review WHERE id = "+id;
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Review get(int id) {
        Review review = null;

        try (Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()){

            String sql = "SELECT * FROM review WHERE id =" +id;
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()) {
                review = new Review();
                int uid = resultSet.getInt("uid");
                int pid = resultSet.getInt("pid");
                String content = resultSet.getString("content");
                java.util.Date createDate = DateUtil.timestampToDate(resultSet.getTimestamp("createDate"));

                Product product = new ProductDao().get(pid);
                User user = new UserDao().get(uid);

                review.setId(id);
                review.setContent(content);
                review.setUser(user);
                review.setProduct(product);
                review.setCreateDate(createDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return review;
    }

    public int getCount(int pid) {
        int total = 0;
        String sql = "SELECT COUNT (*) FROM review WHERE  pid = "+pid;
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            total = resultSet.getInt(1);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<Review> list(int pid,int begin,int size) {
        List<Review> reviews = new ArrayList<>();
        Product product = new ProductDao().get(pid);


        String sql = "SELECT * FROM review WHERE pid = ? ORDER BY id DESC LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,pid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Review review = new Review();
                int id = resultSet.getInt("id");
                int uid = resultSet.getInt("uid");
                String content = resultSet.getString("content");
                java.util.Date createDate = DateUtil.timestampToDate(resultSet.getTimestamp("createDate"));


                User user = new UserDao().get(uid);

                review.setId(id);
                review.setContent(content);
                review.setUser(user);
                review.setProduct(product);
                review.setCreateDate(createDate);

                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> list(int pid) {
        return list(pid,0,Short.MAX_VALUE);
    }

    public boolean isExist(String content,int pid) {

        String sql = "SELECT * FROM review WHERE content = ? and pid = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,content);
            preparedStatement.setInt(2,pid);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  false;
    }
}
