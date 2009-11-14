package com.appspot.mypwbox;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class PicasaServlet extends HttpServlet {
	
	private static final int BUFFER_SIZE = 8096;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.setContentType("application/octet-stream");
		
		String img = req.getParameter("img");
		HttpURLConnection connection = null;
		InputStream input = null;
		OutputStream output = null;
		
		try {
			if (img != null && !"".equals(img)){
				byte[] buffer = new byte[BUFFER_SIZE];
				int size = 0;
				URL url = new URL(img);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				input = new BufferedInputStream(connection.getInputStream());
				output = resp.getOutputStream();
				while ((size=input.read(buffer)) > 0){
					output.write(buffer, 0, size);
				}
			}
		} catch (IOException ioe){
			throw ioe;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (output != null) {
					output.close();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (Exception e){
				System.out.println(e);
			}
		}
	}
}
