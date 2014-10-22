package com.koobest.m.supermarket.contentprovider;

import java.util.ArrayList;

import com.koobest.m.supermarket.database.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PriceSyncContentProvider extends ContentProvider{
	private static final UriMatcher sUriMatcher;
	private static SQLiteDatabase mOpenHelper;
	private static final int PRODUCT = 11;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY_PRICE, PROVIDER_NAME.TABLE_PRODUCT_PRICE,
				PRODUCT);
	}
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case PRODUCT:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_PRICE, selection,
					selectionArgs);
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id;
		switch (sUriMatcher.match(uri)) {
		case PRODUCT:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case PRODUCT:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_PRICE);
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
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		switch (sUriMatcher.match(uri)) {
		case PRODUCT:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT_PRICE, values,
					selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		// TODO Auto-generated method stub
		mOpenHelper.beginTransaction();
		try{
			int length = operations.size();
			ContentProviderResult[] result = new ContentProviderResult[length];
			for(int i=0;i<length;i++){
				result[i]=operations.get(i).apply(this, null, 0);
			}
			mOpenHelper.setTransactionSuccessful();
			return result;
		}finally{
			mOpenHelper.endTransaction(); 
		}
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int count=0;
		long id;
		switch (sUriMatcher.match(uri)) {
		case PRODUCT:
			mOpenHelper.beginTransaction();
			try{
				//mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, null);
				for(ContentValues value:values){
					id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, value);
					if (id > 0) {
						Uri newUri = ContentUris.withAppendedId(
								PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI, id);
						getContext().getContentResolver().notifyChange(newUri, null);
						count++;
					}else{
						count=0; 
						throw new Exception("insert failure");
					}
				}
				mOpenHelper.setTransactionSuccessful();
			}catch (Exception e) {
				e.printStackTrace(); 
			}finally{
				mOpenHelper.endTransaction(); 
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}
}
