package com.koobest.m.supermarket.database;

import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
	public DatabaseHelper(Context context) {
		super(context, PROVIDER_NAME.DATABASE_NAME, null,
				PROVIDER_NAME.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//orders
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_ORDERS + " ("
				+ PROVIDER_NAME.ORDER_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.STATUS + " VARCHAR,"
				+ PROVIDER_NAME.STATUS_ID + " INTEGER,"
				+ PROVIDER_NAME.CUSTOMER_ID + " INTEGER,"
				+ PROVIDER_NAME.ORDER_COMMENT + " VARCHAR,"
				+ PROVIDER_NAME.ORDER_DATE + " DATE,"
				+ PROVIDER_NAME.ORDER_XML + " VARCHAR);");
        //about product
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_PRODUCT + " ("
				+ PROVIDER_NAME.PRODUCT_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.PRODUCT_NAME + " VARCHAR,"
				+ PROVIDER_NAME.PRODUCT_XML + " VARCHAR,"
				+ PROVIDER_NAME.MINQTY + " REAL,"
				+ PROVIDER_NAME.QTY_UNIT + " TEXT,"
				+ PROVIDER_NAME.VOLUME + " DOUBLE,"
				+ PROVIDER_NAME.WEIGHT + " DOUBLE," 
				+ PROVIDER_NAME.LAST_ACCESS_DATE + " INTEGER);");
		
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_PRODUCT_PRICE
				+ " (" + PROVIDER_NAME.PRODUCT_ID+ " INTEGER," 
				+ PROVIDER_NAME.QTY	+ " DOUBLE," 
				+ PROVIDER_NAME.PRICE + " DOUBLE,"+
				"UNIQUE("+PROVIDER_NAME.PRODUCT_ID+","+PROVIDER_NAME.
                QTY+"));");
		
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_PRODUCT_IMAGE
				+ " (" + PROVIDER_NAME.PRODUCT_IMAGE_ID+ " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.PRODUCT_ID + " INTEGER,"
				+ PROVIDER_NAME.IMAGE + " TEXT);");
		
		
		//the follow two are used by search product in the way of barcode 
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_BARCODE_PRODUCT
				+ " (" + PROVIDER_NAME.BARCODE+ " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.PRODUCT_NUM + " INTEGER,"
				+ PROVIDER_NAME.PRODUCT_IDS + " TEXT,"
				//this is used to mark when a record inserted to this table
				//,beside it could decide whether to delete products in TABLE_PRODUCT_DESC.
				//more information could get form method{onDeleteBarcodeListener(String,String[])}
				//in SupermarketContentProvider
		        + PROVIDER_NAME.CREATE_DATE + " INTEGER);");

		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_PRODUCT_DESC
				+ " (" + PROVIDER_NAME.PRODUCT_ID+ " INTEGER PRIMARY KEY," 
				+ PROVIDER_NAME.PRODUCT_NAME + " VARCHAR,"
				+ PROVIDER_NAME.MANUFACTURER + " VARCHAR," 
				+ PROVIDER_NAME.PRICE + " VARCHAR,"
				+ PROVIDER_NAME.IMAGE + " TEXT,"
				//this date is used to distinguish whether
				//a record should be delete when delete a barcode in
				//TABLE_BARCODE_PRODUCT 
		        + PROVIDER_NAME.LAST_UPDATE_DATE + " INTEGER);");
		
		
		//about wishlist
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_WISHLIST + " ("
				+ PROVIDER_NAME.WISHLIST_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.CUSTOMER_ID + " INTEGER);");
		
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_WISHLIST_PRODUCT+ " (" 
				+ PROVIDER_NAME.PRODUCT_ID + " INTEGER," 
				+ PROVIDER_NAME.WISHLIST_ID + " VARCHAR," +
				"UNIQUE("+PROVIDER_NAME.PRODUCT_ID+","+PROVIDER_NAME.
                WISHLIST_ID+"));");
		
        //about quote
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_QUOTES + " ("
				+ PROVIDER_NAME.QUOTE_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.CUSTOMER_ID + " INTEGER,"
				+ PROVIDER_NAME.DESCRIPTION + " VARCHAR,"	
                + PROVIDER_NAME.BILLING_ADDRESS_ID+" INTEGER,"
                + PROVIDER_NAME.SHIPPING_ADDRESS_ID+" INTEGER,"
                + PROVIDER_NAME.CONTAINER_CLASS_ID+" INTEGER,"
//                + PROVIDER_NAME.PAYMENT_ID+" TEXT,"
//                + PROVIDER_NAME.PAYMENT_NAME+" TEXT,"
                + PROVIDER_NAME.VOLUME + " DOUBLE,"
				+ PROVIDER_NAME.WEIGHT + " DOUBLE,"
				+ PROVIDER_NAME.ORDER_ID + " INTEGER,"
				+ PROVIDER_NAME.TEMP_ORDER_ID + " INTEGER,"
				+ PROVIDER_NAME.ISUPDATE + " INTEGER);");
				//+ PROVIDER_NAME.TOTAL_PRICE + " REAL);");
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_QUOTE_TO_PAYMENT + " ("
				+ PROVIDER_NAME.QUOTE_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.PAYMENT_ID+" VARCHAR,"
				+ PROVIDER_NAME.PAYTERM_ID+" INTEGER);");
		
				//+ PROVIDER_NAME.TOTAL_PRICE + " REAL);");
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_QUOTE_PRODUCT + " ("
				+ PROVIDER_NAME.QUOTE_PRODUCT_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.QUOTE_ID + " INTEGER,"
				+ PROVIDER_NAME.PRODUCT_ID + " INTEGER,"
				+ PROVIDER_NAME.PRODUCT_NAME + " VARCHAR,"
//				+ PROVIDER_NAME.PRICE + " DOUBLE,"
				+ PROVIDER_NAME.QTY	+ " DOUBLE);");
		createProductDeletionTrigger(db);
		
		//payment
		db.execSQL("CREATE TABLE " + NAME.TABLE_PAYMENT + " ("
				+ NAME.PAYMENT_ID+" VARCHAR,"
                + NAME.PAYMENT_NAME+" VARCHAR,"
                + NAME.PAYMENT_COMMENT+" TEXT);");
				
		db.execSQL("CREATE TABLE " + NAME.TABLE_PAYMENT_TERM + " ("
				+ NAME.PAYTERM_ID+" INTEGER,"
                + NAME.DEPOSIT+" INTEGER,"
                + NAME.GRACE_PERIOD + " INTEGER,"
				+ NAME.DISCOUNT + " DOUBLE,"
				+ NAME.DESCRIPTION + " TEXT);");
		
		//about order_template(Prewired Order)
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS + " ("
				+ PROVIDER_NAME.NAME + " VARCHAR,"
				+ PROVIDER_NAME.VALUE + " VARCHAR,"
				+ PROVIDER_NAME.NUMBER + " INTEGER,"	
                + PROVIDER_NAME.TEMP_IDS+" VARCHAR,"
                +"UNIQUE("+PROVIDER_NAME.NAME+","+PROVIDER_NAME.
                VALUE+"));");
				//+ PROVIDER_NAME.TOTAL_PRICE + " REAL);");

		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_TEMP_ORDERS + " ("
				+ PROVIDER_NAME.ORDER_ID + " INTEGER PRIMARY KEY,"
				+ PROVIDER_NAME.NAME + " TEXT,"
				+ PROVIDER_NAME.CONTAINER_ID + " INTEGER,"
				+ PROVIDER_NAME.END_DATE + " INTEGER,"
				+ PROVIDER_NAME.XML + " TEXT);");
		
		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_TEMP_ORDER_DETAIL_FACETS + " ("
				+ PROVIDER_NAME.ORDER_ID + " INTEGER,"
				+ PROVIDER_NAME.NAME + " VARCHAR,"
				+ PROVIDER_NAME.VALUE + " VARCHAR);");
				//+ PROVIDER_NAME.TOTAL_PRICE + " REAL);");

		db.execSQL("CREATE TABLE " + PROVIDER_NAME.TABLE_TEMP_ORDER_PRODUCTS + " ("
				+ PROVIDER_NAME.ORDER_ID + " INTEGER,"
				+ PROVIDER_NAME.PRODUCT_ID + " INTEGER,"          //no need
				+ PROVIDER_NAME.NAME + " TEXT,"
				+ PROVIDER_NAME.PRICE + " DOUBLE,"
				+ PROVIDER_NAME.QTY + " INTEGER,"
				+ PROVIDER_NAME.QTY_UNIT + " INTEGER,"
				+ PROVIDER_NAME.IMAGE +" BLOB);");
	}
	
	private void createProductDeletionTrigger(SQLiteDatabase db) {
        db.execSQL(""
                + " CREATE TRIGGER " + PROVIDER_NAME.TABLE_PRODUCT +
                           "Delete DELETE ON " + PROVIDER_NAME.TABLE_PRODUCT
                + " BEGIN"
                + "   DELETE FROM " + PROVIDER_NAME.TABLE_PRODUCT_PRICE
                + "     WHERE " + PROVIDER_NAME.PRODUCT_ID + "=OLD." + PROVIDER_NAME.PRODUCT_ID + " ;"
                + "   DELETE FROM " + PROVIDER_NAME.TABLE_PRODUCT_IMAGE
                + "     WHERE " + PROVIDER_NAME.PRODUCT_ID + "=OLD." + PROVIDER_NAME.PRODUCT_ID + " ;"
                + " END");
        db.execSQL(""
                + " CREATE TRIGGER " + PROVIDER_NAME.TABLE_WISHLIST +
                           "Delete DELETE ON " + PROVIDER_NAME.TABLE_WISHLIST
                + " BEGIN"
                + "   DELETE FROM " + PROVIDER_NAME.TABLE_WISHLIST_PRODUCT
                + "     WHERE " + PROVIDER_NAME.WISHLIST_ID + "=OLD." + PROVIDER_NAME.WISHLIST_ID + " ;"
                + " END");
        db.execSQL(""
                + " CREATE TRIGGER " + PROVIDER_NAME.TABLE_QUOTES +
                           "Delete DELETE ON " + PROVIDER_NAME.TABLE_QUOTES
                + " BEGIN"
                + "   DELETE FROM " + PROVIDER_NAME.TABLE_QUOTE_PRODUCT
                + "     WHERE " + PROVIDER_NAME.QUOTE_ID + "=OLD." + PROVIDER_NAME.QUOTE_ID + " ;"
                + "   DELETE FROM " + PROVIDER_NAME.TABLE_QUOTE_TO_PAYMENT
                + "     WHERE " + PROVIDER_NAME.QUOTE_ID + "=OLD." + PROVIDER_NAME.QUOTE_ID + " ;"
                + " END");
    }
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
}
