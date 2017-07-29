package tmall.dao;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.junit.Test;
import tmall.bean.Order;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/25.
 */
public class OrderDao {

    public static final String WAIT_PAY = "waitPay";
    public static final String WAIT_DELIEVERY = "waitDelivery";
    public static final String WAIT_CONFIRM = "waitConfirm";
    public static final String WAIT_REVIEW = "waitReview";
    public static final String FINISH = "finish";
    public static final String DELETE = "delete";


    public int getTotal() {
        int total = 0;

        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT COUNT (*) FROM order_ ";
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    public void add(Order order) {

        String sql = "INSERT INTO order_ (orderCode, address, post, receiver, mobile, userMessage, createDate, payDate, deliveryDate, confirmDate, uid , status) VALUES  (?,?,?,?,?,?,?,?,?,?,?,?)";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,order.getOrderCode());
            preparedStatement.setString(2,order.getAddress());
            preparedStatement.setString(3,order.getPost());
            preparedStatement.setString(4,order.getReceiver());
            preparedStatement.setString(5,order.getMobile());
            preparedStatement.setString(6,order.getUserMessage());

            preparedStatement.setTimestamp(7,DateUtil.dateToTimeStamp(order.getCreateDate()));
            preparedStatement.setTimestamp(8,DateUtil.dateToTimeStamp(order.getPayDate()));
            preparedStatement.setTimestamp(9,DateUtil.dateToTimeStamp(order.getDeliveryDate()));
            preparedStatement.setTimestamp(10,DateUtil.dateToTimeStamp(order.getConfirmDate()));

            preparedStatement.setInt(11,order.getUser().getId());
            preparedStatement.setString(12,order.getStatus());

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                order.setId(id);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Order order) {


        String sql = "UPDATE order_ SET address= ?, post=?, receiver=?,mobile=?,userMessage=? ,createDate = ? , payDate =? , deliveryDate =?, confirmDate = ? , orderCode =?, uid=?, status=? WHERE id = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,order.getAddress());
            preparedStatement.setString(2,order.getPost());
            preparedStatement.setString(3,order.getReceiver());
            preparedStatement.setString(4,order.getMobile());
            preparedStatement.setString(5,order.getUserMessage());
            preparedStatement.setTimestamp(6,DateUtil.dateToTimeStamp(order.getCreateDate()));
            preparedStatement.setTimestamp(7,DateUtil.dateToTimeStamp(order.getPayDate()));
            preparedStatement.setTimestamp(8,DateUtil.dateToTimeStamp(order.getDeliveryDate()));
            preparedStatement.setTimestamp(9,DateUtil.dateToTimeStamp(order.getConfirmDate()));
            preparedStatement.setString(10,order.getOrderCode());
            preparedStatement.setInt(11,order.getUser().getId());
            preparedStatement.setString(12,order.getStatus());
            preparedStatement.setInt(13,order.getId());

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        try (Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()){
            String sql ="DELETE  FROM order_ WHERE id =" +id;
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order get(int id) {
        Order order = null;

        try (Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()){

            String sql = "SELECT * FROM order_ WHERE id = "+id;
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                order = new Order();

                order.setId(id);
                order.setAddress(resultSet.getString("address"));
                order.setConfirmDate(DateUtil.timestampToDate(resultSet.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));
                order.setDeliveryDate(DateUtil.timestampToDate(resultSet.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestampToDate(resultSet.getTimestamp("payDate")));
                order.setMobile(resultSet.getString("mobile"));
                order.setOrderCode(resultSet.getString("orderCode"));
                order.setPost(resultSet.getString("post"));
                order.setReceiver(resultSet.getString("receiver"));
                order.setStatus(resultSet.getString("status"));
                order.setUser(new UserDao().get(resultSet.getInt("uid")));
                order.setUserMessage(resultSet.getString("userMessage"));



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> list (int begin,int size) {
        List<Order> lists = new ArrayList<>();

        String sql = "SELECT * FROM order_ ORDER BY id DESC LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1,begin);
            preparedStatement.setInt(2,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();

                order.setId(resultSet.getInt("id"));
                order.setAddress(resultSet.getString("address"));
                order.setConfirmDate(DateUtil.timestampToDate(resultSet.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));
                order.setDeliveryDate(DateUtil.timestampToDate(resultSet.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestampToDate(resultSet.getTimestamp("payDate")));
                order.setMobile(resultSet.getString("mobile"));
                order.setOrderCode(resultSet.getString("orderCode"));
                order.setPost(resultSet.getString("post"));
                order.setReceiver(resultSet.getString("receiver"));
                order.setStatus(resultSet.getString("status"));
                order.setUser(new UserDao().get(resultSet.getInt("uid")));
                order.setUserMessage(resultSet.getString("userMessage"));

                lists.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public List<Order> list() {
        return list(0,Short.MAX_VALUE);
    }

    public List<Order> list(int uid, String excludedStatus,int begin,int size) {
        List<Order> lists = new ArrayList<>();
        User user = new UserDao().get(uid);
        String sql = "SELECT * FROM order_ WHERE uid = ? AND status != ? ORDER BY id DESC LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1,uid);
            preparedStatement.setString(2,excludedStatus);
            preparedStatement.setInt(3,begin);
            preparedStatement.setInt(4,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();

                order.setId(resultSet.getInt("id"));
                order.setAddress(resultSet.getString("address"));
                order.setConfirmDate(DateUtil.timestampToDate(resultSet.getTimestamp("confirmDate")));
                order.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));
                order.setDeliveryDate(DateUtil.timestampToDate(resultSet.getTimestamp("deliveryDate")));
                order.setPayDate(DateUtil.timestampToDate(resultSet.getTimestamp("payDate")));
                order.setMobile(resultSet.getString("mobile"));
                order.setOrderCode(resultSet.getString("orderCode"));
                order.setPost(resultSet.getString("post"));
                order.setReceiver(resultSet.getString("receiver"));
                order.setStatus(resultSet.getString("status"));
                order.setUser(user);
                order.setUserMessage(resultSet.getString("userMessage"));

                lists.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public List<Order> list(int uid,String excludedStatus) {
        return list(uid,excludedStatus,0,Short.MAX_VALUE);
    }


}
