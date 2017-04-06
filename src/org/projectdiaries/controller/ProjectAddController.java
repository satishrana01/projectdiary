package org.projectdiaries.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.projectdiaries.dao.ProjectDAO;
import org.projectdiaries.utility.DatabaseConnection;
import org.projectdiaries.utility.MyDbConnectionListener;
import org.projectdiaries.utility.PdfGenerator;

public class ProjectAddController extends HttpServlet {
	

	private static final long serialVersionUID = -6593239576458672566L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext sc = request.getServletContext();
		DatabaseConnection db = (DatabaseConnection) sc.getAttribute("db");
		Connection conn = db.getConnection();
		RequestDispatcher rd = null;
		List<List<String>> biggerlist = new ArrayList<List<String>>();
		
		String projectName = null; 
		String billingRate = null;
		ProjectDAO dao = null;
		ResultSet rs = null;
		if(null != request.getParameter("projname") && !"".equals(request.getParameter("projname")))
			projectName = request.getParameter("projname");
		
		if(null != request.getParameter("rate") && !"".equals(request.getParameter("rate").trim()))
			billingRate = request.getParameter("rate");
		
		if (null != request.getParameter("start")) {
		    if(null != projectName && (null != billingRate && !"".equals(billingRate.trim()))) {
		    	dao = new ProjectDAO(conn);
		    	String stopTime = null;
		    	rs = dao.getTaskStatus(projectName);
		    	try {
					if(rs.next()) {
						stopTime = (String) rs.getString("end_time");
						if(null == stopTime || "".equals(stopTime.trim()) ) {
							request.setAttribute("errormsg", "Current Task not Completed");
							rd = request.getRequestDispatcher("/index.jsp");
							rd.forward(request, response);
				    	} else {
				    		dao.insertProjectRow(projectName, billingRate);
							rs = dao.fetchSelectedProjectRows(projectName);
							try {
								int count = 1;
								while(rs.next()){ 
									List<String> smallerlist = new ArrayList<String>();
									smallerlist.add(String.valueOf(count));
								    smallerlist.add(rs.getString("project_name"));
									smallerlist.add(rs.getString("start_time"));
									smallerlist.add(rs.getString("end_time"));
									smallerlist.add(rs.getString("date"));
									smallerlist.add(rs.getString("rate"));
									smallerlist.add(rs.getString("wages"));
									smallerlist.add(rs.getString("status"));
									biggerlist.add(smallerlist);
									count++;
								}
								request.setAttribute("mylist", biggerlist);
								rd = request.getRequestDispatcher("/index.jsp");
								rd.forward(request, response);
								rs.close();
							} catch (SQLException e) {
								e.printStackTrace();
							} finally {
							}	
				    	}
					} else {
						dao.insertProjectRow(projectName, billingRate);
						rs = dao.fetchSelectedProjectRows(projectName);
						try {
							int count = 1;
							while(rs.next()){ 
								List<String> smallerlist = new ArrayList<String>();
								smallerlist.add(String.valueOf(count));
							    smallerlist.add(rs.getString("project_name"));
								smallerlist.add(rs.getString("start_time"));
								smallerlist.add(rs.getString("end_time"));
								smallerlist.add(rs.getString("date"));
								smallerlist.add(rs.getString("rate"));
								smallerlist.add(rs.getString("wages"));
								smallerlist.add(rs.getString("status"));
								biggerlist.add(smallerlist);
								count++;
							}
							request.setAttribute("mylist", biggerlist);
							rd = request.getRequestDispatcher("/index.jsp");
							rd.forward(request, response);
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							rs.close();
						}
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}	
		    } else if(null != projectName && (null == billingRate || "".equals(billingRate.trim()))) {
		    	request.setAttribute("errormsg", "Please enter Billing Rate");
				rd = request.getRequestDispatcher("/index.jsp");
				rd.forward(request, response);
		    } else {
				request.setAttribute("errormsg", "No Projets Selected");
				rd = request.getRequestDispatcher("/index.jsp");
				rd.forward(request, response);
		    }
		} else if (null != request.getParameter("stop")) {
			if(null != projectName) {
		    	dao = new ProjectDAO(conn);
		    	rs = dao.getTaskStatus(projectName);
		    	try {
					if(rs.next()){
						if(null == (String) rs.getString("end_time")) {
							dao.updateProjectRow(projectName);
							rs = dao.fetchSelectedProjectRows(projectName);
							try {
								int count = 1;
								while(rs.next()){ 
									List<String> smallerlist = new ArrayList<String>();
									smallerlist.add(String.valueOf(count));
								    smallerlist.add(rs.getString("project_name"));
									smallerlist.add(rs.getString("start_time"));
									smallerlist.add(rs.getString("end_time"));
									smallerlist.add(rs.getString("date"));
									smallerlist.add(rs.getString("rate"));
									smallerlist.add(rs.getString("wages"));
									smallerlist.add(rs.getString("status"));
									biggerlist.add(smallerlist);
									count++;
								}
								request.setAttribute("mylist", biggerlist);
								rd = request.getRequestDispatcher("/index.jsp");
								rd.forward(request, response);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						else {
							request.setAttribute("errormsg", "Current Task Already Completed");
							rd = request.getRequestDispatcher("/index.jsp");
							rd.forward(request, response);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				request.setAttribute("errormsg", "No Projets Selected");
				rd = request.getRequestDispatcher("/index.jsp");
				rd.forward(request, response);
			}
		} else if (null != request.getParameter("invoice")) {
			dao = new ProjectDAO(conn);
			rs = dao.fetchCompletedProjectRows(projectName);
			
			try {
				String fileName = new PdfGenerator().getPDfReport(rs);
				String filePath = MyDbConnectionListener.PDF_PATH+fileName;
		        File downloadFile = new File(filePath);
		        FileInputStream inStream = new FileInputStream(downloadFile);
		         
		        // if you want to use a relative path to context root:
		        String relativePath = getServletContext().getRealPath("");
		        System.out.println("relativePath = " + relativePath);
		         
		        // obtains ServletContext
		        ServletContext context = getServletContext();
		         
		        // gets MIME type of the file
		        String mimeType = context.getMimeType(filePath);
		        if (mimeType == null) {        
		            // set to binary type if MIME mapping not found
		            mimeType = "application/octet-stream";
		        }
		        System.out.println("MIME type: " + mimeType);
		         
		        // modifies response
		        response.setContentType(mimeType);
		        response.setContentLength((int) downloadFile.length());
		         
		        // forces download
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		        response.setHeader(headerKey, headerValue);
		         
		        // obtains response's output stream
		        OutputStream outStream = response.getOutputStream();
		         
		        byte[] buffer = new byte[4096];
		        int bytesRead = -1;
		         
		        while ((bytesRead = inStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		         
		        inStream.close();
		        outStream.close();  
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
		}
	}
}
