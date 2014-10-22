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

public class CurrencySyncContentProvider extends ContentProvider{

	private static final int CURRENCY = 1;
    private static final int CURRENCY_ID = 2;

    private static final UriMatcher sUriMatcher;
    private static SQLiteDatabase mOpenHelper;
	
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CURRENCY, SYNC_PROVIDER_NAME.TABLE_CURRENCY, CURRENCY);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CURRENCY, SYNC_PROVIDER_NAME.TABLE_CURRENCY+"/#", CURRENCY_ID);
    }

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
	    return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case CURRENCY_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CURRENCY,SYNC_PROVIDER_NAME.CURRENCY_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case CURRENCY:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CURRENCY, selection, selectionArgs);	
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
		case CURRENCY:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_CURRENCY, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI, id);
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
	        case CURRENCY:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CURRENCY);
	            break;
	        case CURRENCY_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CURRENCY);
	           // qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.CURRENCY_ID+"=" + uri.getPathSegments().get(1));
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
			case CURRENCY_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CURRENCY, values,
						SYNC_PROVIDER_NAME.CUSTOMER_ID + "=" + segment, selectionArgs);
				break;

			case CURRENCY:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CURRENCY, values,
						selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
	}
}
