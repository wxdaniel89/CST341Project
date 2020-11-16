package edu.gcu.cst341.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
	
	private static String URL 	= "jdbc:mysql://localhost:3306/CST341Project";
	private static String USER 	= "root";
	private static String PASS 	= "password";
	
	private static Connection conn;
    private static DBConnect instance;
	
    public DBConnect() {
        try {
            this.conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
