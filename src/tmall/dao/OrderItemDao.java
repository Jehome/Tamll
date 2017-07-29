package tmall.dao;


import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/27.
 */
public class OrderItemDao {

     public int getTotal(){
         int total = 0;
         try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

             String sql = "SELECT COUNT (*) FROM orderitem";
             ResultSet resultSet = statement.executeQuery(sql);

             if(resultSet.next()) {
                 total = resultSet.getInt(1);
             }

         } catch (SQLException e) {
             e.printStackTrace();
         }
         return total;
     }

    public void add(OrderItem orderItem) {

        String sql = "INSERT INTO orderitem (pid, oid, uid, number) VALUES (?,?,?,?)";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
                preparedStatement.setInt(1,orderItem.getProduct().getId());

                //订单刚创建的时候是没有订单项目的
                if(orderItem.getOrder() == null) {
                    preparedStatement.setInt(2,-1);
                }else{
                    preparedStatement.setInt(2,orderItem.getOrder().getId());
                }
                preparedStatement.setInt(3,orderItem.getUser().getId());
                preparedStatement.setInt(4,orderItem.getNumber());

                preparedStatement.execute();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()) {
                    int id = resultSet.getInt(1);
                    orderItem.setId(id);
                }
         } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(OrderItem orderItem) {

        String sql = "UPDATE orderitem SET pid = ?,oid = ?,uid = ?,number = ? WHERE id = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,orderItem.getProduct().getId());

            //订单刚创建的时候是没有订单项目的
            if(orderItem.getOrder() == null) {
                preparedStatement.setInt(2,-1);
            }else{
                preparedStatement.setInt(2,orderItem.getOrder().getId());
            }
            preparedStatement.setInt(3,orderItem.getUser().getId());
            preparedStatement.setInt(4,orderItem.getNumber());
            preparedStatement.setInt(5,orderItem.getId());

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

         try(Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()) {

             String sql = "DELETE FROM orderitem WHERE id ="+id;
             statement.execute(sql);
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }

    public OrderItem get(int id) {
        OrderItem orderItem = null;

        try (Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()){

            String sql = "SELECT * FROM orderitem WHERE id = "+id;
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                orderItem = new OrderItem();
                orderItem.setId(id);
                orderItem.setNumber(resultSet.getInt("number"));
                orderItem.setOrder( new OrderDao().get(resultSet.getInt("oid")));
                orderItem.setUser(new UserDao().get(resultSet.getInt("uid")));
                orderItem.setProduct( new ProductDao().get(resultSet.getInt("pid")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItem;
    }

    public List<OrderItem> listByUser(int uid,int begin,int size) {
         List<OrderItem> orderItems = new ArrayList<>();
        User user = new UserDao().get(uid);
        String sql = "SELECT * FROM orderitem WHERE uid = ? AND oid = -1 ORDER BY id DESC LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1,uid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setUser(user);
                int oid = resultSet.getInt("oid");
                if(oid != -1) {
                    Order order = new OrderDao().get(oid);
                    orderItem.setOrder(order);
                }
                orderItem.setProduct(new ProductDao().get(resultSet.getInt("pid")));
                orderItem.setId(resultSet.getInt("id"));
                orderItem.setNumber(resultSet.getInt("number"));

                orderItems.add(orderItem);
            }
         } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public List<OrderItem> listByUser(int uid){
         return listByUser(uid,0,Short.MAX_VALUE);
    }


    public List<OrderItem> listByProduct(int pid,int begin,int size) {
        List<OrderItem> orderItems = new ArrayList<>();
        Product product = new ProductDao().get(pid);
        String sql = "SELECT * FROM orderitem WHERE pid = ? ORDER BY id DESC LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,pid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                int oid = resultSet.getInt("oid");
                if(oid != -1) {
                    Order order = new OrderDao().get(oid);
                    orderItem.setOrder(order);
                }
                orderItem.setUser(new UserDao().get(resultSet.getInt("uid")));
                orderItem.setId(resultSet.getInt("id"));
                orderItem.setNumber(resultSet.getInt("number"));

                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public List<OrderItem> listByProduct(int pid) {
        return listByProduct(pid,0,Short.MAX_VALUE);
    }

    public List<OrderItem> listByOrder(int oid,int begin,int size) {
        Order order = new OrderDao().get(oid);
        List<OrderItem> orderItems = new ArrayList<>();

        String sql = "SELECT * FROM orderitem WHERE oid = ? ORDER BY id DESC  LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,oid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setUser(new UserDao().get(resultSet.getInt("uid")));
                orderItem.setProduct(new ProductDao().get(resultSet.getInt("pid")));
                orderItem.setId(resultSet.getInt("id"));
                orderItem.setNumber(resultSet.getInt("number"));

                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public List<OrderItem> listByOrder(int oid) {
        return listByOrder(oid,0,Short.MAX_VALUE);
    }


    public int getSaleCount(int pid) {
        int saleCount =  0;

        try (Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()){

            String sql = "SELECT COUNT (*) FROM orderitem WHERE  pid =" +pid;
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()) {
                saleCount = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  saleCount;
    }

    public void fill(Order o){
        List<OrderItem> orderItems = listByOrder(o.getId());
        float total = 0;
        int sum = 0;
        for(OrderItem orderItem :orderItems){
            total += orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
            sum += orderItem.getNumber();
        }
        o.setTotal(total);
        o.setTotalNumber(sum);
        o.setOrderItems(orderItems);
    }

    public void fill (List<Order> orders) {
        for(Order order : orders) {
            fill(order);
        }
    }


}
