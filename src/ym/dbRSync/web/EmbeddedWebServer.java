package ym.dbRSync.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EmbeddedWebServer {

	public static void main(String[] args) throws Exception {

		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/dbRSync");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new TabSyncSrvlet()), "/TabSyncSrvlet");
		server.start();
	}

	public static class HelloServlet extends HttpServlet {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2675915700923507266L;
		private static final String greeting = "Hello World";

		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			System.out.println("Session requested!");
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(greeting);
		}
	}
}