package com.ibm.scm.pdsctools.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.scm.pdsctools.entities.Operation;
import com.ibm.scm.pdsctools.entities.Parameter;

public class XmlMaker {
	
	private IFile file;
	
	private Document document;
	
	private Element root;
	
	public XmlMaker(IFile file){
		this.file = file;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			root = document.createElement("operations");
			document.setDocumentURI("http://w3.ibm.com/scm");
			document.appendChild(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void make() {
		try{
			Source source = new DOMSource(document);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        Result result = new StreamResult(out);
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.setOutputProperty("indent", "yes");
	        xformer.transform(source, result);
	        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	        if (file.exists()) {
	        	file.appendContents(in, true, true, null);
			} else {
				file.create(in, true, null);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void addOperation(Operation operation) throws ParserConfigurationException {
		Element item = document.createElement("operation");
		item.setAttribute("name", operation.getName());
		Element input = document.createElement("input");
		Element output = document.createElement("output");
		if (operation.getParameters() != null){
			for(int i=0; i<operation.getParameters().length; i++){
				Parameter parameter = operation.getParameters()[i];
				Element param = document.createElement("param");
				param.setAttribute("name", parameter.getName());
				param.setAttribute("type", parameter.getDatatype().toLowerCase());
				if (parameter.isRequest()){
					input.appendChild(param);
				}
				if (parameter.isResponse()){
					output.appendChild(param);
				}
			}
		}
		item.appendChild(input);
		item.appendChild(output);
		root.appendChild(item);
	}
}
