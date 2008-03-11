package org.easy4.tools.generator.entities;

import java.util.ArrayList;

import org.easy4.tools.generator.Utils;

public class Table {

	private String name;
	
	private String description;
	
	private Field[] fields;
	
	private Field[] primaryKeys;
	
	public String getNameAsClassName(){
		if (name != null){
			String[] words = name.split("_");
			String tempName = "";
			for(int i=0; i<words.length; i++){
				tempName += Utils.firstUpper(words[i]);
			}
			return tempName;
		}
		return null;
	}

	/**
	 * @return the columns
	 */
	public Field[] getFields() {
		return fields;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setFields(Field[] fields) {
		this.fields = fields;
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
	 * @return the primaryKeys
	 */
	public Field[] getPrimaryKeys() {
		if (primaryKeys == null){
			ArrayList tempList = new ArrayList();
			for (int i=0; i<fields.length; i++){
				if (fields[i].isPrimaryKey()){
					tempList.add(fields[i]);
				}
			}
			primaryKeys = (Field[]) tempList.toArray(new Field[0]);
		}
		return primaryKeys;
	}

	/**
	 * @param primaryKeys the primaryKeys to set
	 */
	public void setPrimaryKeys(Field[] primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
}
