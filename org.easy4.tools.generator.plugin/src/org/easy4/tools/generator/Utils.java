package org.easy4.tools.generator;

public class Utils {

	public static String firstUpper(String word){
		if (word != null){
			String firstChar = word.substring(0,1);
			return firstChar.toUpperCase() + word.substring(1);
		}
		return null;
	}
	
}
