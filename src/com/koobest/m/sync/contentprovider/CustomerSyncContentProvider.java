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

public class CustomerSyncContentProvider extends ContentProvider{
    

	private static final int CUSTOMER = 19;
    private static final int CUSTOMER_ID = 20;
    private static final int ADDRESS = 21;
    private static final int ADDRESS_ID = 22;
    private static final int PAYMENT = 23;
    private static final int PAYMENT_ID = 24;

    private static final UriMatcher sUriMatcher;
    private static SQLiteDatabase mOpenHelper;
	
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_CUSTOMER, CUSTOMER);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_CUSTOMER+"/#", CUSTOMER_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_ADDRESS,ADDRESS);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_ADDRESS+"/#", ADDRESS_ID);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_PAYMENT,PAYMENT);
        sUriMatcher.addURI(SYNC_PROVIDER_NAME.AUTHORITY_CUSTOMER, SYNC_PROVIDER_NAME.TABLE_PAYMENT+"/#",PAYMENT_ID);
    }

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
	    return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case CUSTOMER_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CUSTOMER,SYNC_PROVIDER_NAME.CUSTOMER_ID + "=" + uri.getPathSegments().get(1) 
					+ " and ("+ selection + ")", selectionArgs);
		case CUSTOMER:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_CUSTOMER, selection, selectionArgs);
		case ADDRESS_ID:
		    return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_ADDRESS, SYNC_PROVIDER_NAME._ID+ "=" +uri.getPathSegments().get(1)
		    		+"and("+selection+")",selectionArgs);
		case ADDRESS:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_ADDRESS,selection,selectionArgs);
			
		
		case PAYMENT_ID:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_PAYMENT,SYNC_PROVIDER_NAME._ID+"="+uri.getPathSegments().get(1)
					+"and("+selection+")",selectionArgs);
		case PAYMENT:
			return mOpenHelper.delete(SYNC_PROVIDER_NAME.TABLE_PAYMENT,selection,selectionArgs);	
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
		case CUSTOMER:
			id = mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_CUSTOMER, null, values);
		    
		    if ( id > 0 ){
			Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		   }break;
		 case ADDRESS:
		    	id=mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_ADDRESS, null, values);
		    if ( id > 0 ){
				Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}break;
		
		case PAYMENT:
		    	id=mOpenHelper.insert(SYNC_PROVIDER_NAME.TABLE_PAYMENT, null, values);
		    if ( id > 0 ){
				Uri newUri=ContentUris.withAppendedId(SYNC_PROVIDER_NAME.PAYMENT_CONTENT_URI, id);
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
	        case CUSTOMER:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CUSTOMER);
	            break;
	        case CUSTOMER_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_CUSTOMER);
//	            qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME.CUSTOMER_ID+"=" + uri.getPathSegments().get(1));
	            break;
	        case ADDRESS:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_ADDRESS);
	            break;
	        case ADDRESS_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_ADDRESS);
//	            qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME._ID+"=" + uri.getPathSegments().get(1));
	            break;
	            
	        
	            
	        case PAYMENT:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_PAYMENT);
	            break;
	        case PAYMENT_ID:
	        	qb.setTables(SYNC_PROVIDER_NAME.TABLE_PAYMENT);
//	            qb.setProjectionMap(SYNC_PROVIDER_NAME.CustomerProjectionMap);
	            qb.appendWhere(SYNC_PROVIDER_NAME._ID+"=" + uri.getPathSegments().get(1));
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
			case CUSTOMER_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CUSTOMER, values,
						SYNC_PROVIDER_NAME.CUSTOMER_ID + "=" + segment, selectionArgs);
				break;

			case CUSTOMER:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_CUSTOMER, values,
						selection, selectionArgs);
				break;
			case ADDRESS_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_ADDRESS, values,
						SYNC_PROVIDER_NAME._ID + "=" + segment, selectionArgs);
				break;

			case ADDRESS:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_ADDRESS, values,
						selection, selectionArgs);
				break;
			
			
			case PAYMENT_ID:
				segment = uri.getPathSegments().get(1);
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_PAYMENT, values,
						SYNC_PROVIDER_NAME._ID + "=" + segment, selectionArgs);
				break;

			case PAYMENT:
				count = mOpenHelper.update(SYNC_PROVIDER_NAME.TABLE_PAYMENT, values,
						selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
	}
}

