package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Inventory;

public class InventoryDAOImpl {

    public List getInventoryDetails() {

        List invData = new ArrayList<>();

        JDBCConnection jdbcConnection = new JDBCConnection();

        Connection connection = jdbcConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "select id,stock,price,img_alt,img,name,description from inventorydb.items");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getLong("id"));
                inv.setName(rs.getString("name"));
                inv.setStock(rs.getInt("stock"));
                inv.setPrice(rs.getInt("price"));
                inv.setImgAlt(rs.getString("img_alt"));
                inv.setImg(rs.getString("img"));
                inv.setDescription(rs.getString("description"));
                invData.add(inv);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return invData;
    }

    public List<Inventory> findByNameContaining(String name) {

        List invData = new ArrayList<>();

        JDBCConnection jdbcConnection = new JDBCConnection();

        Connection connection = jdbcConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "select id,stock,price,img_alt,img,name,description from inventorydb.items where name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getLong("id"));
                inv.setStock(rs.getInt("stock"));
                inv.setName(rs.getString("name"));
                inv.setPrice(rs.getInt("price"));
                inv.setImgAlt(rs.getString("img_alt"));
                inv.setImg(rs.getString("img"));
                inv.setDescription(rs.getString("description"));
                invData.add(inv);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(invData);
        return invData;
    }

    public List<Inventory> findByPriceLessThanEqual(double price) {

        List invData = new ArrayList<>();

        JDBCConnection jdbcConnection = new JDBCConnection();

        Connection connection = jdbcConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "select id,stock,price,img_alt,img,name,description from inventorydb.items where price <= ?");
            ps.setDouble(1, price);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getLong("id"));
                inv.setStock(rs.getInt("stock"));
                inv.setName(rs.getString("name"));
                inv.setPrice(rs.getInt("price"));
                inv.setImgAlt(rs.getString("img_alt"));
                inv.setImg(rs.getString("img"));
                inv.setDescription(rs.getString("description"));
                invData.add(inv);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(invData);
        return invData;
    }

    public Inventory findOne(long id) {

        Inventory inventory = new Inventory();

        JDBCConnection jdbcConnection = new JDBCConnection();

        Connection connection = jdbcConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "select id,stock,price,img_alt,img,name,description from inventorydb.items where id = ?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setId(rs.getLong("id"));
                inv.setStock(rs.getInt("stock"));
                inv.setPrice(rs.getInt("price"));
                inv.setName(rs.getString("name"));
                inv.setImgAlt(rs.getString("img_alt"));
                inv.setImg(rs.getString("img"));
                inv.setDescription(rs.getString("description"));
                inventory = inv;

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(inventory);
        return inventory;
    }

    public void updateStock(int stock, long id) {

        JDBCConnection jdbcConnection = new JDBCConnection();

        Connection connection = jdbcConnection.getConnection();

        try {
            String query = "update inventorydb.items SET stock = stock - ? " + " WHERE id = ?";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, stock);
            preparedStmt.setLong(2, id);
            // execute the preparedstatement
            preparedStmt.execute();

            connection.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }
}
