package edu.gcu.cst341.project;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MyStore {
	static Scanner sc = new Scanner(System.in);
	private String name;
	private DBConnect con;
	private int userID;
	private String customer;

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
				System.out.println("\nThank you! Come again!");
				exit = true;
				break;
			case 1:
				user = login();
				if (user != null) {
					System.out.println("Login successful!!\n");
					shop();
				} else {
					System.out.println("Login unsuccessful\n");
				}
				break;
			default:
				open();
			}
		} while (!exit);
	}

	private String login() {

		String result = null;

		String[] login = UserInterface.login();
		
//		Altered the select statement to match the database. 
//		Daniel Rumfelt 4/19/2021
		String sql = "SELECT user_Id, user_First_Name FROM users WHERE user_Name = ? AND user_Password = ? AND user_Status = \"Active\"";
		try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
			ps.setString(1, login[0]);
			ps.setString(2, login[1]);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				userID = rs.getInt("user_Id");
				customer = rs.getString("user_First_Name");
				result = rs.getString("user_First_Name");
				
				System.out.println("\nThank you, " + customer + " you have successfully logged in.");
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
			try {
				createCartItem();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	private void createCartItem() throws SQLException {
//		Added Method 4-18-2021
//		Bruce Brown
		
		System.out.println("Add (Create) item to cart...");
		readProducts();
		System.out.println("What is the product ID of the item you wish to add?");
		int item = sc.nextInt();
		sc.nextLine();
		System.out.println("How Many Would You Like to Add?");
		int quantity = sc.nextInt();
		sc.nextLine();
		for (int i = 0; i < quantity; i++) {
			String sqlInsert = "INSERT INTO cst341nproject.shopping_cart (user_Id, product_Id) VALUES (?, ?)";
			try (PreparedStatement ps = con.getConnection().prepareStatement(sqlInsert)) {
				ps.setInt(1, userID);
				ps.setInt(2, item);
				ps.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Item ID: " + item + " Was Added " + quantity + " Time(s)!");
		System.out.println("\nPress \"ENTER\" to continue...\n");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		shop();
	}


	private void readCartItems() {
//		Added Method 4-18-2021
//		Daniel Rumfelt
//		
		System.out.println("View (Read) cart...\n");
		System.out.println(customer + ", Items currently in your shopping cart.");
		String sql = "SELECT shopping_cart.product_Id, products.product_Name, products.product_Price\n"
				+ "FROM cst341nproject.products\n"
				+ "JOIN  cst341nproject.shopping_cart on shopping_cart.product_Id = products.product_Id\n"
				+ "WHERE shopping_cart.user_Id = ?";
		try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
			ps.setInt(1, userID);
			ResultSet rs = ps.executeQuery();
			System.out.println("Product ID   Product Name     Product Price");
			System.out.println("--------------------------------------------");
			Double total = 0.00;
			while (rs.next()) {
				System.out.printf("%-12s %-15s  $%,.2f\n",rs.getInt("product_Id"),rs.getString("product_Name"),
						rs.getDouble("product_Price"));
				
				total = total + rs.getDouble("product_Price");
			}
			System.out.println("--------------------------------------------");
			System.out.printf("%-29s $%,.2f\n","Total", total);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\nPress \"ENTER\" to continue...\n");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void deleteCartItem() {
		
//		Added Method 4-18-2021
//		Bruce Brown
		
		System.out.println("Delete from cart...");
		System.out.println("Choose from the following items to delete:");
		readCartItems();
		System.out.println("Type the Product ID of the item you wish to remove, and press enter.");
		int id = sc.nextInt();
		sc.nextLine();
		String sql = "DELETE FROM cst341nproject.shopping_cart WHERE product_Id = ? and user_Id = ? ";
		try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.setInt(2, userID);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Deleting " + id + " from Your Cart...\n");
		System.out.println("\nPress \"ENTER\" to continue...\n");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		shop();
	}
	

	private void createProduct() throws SQLException {
//		Added Method 4-18-2021
//		Daniel Rumfelt
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
