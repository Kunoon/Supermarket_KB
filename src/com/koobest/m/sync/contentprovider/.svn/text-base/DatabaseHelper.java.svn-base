package com.koobest.m.sync.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    
    DatabaseHelper(Context context) {
        super(context, SYNC_PROVIDER_NAME.DATABASE_NAME, null, SYNC_PROVIDER_NAME.DATABASE_VERSION);
       }

	@Override
	public void onCreate(SQLiteDatabase db) {
		 //customer profile
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_CUSTOMER  + " (" 
				    +SYNC_PROVIDER_NAME.CUSTOMER_ID+" INTEGER PRIMARY KEY,"    
                    +SYNC_PROVIDER_NAME.CUSTOMER_FIRSTNAME+" TEXT,"
                    +SYNC_PROVIDER_NAME.CUSTOMER_LASTNAME+" TEXT,"
                    +SYNC_PROVIDER_NAME.EMAIL+" TEXT,"
                    +SYNC_PROVIDER_NAME.TELEPHONE+" TEXT,"
                    +SYNC_PROVIDER_NAME.DATE_ADDED+" TEXT,"
                    +SYNC_PROVIDER_NAME.CUSTOMER_XML+" TEXT);");
		 
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_ADDRESS + " (" 
				    +SYNC_PROVIDER_NAME._ID+" INTEGER PRIMARY KEY,"
				    +SYNC_PROVIDER_NAME.CUSTOMER_ID+" INTEGER,"  
				    +SYNC_PROVIDER_NAME.ADDRESS_ID+" INTEGER,"
				    +SYNC_PROVIDER_NAME.ADDRESS_1+" VARCHAR,"
				    +SYNC_PROVIDER_NAME.ADDRESS_2+" VARCHAR,"
                    +SYNC_PROVIDER_NAME.CITY+" VARCHAR ," 
                    +SYNC_PROVIDER_NAME.ZONE+" VARCHAR);");
		 
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_PAYMENT + " (" 
				    +SYNC_PROVIDER_NAME._ID+" INTEGER PRIMARY KEY,"
                    +SYNC_PROVIDER_NAME.CUSTOMER_ID+" INTEGER , "
		 		    +SYNC_PROVIDER_NAME.PAYMENT_ID+" TEXT ,"
                    +SYNC_PROVIDER_NAME.TITLE+" TEXT," 
                    +SYNC_PROVIDER_NAME.DESCRIPTION+" TEXT," 
                    +SYNC_PROVIDER_NAME.DISCOUNT+" DOUBLE," 
                    + "UNIQUE(" + SYNC_PROVIDER_NAME.PAYMENT_ID + "," + SYNC_PROVIDER_NAME.CUSTOMER_ID + "));");
		 //currency
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_CURRENCY  + " (" 
				    +SYNC_PROVIDER_NAME.CURRENCY_ID+" INTEGER PRIMARY KEY," 
				    +SYNC_PROVIDER_NAME.TITLE+" TEXT,"
                    +SYNC_PROVIDER_NAME.CODE+" TEXT,"
                    +SYNC_PROVIDER_NAME.SYMBOL_LEFT+" CHAR,"
                    +SYNC_PROVIDER_NAME.SYMBOL_RIGHT+" CHAR,"
                    +SYNC_PROVIDER_NAME.DECIMAL_PLACE+" TEXT,"
                    +SYNC_PROVIDER_NAME.VALUE+" DOUBLE,"
                    //+SYNC_PROVIDER_NAME.DEFAULT+" INTEGER,"
                    +SYNC_PROVIDER_NAME.DATA_MODIFIED+" TEXT);");
		//constants
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS  + " (" 
				    +SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID+" INTEGER PRIMARY KEY," 
				    +SYNC_PROVIDER_NAME.TITLE+" TEXT,"
                    +SYNC_PROVIDER_NAME.UNIT+" TEXT,"
                    +SYNC_PROVIDER_NAME.VALUE+" DOUBLE);");
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS  + " (" 
				    +SYNC_PROVIDER_NAME.LENGHT_CLASS_ID+" INTEGER PRIMARY KEY," 
				    +SYNC_PROVIDER_NAME.TITLE+" TEXT,"
                    +SYNC_PROVIDER_NAME.UNIT+" TEXT,"
                    +SYNC_PROVIDER_NAME.VALUE+" DOUBLE);");
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_MANUFACTURER + " (" 
				    +SYNC_PROVIDER_NAME.MANUFACTURER_ID+" INTEGER PRIMARY KEY," 
                    +SYNC_PROVIDER_NAME.NAME+" TEXT);");
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_BRAND + " (" 
				    +SYNC_PROVIDER_NAME.BRAND_ID+" INTEGER PRIMARY KEY," 
				    +SYNC_PROVIDER_NAME.IMAGE +" VARCHAR,"
                 +SYNC_PROVIDER_NAME.NAME+" TEXT);");
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY + " (" 
				    +SYNC_PROVIDER_NAME.CATEGORY_ID+" INTEGER PRIMARY KEY," 
                    +SYNC_PROVIDER_NAME.NAME+" TEXT);");
		 
		 db.execSQL("CREATE TABLE " + SYNC_PROVIDER_NAME.TABLE_CONTAINER  + " (" 
				    +SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID+" INTEGER PRIMARY KEY," 
				    +SYNC_PROVIDER_NAME.NAME+" TEXT,"
				    +SYNC_PROVIDER_NAME.VOLUME+" DOUBLE,"
                    +SYNC_PROVIDER_NAME.WEIGHT+" DOUBLE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
