package org.projectdiaries.utility;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.projectdiaries.utility.DatabaseConnection;

public class MyDbConnectionListener implements ServletContextListener {

	public static String PDF_PATH = "";
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
		DatabaseConnection db = (DatabaseConnection) sc.getAttribute("db");
		Connection conn = db.getConnection();
		try {
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
		
		String url = sc.getInitParameter("url");
		PDF_PATH = sc.getRealPath("/resources").concat(File.separator);
		String DBPATH = sc.getRealPath("/resources").concat(File.separator).concat("project.db");
		url = url + DBPATH;
		
		DatabaseConnection db = new DatabaseConnection(url);
		sc.setAttribute("db", db);
		System.out.println("Database Connection Initialized Successfully");	
	}

}
