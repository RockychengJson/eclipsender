package com.ibm.scm.pdsctools.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.scm.pdsctools.entities.Operation;
import com.ibm.scm.pdsctools.entities.Parameter;

public class XmlTool {
	
	private XPath xPath;

	public List getOperationList(Document doc) throws XPathExpressionException{
		ArrayList opList = new ArrayList();
		String expression = "/html/body/form/center/table/tbody/tr[count(td[@width])=1]";
		getXPath();
		NodeList opNodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);
		for (int i=0; i<opNodes.getLength(); i++){
			Node opNode = opNodes.item(i);
			opList.add(getOperation(opNode));
		}
		return opList;
	}
	
	public List getDetails(Document doc) throws XPathExpressionException{
		ArrayList paramList = new ArrayList();
		String expression = "/html/body/form/center/table/tbody/tr[count(td[@width])=1]";
		getXPath();
		NodeList paramNodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);
		for (int i=0; i<paramNodes.getLength(); i++){
			Node paramNode = paramNodes.item(i);
			Parameter parameter = getParameter(paramNode);
			if (parameter != null) {
				paramList.add(parameter);
			}
		}
		return paramList;
	}
	
	private Operation getOperation(Node opNode) throws XPathExpressionException{
		NodeList tds = (NodeList) xPath.evaluate("td", opNode, XPathConstants.NODESET);
		Operation op = null;
		if (tds.getLength() == 3){
			op = new Operation();
			Node nameNode = (Node) xPath.evaluate("td/input", opNode, XPathConstants.NODE);
			op.setName(getAttribute(nameNode, "value"));
			Node descNode = tds.item(2);
			Node descText = descNode.getFirstChild();
			if (descText != null){
				op.setDescription(descText.getNodeValue());
			}
		}
		return op;
	}
	
	private Parameter getParameter(Node paramNode) throws XPathExpressionException{
		NodeList tds = (NodeList) xPath.evaluate("td", paramNode, XPathConstants.NODESET);
		Parameter parameter = null;
		if (tds.getLength() == 6){
			parameter = new Parameter();
			String name = (String) xPath.evaluate("font", tds.item(1), XPathConstants.STRING);
			if (isCommonParameter(name)){
				return null;
			}
			parameter.setName(name);
			Node reqNode = tds.item(2);
			parameter.setRequest("x".equalsIgnoreCase((reqNode.getFirstChild().getNodeValue())));
			Node resNode = tds.item(3);
			parameter.setResponse("x".equalsIgnoreCase((resNode.getFirstChild().getNodeValue())));
			String structure = (String) xPath.evaluate("font", tds.item(4), XPathConstants.STRING);
			parameter.setStructure(structure);
			String datatype = (String) xPath.evaluate("td[last()]", paramNode, XPathConstants.STRING);
			parameter.setDatatype(datatype);
		}
		return parameter;
	}
	
	private boolean isCommonParameter(String paramName) {
		String[] commonParameters = new String[]{
				"InfoMessage",
				"ErrorCode",
				"ErrorMessage",
				"SessionId",
				"SessionData"};
		for (int i=0; i<commonParameters.length; i++){
			if (commonParameters[i].equalsIgnoreCase(paramName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * To get the attribute from a DOM node
	 * @param node DOM node
	 * @param name attribute name
	 * @return attribute value
	 */
	private String getAttribute(Node node, String name){
		if (node != null){
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}
	
	private void getXPath() {
		if (xPath == null){
			XPathFactory xpfactory = XPathFactory.newInstance();
			xPath = xpfactory.newXPath();
		}
	}
}
