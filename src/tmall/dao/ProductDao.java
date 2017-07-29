package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15001 on 2017/7/24.
 */
public class ProductDao {


    public int getTotal(int cid) {
        int total = 0;
        try (Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()){

            String sql = "SELECT COUNT(*) FROM product WHERE cid ="+cid;
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }


    public void add(Product product) {

        String sql = "INSERT INTO product (name, subTitle, originalPrice, promotePrice, stock, cid, createDate) VALUES (?,?,?,?,?,?,?)";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,product.getName());
            preparedStatement.setString(2,product.getSubTitle());
            preparedStatement.setFloat(3,product.getOriginalPrice());
            preparedStatement.setFloat(4,product.getPromotePrice());
            preparedStatement.setInt(5,product.getStock());
            preparedStatement.setInt(6,product.getCategory().getId());
            preparedStatement.setTimestamp(7, DateUtil.dateToTimeStamp(product.getCreateDate()));

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                product.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(Product product) {

        String sql = "UPDATE product SET name = ?, subTitle = ?, originalPrice =?,promotePrice=?,stock=?,cid = ?,createDate = ? WHERE  id = ?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
             preparedStatement.setString(1,product.getName());
             preparedStatement.setString(2,product.getSubTitle());
             preparedStatement.setFloat(3,product.getOriginalPrice());
             preparedStatement.setFloat(4,product.getPromotePrice());
             preparedStatement.setInt(5,product.getStock());
             preparedStatement.setInt(6,product.getCategory().getId());
             preparedStatement.setTimestamp(7,DateUtil.dateToTimeStamp(product.getCreateDate()));
             preparedStatement.setInt(8,product.getId());

             preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try(Connection connection = DBUtil.getConnection();Statement statement = connection.createStatement()) {

            String sql = "DELETE FROM product WHERE id ="+id;
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Product get(int id) {
        Product product = null;
        try(Connection connection = DBUtil.getConnection(); Statement statement = connection.createStatement()) {

            String sql = "SELECT * FROM product WHERE id ="+id;
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                product = new Product();
                product.setId(id);
                product.setName(resultSet.getString("name"));
                product.setSubTitle(resultSet.getString("subTitle"));
                product.setOriginalPrice(resultSet.getFloat("originalPrice"));
                product.setPromotePrice(resultSet.getFloat("promotePrice"));
                product.setStock(resultSet.getInt("stock"));
                product.setCategory( new CategoryDao().get(resultSet.getInt("cid")));
                product.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));

                setFirstProductPicture(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;

    }

    public List<Product> list(int cid, int begin, int size) {
        List<Product> products = new ArrayList<>();
        Category category =new CategoryDao().get(cid);

        String sql = "SELECT * FROM product WHERE  cid = ?, ORDER BY id DESC LIMIT ?,?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,cid);
            preparedStatement.setInt(2,begin);
            preparedStatement.setInt(3,size);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setSubTitle(resultSet.getString("subTitle"));
                product.setOriginalPrice(resultSet.getFloat("originalPrice"));
                product.setPromotePrice(resultSet.getFloat("promotePrice"));
                product.setStock(resultSet.getInt("stock"));
                product.setCategory(category);
                product.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));

                setFirstProductPicture(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> list(int cid) {
        return list(cid,0,Short.MAX_VALUE);
    }

    public List<Product> list(int begin,int size) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM product LIMIT ?,?";
        try(Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1,begin);
            preparedStatement.setInt(2,size);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setSubTitle(resultSet.getString("subTitle"));
                product.setOriginalPrice(resultSet.getFloat("originalPrice"));
                product.setPromotePrice(resultSet.getFloat("promotePrice"));
                product.setStock(resultSet.getInt("stock"));
                product.setCategory(new CategoryDao().get(resultSet.getInt("cid")));
                product.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));

                setFirstProductPicture(product);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> list() {
        return list(0,Short.MAX_VALUE);
    }

    public void fill( Category category) {
        List<Product> products = list(category.getId());
        category.setProducts(products);
    }

    public void fill(List<Category> categories) {
        for(Category category :categories){
            fill(category);
        }
    }

    public void fillByRow(List<Category> categories) {
       for(Category category :categories) {
           int productNumberEachRow = 8;
           List<Product> products = category.getProducts();
           List<List<Product>> productByRow = new ArrayList<>();
           for(int i=0; i<products.size();i+=productNumberEachRow){
               int size = i+productNumberEachRow;
               size = size>products.size() ? products.size():size;
               List<Product> products1 = products.subList(i,size);
               productByRow.add(products1);
           }
           category.setProductsByRow(productByRow);
       }

    }

    public void setFirstProductPicture(Product product) {
        List<ProductImage> pis = new ProductImageDao().list(product,ProductImageDao.TYPR_SINGLE);
        if(!pis.isEmpty()){
            product.setFirstProductImage(pis.get(0));
        }


    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = new OrderItemDao().getSaleCount(product.getId());
        product.setSaleCount(saleCount);

        int reviewCount = new ReviewDao().getCount(product.getId());
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products) {
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int begin, int size) {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM product WHERE name LIKE ? LIMIT ?,?";
        try (Connection connection = DBUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, keyword);
            preparedStatement.setInt(2, begin);
            preparedStatement.setInt(3, size);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("name"));
                product.setSubTitle(resultSet.getString("subTitle"));
                product.setOriginalPrice(resultSet.getFloat("originalPrice"));
                product.setStock(resultSet.getInt("stock"));
                product.setPromotePrice(resultSet.getFloat("promotePrice"));
                product.setCategory(new CategoryDao().get(resultSet.getInt("cid")));
                product.setCreateDate(DateUtil.timestampToDate(resultSet.getTimestamp("createDate")));
                setFirstProductPicture(product);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }





}
