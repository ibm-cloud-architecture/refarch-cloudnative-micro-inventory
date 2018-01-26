package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Inventory;

public class InventoryDAOImpl {

	public List getInventoryDetails(){
		
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
	public List<Inventory> findByNameContaining(String name){
		
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
	public List<Inventory> findByPriceLessThanEqual(double price){
		
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
	public Inventory findOne(long id){
		
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
	public void putInventoryDetails(){

		JDBCConnection jdbcConnection = new JDBCConnection();
		
		Connection connection = jdbcConnection.getConnection();
		try
	    {
	      String query = " insert into users (id,stock,price,img_alt,img,name,description)"
	        + " values (?, ?, ?, ?, ?, ?, ?)";

	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = connection.prepareStatement(query);
	      preparedStmt.setLong(1, 13413);
	      preparedStmt.setInt (2, 1000);
	      preparedStmt.setDouble(3,  5199.99);
	      preparedStmt.setString(4, "Selectric Typewriter");
	      preparedStmt.setString(5, "selectric.jpg");
	      preparedStmt.setString(6, "Selectric Typewriter");
	      preparedStmt.setString(7, "Unveiled in 1961, the revolutionary Selectric typewriter eliminated the need for conventional type bars and movable carriages by using an innovative typing element on a head-and-rocker assembly, which, in turn, was mounted on a small carrier to move from left to right while typing.");
	     
	      // execute the preparedstatement
	      preparedStmt.execute();
	      
	      connection.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	public void save(Inventory item){

		JDBCConnection jdbcConnection = new JDBCConnection();
		
		Connection connection = jdbcConnection.getConnection();
		
		int stock = item.getStock();
		long id = item.getId();
		try
	    {
	      String query = "update users SET stock = ? " + " WHERE id = ?";

	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = connection.prepareStatement(query);
	      preparedStmt.setInt(1, stock);
	      preparedStmt.setLong(2, id);
	      // execute the preparedstatement
	      preparedStmt.execute();
	      
	      connection.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
}
