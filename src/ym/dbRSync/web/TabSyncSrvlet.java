package ym.dbRSync.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TabSyncSrvlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9095305299299294517L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("TabSyncSrvlet - Request start!");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.write("{\"message\":\"Hello World!\"}");		// Test Code
		
	}
}
