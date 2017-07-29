package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/24.
 */
public class ProductImageDao {

    public static final String TYPE_DETAIL = "type_detail";
    public static final String TYPR_SINGLE = "type_single";

    public int getTotal() {
        int total  = 0;

        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT COUNT (*) FROM productimage";
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(ProductImage productImage) {

        String sql = "INSERT  INTO productimage (pid, type) VALUES (?,?)";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1,productImage.getProduct().getId());
            preparedStatement.setString(2,productImage.getType());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                productImage.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(ProductImage productImage) {

    }

    public void delete(int id) {
        try( Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()) {
            String sql = "DELETE FROM productimage WHERE  id ="+id;

            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ProductImage get(int id) {
        ProductImage productImage = null;

        try( Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()) {

            String sql = "SELECT * FROM productimage WHERE id = "+id;
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()) {
                productImage = new ProductImage();
                int pid = resultSet.getInt("pid");
                Product product = new ProductDao().get(pid);
                String type =resultSet.getString("type");
                productImage.setId(id);
                productImage.setProduct(product);
                productImage.setType(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productImage;
    }

    public List<ProductImage> list(Product product,String type,int begin,int size) {
        List<ProductImage> list = new ArrayList<>();

        String sql = "SELECT * FROM productimage WHERE pid = ? AND type = ? ORDER BY id DESC LIMIT ?,?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,product.getId());
            preparedStatement.setString(2,type);
            preparedStatement.setInt(3,begin);
            preparedStatement.setInt(4,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setType(type);
                productImage.setId(id);
                list.add(productImage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  list;
    }

    public List<ProductImage> list(Product product,String type) {
        return list(product,type,0,Short.MAX_VALUE);
    }
}

