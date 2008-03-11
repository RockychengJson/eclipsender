package com.ibm.scm.pdsctools.entities;


public class Operation {

	private String name;
	
	private String description;
	
	private Parameter[] parameters;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(name).append(":");
		sb.append(description).append(":");
		sb.append(parameters != null ? parameters.length : 0);
		sb.append("]").append("\n");
		return  sb.toString();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the parameters
	 */
	public Parameter[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
}
