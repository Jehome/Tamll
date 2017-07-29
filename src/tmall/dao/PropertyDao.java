package tmall.dao;


import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyDao {

    public int getTotal(int id) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM property WHERE cid = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Property property) {
        String sql = "INSERT  INTO property (cid, name) VALUES (?,?)";
        try (Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1,property.getCategory().getId());
            preparedStatement.setString(2,property.getCategory().getName());

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                property.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Property property) {

        String sql = "UPDATE property SET cid = ?, name = ? WHERE  id = ?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
             preparedStatement.setInt(1,property.getCategory().getId());
             preparedStatement.setString(2,property.getCategory().getName());
             preparedStatement.setInt(3,property.getId());

             preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM property WHERE id = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);

            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Property get(String name, int cid) {
        Property property = null;
        String sql = "SELECT * FROM property WHERE name = ? and cid = ?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,cid);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                property = new Property();
                int id = resultSet.getInt(1);
                property.setId(id);
                property.setName(name);
                property.setCategory( new CategoryDao().get(cid));
            }


        } catch (SQLException e) {
            e.printStackTrace();

        }
        return property;
    }

    public Property get(int id) {
        Property property = null;
        String sql = "SELECT * FROM property WHERE id = ?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                property = new Property();
                property.setCategory(new CategoryDao().get(resultSet.getInt("cid")));
                property.setName(resultSet.getString("name"));
                property.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return property;
    }

    public List<Property> list(int cid,int begin,int size) {
        List<Property> lists = new ArrayList<>();

        String sql = "SELECT * FROM property WHERE cid = ? ORDER BY id Desc LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,cid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);
            Category category = new CategoryDao().get(cid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Property property = new Property();
                property.setId(resultSet.getInt("id"));
                property.setName(resultSet.getString("name"));
                property.setCategory(category);

                lists.add(property);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

    public List<Property> list(int cid) {
        return list(cid,0,Short.MAX_VALUE);
    }


}

