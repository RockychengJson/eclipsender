package com.ibm.scm.pdsctools.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

public class HttpTool {

	private HttpClient client;
	
	private HttpClient getClient() {
		if (client == null) {
			this.client = new HttpClient();
		}
		return this.client;
	}
	
	public Document getHtmlDocument(String url, NameValuePair[] parameters) throws HttpException, IOException{
		PostMethod method = new PostMethod(url);
		if (parameters != null){
			method.setRequestBody(parameters);
		}
		int status = getClient().executeMethod(method);
		if (status == HttpStatus.SC_OK) {
			InputStream in = method.getResponseBodyAsStream();
			Tidy tidy = new Tidy();
			// Tell Tidy to convert HTML to XML
			tidy.setXmlOut(true); 
			tidy.setErrout(new PrintWriter(System.err));
			//	Convert files
			return tidy.parseDOM(in, System.out);
		}
		return null;
	}

}
