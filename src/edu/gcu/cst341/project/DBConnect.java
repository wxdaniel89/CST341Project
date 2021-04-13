package edu.gcu.cst341.project;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
	

	private static String URL 	= "jdbc:mysql://localhost:3306/cst341nproject";
	private static String USER 	= "root";
	private static String PASS 	= "root";
	
	private static Connection conn;
    private static DBConnect instance;
	
    public DBConnect() {
        try {
        	try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            DBConnect.conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public static Connection getConnection() {
        return conn;
    }
}
