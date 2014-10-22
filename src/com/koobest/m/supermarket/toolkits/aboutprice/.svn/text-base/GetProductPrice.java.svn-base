package com.koobest.m.supermarket.toolkits.aboutprice;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;

public class GetProductPrice {
	public static final double getPriceInBaseCurrency(Context context,int productId,int qty){
		SQLiteDatabase database=new DatabaseHelper(context).getReadableDatabase();
		Cursor cursor = database
			.query(NAME.TABLE_PRODUCT_PRICE,
				new String[] { NAME.PRICE },
				NAME.PRODUCT_ID+ "=" + productId
				+ " and " + NAME.QTY + "<=" + qty,
				null, null,null,NAME.QTY+" DESC");
		try{
			if(!cursor.moveToFirst()){
				cursor.close();
				cursor = database.query(NAME.TABLE_PRODUCT_PRICE,
    				new String[] { NAME.PRICE },
    				NAME.PRODUCT_ID+ "=" + productId,
    				null,null,null, NAME.QTY+" ASC");
				if(!cursor.moveToFirst()){
					throw new IllegalArgumentException("need price");
				}
			}
			return cursor.getDouble(0);
    	}finally{
    		cursor.close();
    		database.close();
    	}
    }
	
//	public static final double getPriceInBaseCurrency(SQLiteDatabase database,int productId,int qty){
//    	Cursor cursor = database
//    		.query(NAME.TABLE_PRODUCT_PRICE,
//    				new String[] { NAME.PRICE },
//    				NAME.PRODUCT_ID+ "=" + productId
//    				+ " and " + NAME.QTY + "<=" + qty,
//    				null, null,null,NAME.QTY+" DESC");
//    	try{
//    		if(!cursor.moveToFirst()){
//        		cursor.close();
//        		cursor = database
//        		.query(NAME.TABLE_PRODUCT_PRICE,
//        				new String[] { NAME.PRICE },
//        				NAME.PRODUCT_ID+ "=" + productId,
//        				null,null,null, NAME.QTY+" ASC");
//        	}
//    		return cursor.getDouble(0);
//    	}finally{
//    		cursor.close();
//    	}
//    }
	private GetProductPrice(){};
}
