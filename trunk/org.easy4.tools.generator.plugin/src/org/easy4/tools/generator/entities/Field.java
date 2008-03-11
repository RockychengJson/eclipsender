package org.easy4.tools.generator.entities;

import org.easy4.tools.generator.Utils;

public class Field {
	
	public final static String STRING = "String";
	
	public final static String INT = "int";
	
	public final static String DATE = "java.util.Date";
	
	public final static String FLOAT = "float";
	
	public final static String LONG = "long";
	
	public final static String OBJECT = "Object";
	
	private String name;
	
	private String description;
	
	private String type;
	
	private boolean primaryKey = false;
	
	private boolean nullable = true;
	
	private int length;
	
	public String getNameWithFirstUpper(){
		if (name != null){
			String[] words = name.split("_");
			String tempName = "";
			for(int i=0; i<words.length; i++){
				tempName += Utils.firstUpper(words[i].toLowerCase());
			}
			return tempName;
		}
		return null;
	}
	
	public String getNameWithFirstLower(){
		if (name != null){
			String[] words = name.split("_");
			String tempName = words[0].toLowerCase();
			for(int i=1; i<words.length; i++){
				tempName += Utils.firstUpper(words[i].toLowerCase());
			}
			return tempName;
		}
		return null;
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
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
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
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
