package com.koobest.m.supermarket.toolkits.regex.xmlparse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.koobest.m.supermarket.database.NAME;

public class RegexParse {
	public static void parsePaymentList(SQLiteDatabase db, String text){
		List<String> invalidPaymentInQuoteEdit = new ArrayList<String>(); 
		Cursor cursor = db.query(NAME.TABLE_QUOTE_TO_PAYMENT, 
				new String[]{NAME.PAYMENT_ID}, null, null, null, null, null);
		while(cursor.moveToNext()){
			invalidPaymentInQuoteEdit.add(cursor.getString(0));
		}
		cursor.close();
		db.delete(NAME.TABLE_PAYMENT, null, null);
		//start matcher
		Log.e("payment",text);
		Matcher matcher = Pattern.compile("<payment_method>(?s:.*?)"
    			+"<id>([^<]*)</id>(?s:.*?) #payment ID $1 \n"
    			+"<name>([^<]*)</name>(?s:.*?) #name $2 \n" 
    			+"<comment>((?s:.*?))</comment>(?s:.*?) #comment $3 \n"
    			+"</payment_method>", Pattern.COMMENTS).matcher(text);
		ContentValues values = new ContentValues();
		while(matcher.find()){
			Log.e("payment",matcher.group(1)+";"+matcher.group(2)+";"+matcher.group(3));
			values.put(NAME.PAYMENT_ID, matcher.group(1));
			values.put(NAME.PAYMENT_NAME, matcher.group(2));
			values.put(NAME.PAYMENT_COMMENT, matcher.group(3));
			db.insert(NAME.TABLE_PAYMENT, null, values);
			values.clear();
			invalidPaymentInQuoteEdit.remove(matcher.group(1));
		}
		//delete invalid payment in quote
		values.putNull(NAME.PAYMENT_ID);
		for(String id:invalidPaymentInQuoteEdit){
			db.update(NAME.TABLE_QUOTE_TO_PAYMENT, values, NAME.PAYMENT_ID+"=\""+id+"\"", null);
		}
		db.delete(NAME.TABLE_QUOTE_TO_PAYMENT, NAME.PAYMENT_ID+" IS NULL AND "+NAME.PAYTERM_ID+"=-1", null);
	}
	
	//
	public static void parsePaymentTermList(SQLiteDatabase db, String text){
		List<String> invalidTermInQuoteEdit = new ArrayList<String>(); 
		Cursor cursor = db.query(NAME.TABLE_QUOTE_TO_PAYMENT, 
				new String[]{NAME.PAYTERM_ID}, null, null, null, null, null);
		while(cursor.moveToNext()){
			invalidTermInQuoteEdit.add(String.valueOf(cursor.getInt(0)));
		}
		cursor.close();
		db.delete(NAME.TABLE_PAYMENT_TERM, null, null);
		//start matcher
		Matcher matcher = Pattern.compile("<payment_term>(?s:.*?)"
    			+"<id>(.*?)</id>(?s:.*?) #payment ID $1 \n"
    			+"<deposit>(.*?)</deposit>(?s:.*?) #deposit $2 \n" 
    			+"<grace_period>(.*?)</grace_period>(?s:.*?) #comment $3 \n"
    			+"<discount>(.*?)</discount>(?s:.*?) #discount $4 \n" 
    			+"<description>(.*?)</description>(?s:.*?) #comment $5 \n"
    			+"</payment_term>", Pattern.COMMENTS).matcher(text);
		ContentValues values = new ContentValues();
		while(matcher.find()){
			values.put(NAME.PAYTERM_ID, Integer.valueOf(matcher.group(1)));
			values.put(NAME.DEPOSIT, Integer.valueOf(matcher.group(2)));
			values.put(NAME.GRACE_PERIOD, Integer.valueOf(matcher.group(3)));
			values.put(NAME.DISCOUNT, Double.valueOf(matcher.group(4)));
			values.put(NAME.DESCRIPTION, matcher.group(5));
			db.insert(NAME.TABLE_PAYMENT_TERM, null, values);
			values.clear();
		    invalidTermInQuoteEdit.remove(matcher.group(1));
		}
		//delete invalid payment term in quote
		values.put(NAME.PAYTERM_ID, -1);
		for(String termId:invalidTermInQuoteEdit){
			db.update(NAME.TABLE_QUOTE_TO_PAYMENT, values, NAME.PAYTERM_ID+"="+termId, null);
		}
		db.delete(NAME.TABLE_QUOTE_TO_PAYMENT, NAME.PAYMENT_ID+" IS NULL AND "+NAME.PAYTERM_ID+"=-1", null);
	}
}
