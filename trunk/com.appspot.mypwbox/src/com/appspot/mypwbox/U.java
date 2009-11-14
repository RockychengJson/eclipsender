package com.appspot.mypwbox;

public class U {

	public final static boolean isEmpty (String s){
		return (s == null || "".equals(s));
	}
	
	public final static String invert(String s) {
		if (isEmpty(s)){
			return s;
		} else {
			StringBuffer r = new StringBuffer();
			for(int i=s.length(); i > 0; i--) {
				r.append(s.charAt(i-1));
			}
			return r.toString();
		}
	}
}
