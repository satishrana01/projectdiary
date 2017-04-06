package org.projectdiaries.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private Connection conn;
	
	public DatabaseConnection(String url) {
        try {
        	Class.forName("org.sqlite.JDBC");
        	this.conn = DriverManager.getConnection(url);
        }
        catch(ClassNotFoundException cfe) {
        	cfe.printStackTrace();
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return this.conn;
	}

}
