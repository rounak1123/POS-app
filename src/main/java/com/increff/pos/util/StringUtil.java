package com.increff.pos.util;


import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? "" : s.trim().toLowerCase();
	}

	public static String trimZeros(String input) {
		input = input.trim();
		if (input.contains(".")) {
			String[] parts = input.split("\\.");
			if(parts.length > 2)
				return input;
			String integerPart = parts[0].replaceAll("^0+", "");
			String decimalPart = parts[1].replaceAll("0+$", "");
			if (integerPart.isEmpty() && decimalPart.isEmpty()) {
				return "0";
			} else if (integerPart.isEmpty()) {
				return "0." + decimalPart;
			} else if (decimalPart.isEmpty()) {
				return integerPart;
			} else {
				return integerPart + "." + decimalPart;
			}
		} else {
			input = input.replaceAll("^0+", "");
			if(input.isEmpty()) return "0";
			return input;
		}
	}

	public static boolean isValidInteger(String s) {
		try{

			if(Integer.valueOf(s) <= 0)
				return false;
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}

	public static boolean isValidDouble(String s) {
		try{
			String regex = "[-+]?\\d+(\\.\\d+)?";
			 if(!Pattern.matches(regex, s))
				 return false;
			if(s.length() > 30 ||Double.valueOf(s) <= 0)
				return false;
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}

}
