package com.rohitkolar.tmo.utility;

public class TMOValidator {

	public static boolean isEmpty(String str) {
		if(str == null)
			return true;
		if(str.length() == 0)
			return true;
		
		return false;
	}
}
