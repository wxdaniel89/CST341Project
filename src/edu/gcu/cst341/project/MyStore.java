package edu.gcu.cst341.project;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MyStore {

	private String name;
	private DBConnect con;

	MyStore(String name) {
		this.name = name;
		con = new DBConnect();
	}

	public void open() {
		String user = null;
		boolean exit = false;
		do {
			switch (UserInterface.menuMain()) {
			case 0:
				System.out.println("Thank you! Come again!");
				exit = true;
				break;
			case 1:
				user = login();
				if (user != null) {
					System.out.println("Login successful!!");
					shop();
				} else {
					System.out.println("Login unsuccessful");
				}
				break;
			case 2:
				admin();
				break;
			default:
				open();
			}
		} while (!exit);
	}

	private String login() {
		String result = null;

		String[] login = UserInterface.login();

		String sql = "SELECT UserId, UserFirstName FROM users WHERE UserName = ? AND UserPassword = ? AND UserStatus = 1";

		try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
			ps.setString(1, login[0]);
			ps.setString(2, login[1]);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getString("UserFirstName");
			} else {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void shop() {
		switch (UserInterface.menuShop()) {
		case 0:
			return;
		case 1:
			createCartItem();
			break;
		case 2:
			readCartItems();
			break;
		case 3:
			deleteCartItem();
			break;
		default:
			return;
		}
	}

	private void admin() {
		switch (UserInterface.menuAdmin()) {
		case 0:
			return;
		case 1:
			try {
				createProduct();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				readProducts();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 3:
			try {
				updateProduct();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				deleteProduct();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			open();
		}
	}

	private void createCartItem() {
		System.out.println("Add (Create) item to cart...");
		System.out.println();
	}

	private void readCartItems() {
		System.out.println("View (Read) cart...");
		System.out.println();
	}

	private void deleteCartItem() {
		Scanner sc = new Scanner(System.in);

		System.out.println("Delete from cart...");
		System.out.println();
	}

	private void createProduct() throws SQLException {
		Scanner sc = new Scanner(System.in);

		System.out.println("Create product...");
		System.out.println("==================");
		System.out.println("Please Enter a Unique Product Id: ");
		int p_Id = sc.nextInt();
		sc.nextLine();
		System.out.println("Please Enter the Products Name: ");
		String p_Name = sc.nextLine();
		System.out.println("Please Enter the Products Price: ");
		String p_Price = sc.nextLine();
		System.out.println("Please Enter Products Inventory Status [true] = In stock [false] = Out of Stock : ");
		String p_Status = sc.nextLine();

		String sql_command = "INSERT INTO cst341nproject.products (product_Id, product_Name, "
				+ "product_Price, product_Stock_Stadus) VALUE (?,?,?,?);";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql_command)) {

			initiate.setInt(1, p_Id);
			initiate.setString(2, p_Name);
			initiate.setString(3, p_Price);
			initiate.setString(4, p_Status);
			initiate.executeUpdate();
			System.out.println(initiate);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readProducts() throws SQLException {
		System.out.println("View (Read) all products...");
		System.out.println();

		String sql_command = "SELECT product_Id, product_Name, product_Price, product_Stock_Stadus FROM cst341nproject.products;";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql_command)) {

			ResultSet list = initiate.executeQuery();
			while (list.next()) {
				System.out.println("All Products: " + list.getInt("product_Id") + " " + list.getString("product_Name")
						+ " " + list.getString("product_Price") + " " + list.getString("product_Stock_Stadus"));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void updateProduct() throws SQLException {
		Scanner sc = new Scanner(System.in);

		// 1] Ask user which credentials to update
		System.out.println("Updating Product");
		System.out.println("==================");
		System.out.println("");

		System.out.println("Which Product Id Number do you want to update? ");
		int id = sc.nextInt();
		sc.nextLine();

		// 2] Connect to DB and get current credentials
		String sql = "SELECT product_Id, product_Name, product_Price, product_Stock_Stadus"
				+ " FROM cst341nproject.products WHERE product_Id = ?";
		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql)){

			initiate.setInt(1, id);
			ResultSet results = initiate.executeQuery();
			results.next();
			
			
		// 3] Display the current credentials while asking for new info

		System.out.println("Update Product Id Number : [" + results.getInt("product_Id") + "] ? : ");
		int p_Id = sc.nextInt();
		sc.nextLine();
		System.out.println("Update Product Name : [" + results.getString("product_Name") + "] ? : ");
		String p_Name = sc.nextLine();
		System.out.println("Update Product Price : [" + results.getString("product_Price") + "] ? : ");
		String p_Price = sc.nextLine();
		System.out.println("Update Inventory Status [true] = In stock [false] = Out of Stock : ["
				+ results.getString("product_Stock_Stadus") + "] ? : ");
		String inventory = sc.nextLine();

		// 4] Write the updated credentials to the DB
		
		String sql2 = "UPDATE cst341nproject.products SET product_Id = ?, product_Name = ?, product_Price = ?, "
				+ "product_Stock_Stadus = ? WHERE product_Id = 131";
		try (PreparedStatement initiate1 = con.getConnection().prepareStatement(sql2)){
		
		initiate1.setInt(1, p_Id);
		initiate1.setString(2, p_Name);
		initiate1.setString(3, p_Price);
		initiate1.setString(4, inventory);

		initiate1.executeUpdate();

		// 5] Check to see if the data was written to the DB throws exception
		String sql3 = "SELECT * FROM cst341nproject.products WHERE product_Id = ?";
		try (PreparedStatement initiate2 = con.getConnection().prepareStatement(sql3)){
			initiate2.setInt(1, id);
		ResultSet results3 = initiate2.executeQuery();
		results3.next();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		}
		}

	}

	private void deleteProduct() throws SQLException {

		Scanner sc = new Scanner(System.in);

		System.out.println("Delete Product: ");
		System.out.println("===================");
		System.out.println("What Product Id would you like to delete? ");
		int product_Id = sc.nextInt();
		sc.nextLine();

		String sql12 = "DELETE FROM cst341nproject.products WHERE product_Id = ?";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql12)) {

			initiate.setInt(1, product_Id);
			initiate.executeUpdate();
			System.out.println(initiate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now you can extract all the records
		// to see the remaining records

	}

}
