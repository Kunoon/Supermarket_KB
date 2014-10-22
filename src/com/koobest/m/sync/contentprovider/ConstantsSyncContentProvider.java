package com.koobest.m.sync.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ConstantsSyncContentProvider extends ContentProvider{

	private static final int LENGTH = 1;
    private static final int LENGTH_ID = 2;
    private static final int WEIGHT = 3;
    private static final int WEIGHT_ID = 4;
    private static final int CATEGORY = 5;
    private static final int CATEGORY_ID = 6;
    private static final int MANUFACTURER = 7;
    private static final int MANUFACTURER_ID = 8;
    private static final int CONTAINER = 9;
    private static final int CONTAINER_ID = 10;
    private static final int BRAND = 11;
    private static final int BRAND_ID = 12;
    private static final UriMatcher sUriMatcher;
    private static SQLiteDatabase mOpenHelper;
	
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS, WEIGHT);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS+"/#", WEIGHT_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS, LENGTH);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS+"/#", LENGTH_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_MANUFACTURER, MANUFACTURER);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_MANUFACTURER+"/#", MANUFACTURER_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_BRAND, BRAND);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_BRAND+"/#", BRAND_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY, CATEGORY);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY+"/#", CATEGORY_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_CONTAINER, CONTAINER);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CONSTANTS, SYNC_PROVIDER_NAME.TABLE_CONTAINER+"/#", CONTAINER_ID);
    }

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
	    return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case WEIGHT_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS,SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case WEIGHT:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS, selection, selectionArgs);	
		case LENGTH_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS,SYNC_PROVIDER_NAME.LENGHT_CLASS_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case LENGTH:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS, selection, selectionArgs);	
		case MANUFACTURER_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER,SYNC_PROVIDER_NAME.MANUFACTURER_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case MANUFACTURER:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER, selection, selectionArgs);	
		case BRAND_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_BRAND,SYNC_PROVIDER_NAME.BRAND_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case BRAND:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_BRAND, selection, selectionArgs);	
		case CATEGORY_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY,SYNC_PROVIDER_NAME.CATEGORY_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case CATEGORY:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY, selection, selectionArgs);
		case CONTAINER_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CONTAINER,SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case CONTAINER:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CONTAINER, selection, selectionArgs);	
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id;
		switch(sUriMatcher.match(uri)){
		case WEIGHT:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.WEIGHT_UNITS_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		case LENGTH:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.LENGTH_UNITS_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		case MANUFACTURER:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.MANUFACTURER_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		case BRAND:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_BRAND, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		case CATEGORY:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		case CONTAINER:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_CONTAINER, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	    if ( id > 0 ){
			//Uri newUri=ContentUris.withAppendedId(PROVIDER_NAME.ACCOUNTSYNC_CONTENT_URI, id);
		    Uri newUri=null;
		    getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
	    return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        

	        switch (sUriMatcher.match(uri)) {
	        case WEIGHT:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS);
	            break;
	        case WEIGHT_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID+"=" + uri.getPathSegments().get(1));
	            break;         
	        case LENGTH:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS);
	            break;
	        case LENGTH_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.LENGHT_CLASS_ID+"=" + uri.getPathSegments().get(1));
	            break;   
	        case MANUFACTURER:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER);
	            break;
	        case MANUFACTURER_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.MANUFACTURER_ID+"=" + uri.getPathSegments().get(1));
	            break;
	        case BRAND:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_BRAND);
	            break;
	        case BRAND_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_BRAND);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.BRAND_ID+"=" + uri.getPathSegments().get(1));
	            break;
	        case CATEGORY:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY);
	            break;
	        case CATEGORY_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.CATEGORY_ID+"=" + uri.getPathSegments().get(1));
	            break;   
	        case CONTAINER:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CONTAINER);
	            break;
	        case CONTAINER_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CONTAINER);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID+"=" + uri.getPathSegments().get(1));
	            break; 
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	        }

	        // If no sort order is specified use the default
	        String orderBy;
	        if (TextUtils.isEmpty(sortOrder)) {
	            orderBy = null;
	        } else {
	            orderBy = sortOrder;
	        }

	        // Get the database and run the query
	        SQLiteDatabase db = mOpenHelper;
	        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	        // Tell the cursor what uri to watch, so it knows when its source data changes
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;	
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		String segment;
		switch (sUriMatcher.match(uri)) {
			case WEIGHT_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS, values,
						SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID + "=" + segment, selectionArgs);
				break;

			case WEIGHT:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_WEIGHT_UNITS, values,
						selection, selectionArgs);
				break;
			case LENGTH_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS, values,
						SYNC_PROVIDER_NAME.LENGHT_CLASS_ID+ "=" + segment, selectionArgs);
				break;

			case LENGTH:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_LENGTH_UNITS, values,
						selection, selectionArgs);
				break;
			case MANUFACTURER_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER, values,
						SYNC_PROVIDER_NAME.MANUFACTURER_ID + "=" + segment, selectionArgs);
				break;

			case MANUFACTURER:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_MANUFACTURER, values,
						selection, selectionArgs);
				break;
			case BRAND_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_BRAND, values,
						SYNC_PROVIDER_NAME.BRAND_ID + "=" + segment, selectionArgs);
				break;

			case BRAND:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_BRAND, values,
						selection, selectionArgs);
				break;
			case CATEGORY_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY, values,
						SYNC_PROVIDER_NAME.CATEGORY_ID+ "=" + segment, selectionArgs);
				break;

			case CATEGORY:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_PRODUCT_CATEGORY, values,
						selection, selectionArgs);
				break;
			case CONTAINER_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CONTAINER, values,
						SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID + "=" + segment, selectionArgs);
				break;

			case CONTAINER:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CONTAINER, values,
						selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;	
	}
}
