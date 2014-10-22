package com.koobest.m.supermarket.database;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtilities {
	private DatabaseUtilities(){};
    public static void deleteProduct(SQLiteDatabase database,String productId){
    	//1.delete product in Table QuoteProducts
    	database.delete(NAME.TABLE_QUOTE_PRODUCT, NAME.PRODUCT_ID+"="+productId, null);
    	//2.delete product in Product Series Tables
    	//2-1.delete product discount
    	database.delete(NAME.TABLE_PRODUCT_PRICE, NAME.PRODUCT_ID+"="+productId, null);
    	//2-2.delete product image
    	Cursor cursor = database.query(NAME.TABLE_PRODUCT_IMAGE, new String[]{NAME.IMAGE}, 
    			NAME.PRODUCT_ID+"="+productId, null, null, null, null);
    	File file;
    	while(cursor.moveToNext()){
    		file=new File(SAVE_PATH.FILE_PATH, cursor.getString(0));
    		if(file.exists()){
    			file.delete();
    		}
    	}
    	cursor.close();
    	database.delete(NAME.TABLE_PRODUCT_IMAGE,NAME.PRODUCT_ID+"="+productId,null);
    	//2-3.delete product product
    	database.delete(NAME.TABLE_PRODUCT, NAME.PRODUCT_ID+"="+productId, null);
    	//3.delete product detail
    	file=new File(SAVE_PATH.FILE_PATH, "pro" + productId + ".xml");
		if(file.exists()){
			file.delete();
		}
    }
    
    public static void deleteQuote(SQLiteDatabase database,int quoteId){
    	database.delete(NAME.TABLE_QUOTES, NAME.QUOTE_ID+"="+quoteId, null);
    }
}
