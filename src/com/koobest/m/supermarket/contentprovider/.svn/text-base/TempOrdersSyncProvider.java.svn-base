package com.koobest.m.supermarket.contentprovider;

import com.koobest.m.supermarket.database.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TempOrdersSyncProvider extends ContentProvider{
	private static final UriMatcher sUriMatcher;
	private static SQLiteDatabase mOpenHelper;
	private static final int ORDER = 1;
	private static final int ORDER_ID = 2;
	private static final int FACETS = 3;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY_TEMP_ORDERS, PROVIDER_NAME.TABLE_TEMP_ORDERS,
				ORDER);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY_TEMP_ORDERS, PROVIDER_NAME.TABLE_TEMP_ORDERS
				+ "/#", ORDER_ID);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY_TEMP_ORDERS, PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS,
				FACETS);
	}
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case ORDER_ID:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_TEMP_ORDERS,
					PROVIDER_NAME.ID + "=" + uri.getPathSegments().get(1)
							+ " and (" + selection + ")", selectionArgs);
		case ORDER:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_TEMP_ORDERS, selection,
					selectionArgs);
		case FACETS:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS, selection,
					selectionArgs);
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
		switch (sUriMatcher.match(uri)) {
		case ORDER:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_TEMP_ORDERS, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.SYNC_TEMP_ORDERS_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case FACETS:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.SYNC_TEMPORDER_FACETS_CONTENT_URI, id);
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
		case ORDER:
			qb.setTables(PROVIDER_NAME.TABLE_TEMP_ORDERS);
			break;
		case ORDER_ID:
			qb.setTables(PROVIDER_NAME.TABLE_TEMP_ORDERS);
			qb.appendWhere(PROVIDER_NAME.ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case FACETS:
			qb.setTables(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS);
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
		String segment;
		switch (sUriMatcher.match(uri)) {
		case ORDER_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_TEMP_ORDERS, values,
					PROVIDER_NAME.ID + "=" + segment, selectionArgs);
			break;
		case ORDER:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_TEMP_ORDERS, values,
					selection, selectionArgs);
			break;
		case FACETS:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS, values,
					selection, selectionArgs);
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
