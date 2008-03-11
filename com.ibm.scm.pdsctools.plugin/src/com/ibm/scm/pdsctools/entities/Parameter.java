package com.ibm.scm.pdsctools.entities;

public class Parameter {

	private String name;
	
	private boolean request;
	
	private boolean response;
	
	private String structure;
	
	private String datatype;
	
	public String toString(){
		StringBuffer sb = new StringBuffer("[");
		sb.append(name).append(",");
		sb.append(request ? "request" : "").append(",");
		sb.append(response ? "response" : "").append(",");
		sb.append(structure).append(",");
		sb.append(datatype).append("]").append("\n");
		return sb.toString();
	}

	/**
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the request
	 */
	public boolean isRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(boolean request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public boolean isResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(boolean response) {
		this.response = response;
	}

	/**
	 * @return the structure
	 */
	public String getStructure() {
		return structure;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setStructure(String structure) {
		this.structure = structure;
	}
}
