package com.onpositive.slack.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FilesServlet extends HttpServlet {

	static HashMap<String, IContentProducer> content = new HashMap<>();
	static HashMap<IContentProducer, String> urlMap = new HashMap<>();
	static HashMap<String, IContentProducer> cpMap = new HashMap<>();

	static String url="http://9edfa547.ngrok.io/images";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String queryString = req.getQueryString();
		IContentProducer iContentProducer = content.get(queryString);
		resp.setContentType(iContentProducer.mimeType());
		resp.getOutputStream().write(iContentProducer.content());
		resp.setStatus(200);
	}

	public static synchronized String publish(IContentProducer producer) {
		String key = producer.key();
		if (key != null) {
			if (cpMap.containsKey(key)) {
				IContentProducer iContentProducer = cpMap.get(key);
				String string = urlMap.get(iContentProducer);
				content.put(string, producer);
				cpMap.put(key, producer);
				return url+"?"+string;
			}
			cpMap.put(key, producer);			
		}
		String string = UUID.randomUUID().toString();
		content.put(string, producer);
		urlMap.put(producer, string);
		return url+"?"+string;
	}
}
