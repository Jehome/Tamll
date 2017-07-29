package tmall.dao;

import tmall.bean.Category;
import tmall.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/23.
 */
public class CategoryDao {
    public int getTotal() {
        int total = 0;

        try ( Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement();) {
            String sql = "SELECT count(*) FROM category";
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                total = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public void add(Category category) {
        String sql = "INSERT INTO category (name) VALUES(?)";

        try (Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,category.getName());
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                category.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Category category) {
        String sql = "UPDATE category SET name = ? where id = ?";

        try ( Connection connection = DBUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){

            int id = category.getId();
            statement.setString(1,category.getName());
            statement.setInt(2,id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM category WHERE id = ?";

        try ( Connection connection = DBUtil.getConnection();PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1,id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Category get(int id) {
        String sql = "SELECT * FROM category where id = ?";
        Category category = null;
        try(Connection connection = DBUtil.getConnection();PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,id);
            ResultSet set = statement.executeQuery();

            while(set.next()){
                category = new Category();
                int getId = set.getInt(1);
                category.setId(getId);
                String name = set.getString(2);
                category.setName(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public List<Category> list(int begin,int size) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY id DESC LIMIT ?,?;";

        try(Connection connection = DBUtil.getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql
        )) {
            preparedStatement.setInt(1,begin);
            preparedStatement.setInt(2,size);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Category category = new Category();
                category.setId(resultSet.getInt(1));
                category.setName(resultSet.getString(2));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> list() {
        return list(0,Short.MAX_VALUE);
    }

}
