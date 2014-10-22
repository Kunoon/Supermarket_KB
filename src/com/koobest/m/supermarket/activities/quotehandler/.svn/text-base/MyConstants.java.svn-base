package com.koobest.m.supermarket.activities.quotehandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MyConstants {
	private static final Matcher PRICE_MATCHER = Pattern.compile("(?<=\\d)(?=(?:\\d\\d\\d)++(?:\\.|$))").matcher("");
	/**
	 * format price from \d\d\d\d\d...to \d\d\d,...(\d\d\d,)...\d\d\d
	 * @param price
	 * @return
	 */
	public static String getFormatedPrice(String price){
		return PRICE_MATCHER.reset(price).replaceAll(",");
	}
}
