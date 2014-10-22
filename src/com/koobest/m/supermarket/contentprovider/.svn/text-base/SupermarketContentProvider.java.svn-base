package com.koobest.m.supermarket.contentprovider;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

public class SupermarketContentProvider extends ContentProvider {
	
	private static final int BARCODE_PRODUCT = 5;
	private static final int BARCODE_PRODUCT_ID= 6;
	private static final int PRODUCT_DESC = 17;
	private static final int PRODUCT_DESC_ID = 18;
	
	private static final int PRODUCT = 1;
	private static final int PRODUCT_ID = 2;
	private static final int PRODUCT_IMAGE = 3;
	private static final int PRODUCT_IMAGE_ID = 4;
	private static final int PRODUCT_PRICE= 19;//only used in applyBatch()to insert
	
	private static final int WISHLIST = 7;
	private static final int WISHLIST_ID = 8;
	private static final int WISHLIST_PRODUCT = 9;
	
	private static final int QUOTE = 13;
	private static final int QUOTE_ID = 14;
	private static final int QUOTE_PRODUCT = 15;
	private static final int QUOTE_PRODUCT_ID = 16;

	private static final int ORDER = 11;
	private static final int ORDER_ID = 12;

	

	
	
	private static final UriMatcher sUriMatcher;
	private static SQLiteDatabase mOpenHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY, PROVIDER_NAME.TABLE_ORDERS,
				ORDER);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY, PROVIDER_NAME.TABLE_ORDERS
				+ "/#", ORDER_ID);
		
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_PRODUCT, PRODUCT);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY, PROVIDER_NAME.TABLE_PRODUCT
				+ "/#", PRODUCT_ID);
		
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_PRODUCT_IMAGE, PRODUCT_IMAGE);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_PRODUCT_IMAGE + "/#", PRODUCT_IMAGE_ID);
		
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_BARCODE_PRODUCT, BARCODE_PRODUCT);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_BARCODE_PRODUCT + "/#", BARCODE_PRODUCT_ID);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_WISHLIST, WISHLIST);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_WISHLIST + "/#", WISHLIST_ID);
		
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_WISHLIST_PRODUCT, WISHLIST_PRODUCT);
		/*sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_WISHLIST_PRODUCT + "/#",WISHLIST_PRODUCT_ID);
        */
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_QUOTES,QUOTE);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY, 
				PROVIDER_NAME.TABLE_QUOTES	+ "/#", QUOTE_ID);
		
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_QUOTE_PRODUCT, QUOTE_PRODUCT);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_QUOTE_PRODUCT + "/#", QUOTE_PRODUCT_ID);

		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_PRODUCT_DESC, PRODUCT_DESC);
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY,
				PROVIDER_NAME.TABLE_PRODUCT_DESC + "/#", PRODUCT_DESC_ID);
        
		sUriMatcher.addURI(PROVIDER_NAME.AUTHORITY, PROVIDER_NAME.TABLE_PRODUCT_PRICE,
				PRODUCT_PRICE);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext()).getWritableDatabase();
		return true;
	}

	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		return ParcelFileDescriptor.open(new File(
				"/data/data/com.koobest.m.supermarket.activities/files", uri
						.getPathSegments().get(1)),
				ParcelFileDescriptor.MODE_READ_ONLY);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
		case ORDER_ID:
			String selection1=PROVIDER_NAME.ORDER_ID + "=" 
					+ uri.getPathSegments().get(1)
			        + " and (" + selection + ")";
			deleteFile(PROVIDER_NAME.TABLE_ORDERS, 
					new String[]{PROVIDER_NAME.ORDER_XML},
					selection1, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_ORDERS,
					selection1, selectionArgs);
		case ORDER:
			deleteFile(PROVIDER_NAME.TABLE_ORDERS, 
					new String[]{PROVIDER_NAME.ORDER_XML},
					selection, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_ORDERS, selection,
					selectionArgs);
		case PRODUCT_ID:
			String selection2=PROVIDER_NAME.PRODUCT_ID + "="
					+ uri.getPathSegments().get(1) + " and ("+ selection + ")";
			deleteProductFile(PROVIDER_NAME.TABLE_PRODUCT, 
					new String[]{PROVIDER_NAME.PRODUCT_XML},
					selection2, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT,
					selection2, selectionArgs);
		case PRODUCT:
			deleteProductFile(PROVIDER_NAME.TABLE_PRODUCT, 
					new String[]{PROVIDER_NAME.PRODUCT_XML},
					selection, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT, selection,
					selectionArgs);
			//the deletion of product_image will be caused by delete product  
		/*case PRODUCT_IMAGE_ID:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_IMAGE,
					PROVIDER_NAME.PRODUCT_IMAGE_ID + "="
							+ uri.getPathSegments().get(1) + " and ("
							+ selection + ")", selectionArgs);
		case PRODUCT_IMAGE:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_IMAGE,
					selection, selectionArgs);*/
			
		case BARCODE_PRODUCT_ID:
			String selection3=PROVIDER_NAME.BARCODE +"="
			        + uri.getPathSegments().get(1) + " and ("+ selection + ")";
			onDeleteBarcodeListener(selection3, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_BARCODE_PRODUCT,
					selection3, selectionArgs);
		case BARCODE_PRODUCT:
			onDeleteBarcodeListener(selection, selectionArgs);
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_BARCODE_PRODUCT,
					selection, selectionArgs);
		case WISHLIST_PRODUCT:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT,
					selection, selectionArgs);
		case WISHLIST_ID:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_WISHLIST,
					PROVIDER_NAME.WISHLIST_ID + "="
							+ uri.getPathSegments().get(1) + " and ("
							+ selection + ")", selectionArgs);
		case WISHLIST:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_WISHLIST, selection,
					selectionArgs);

		case QUOTE_ID:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_QUOTES,
					PROVIDER_NAME.QUOTE_ID + "=" + uri.getPathSegments().get(1)
							+ " and (" + selection + ")", selectionArgs);
		case QUOTE:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_QUOTES, selection,
					selectionArgs);
		case QUOTE_PRODUCT_ID:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_QUOTE_PRODUCT,
					PROVIDER_NAME.QUOTE_PRODUCT_ID + "="
							+ uri.getPathSegments().get(1) + " and ("
							+ selection + ")", selectionArgs);
		case QUOTE_PRODUCT:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_QUOTE_PRODUCT,
					selection, selectionArgs);
			//the deletion of product_desc will be caused by delete barcode  
		/*case PRODUCT_DESC:
			return mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_DESC,
					selection, selectionArgs);*/

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	private void onDeleteBarcodeListener(String selection, String[] selectionArgs){
		Cursor cursor = mOpenHelper.query(PROVIDER_NAME.TABLE_BARCODE_PRODUCT,
				new String[]{PROVIDER_NAME.PRODUCT_IDS,PROVIDER_NAME.CREATE_DATE},
				selection, selectionArgs, null,null,null);
		while(cursor.moveToNext()){
			String[] product_ids = cursor.getString(0).split(",");
			String whereClause=PROVIDER_NAME.PRODUCT_ID+"=? and "
        		+PROVIDER_NAME.LAST_UPDATE_DATE+"<="+cursor.getLong(1);
			for(String product_id:product_ids){
				deleteFile(PROVIDER_NAME.TABLE_PRODUCT_DESC,new String[]{PROVIDER_NAME.IMAGE}, whereClause, new String[]{product_id});
				mOpenHelper.delete(PROVIDER_NAME.TABLE_PRODUCT_DESC,
						whereClause, new String[]{product_id});
			}
		}
		cursor.close();	
	}
	
	/**
	 * used to delete files about product
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 */
	private void deleteProductFile(String table,String[] columns,String selection,String[] selectionArgs){
		//delete images
		Cursor cursor = mOpenHelper.query(PROVIDER_NAME.TABLE_PRODUCT,
				new String[]{PROVIDER_NAME.PRODUCT_ID},
				selection, selectionArgs, null, null, null);
		String img_table=PROVIDER_NAME.TABLE_PRODUCT_IMAGE,
		       img_columns[]=new String[]{PROVIDER_NAME.IMAGE},
		       img_selection=PROVIDER_NAME.PRODUCT_ID+"=";
		while(cursor.moveToNext()){
			deleteFile(img_table, img_columns, img_selection+cursor.getInt(0), null);
		}
		cursor.close();
		//delete XML file
		deleteFile(table,columns,selection, selectionArgs);
		
	}
	/**
	 * 
	 * @param table 
	 * @param columns the first column must the File direct 
	 * @param selection
	 * @param selectionArgs
	 */
	private void deleteFile(String table,String[] columns,String selection,String[] selectionArgs){
		Cursor cursor = mOpenHelper.query(table,
				columns,selection, selectionArgs, null, null,null);
//		System.out.println(table+","+columns[0]+"'"+selection+","+(cursor.getCount()));
		File file;
		while(cursor.moveToNext()&&cursor.getString(0)!=null){
			System.out.println((cursor==null)+""+(cursor.getString(0)==null));
			file= new File("/data/data/com.koobest.m.supermarket.activities/files",
					cursor.getString(0));
			if(file.exists()){
				file.delete();
			}
		}
		cursor.close();
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
		//search product
		case BARCODE_PRODUCT:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_BARCODE_PRODUCT, null,
					values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case PRODUCT_DESC:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT_DESC, null,
					values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.PRODUCT_DESC_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		
		//product detail
		case PRODUCT:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.PRODUCT_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case PRODUCT_PRICE:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case PRODUCT_IMAGE:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_PRODUCT_IMAGE, null,
					values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.PRODUCT_IMAGE_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		
		//wishlist
		case WISHLIST:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_WISHLIST, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.WISHLIST_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case WISHLIST_PRODUCT:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT, null,
					values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.WISHILST_PRODUCT_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
        //quote
		case QUOTE:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_QUOTES, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.QUOTE_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
		case QUOTE_PRODUCT:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_QUOTE_PRODUCT, null,
					values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.QUOTE_PRODUCT_CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
			
		//order
		case ORDER:
			id = mOpenHelper.insert(PROVIDER_NAME.TABLE_ORDERS, null, values);
			if (id > 0) {
				Uri newUri = ContentUris.withAppendedId(
						PROVIDER_NAME.ORDER_CONTENT_URI, id);
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
			qb.setTables(PROVIDER_NAME.TABLE_ORDERS);
			break;
		case ORDER_ID:
			qb.setTables(PROVIDER_NAME.TABLE_ORDERS);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere(PROVIDER_NAME.ORDER_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case PRODUCT:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT);
			break;
		case PRODUCT_ID:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere(PROVIDER_NAME.PRODUCT_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case PRODUCT_IMAGE:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_IMAGE);
			break;
		case PRODUCT_IMAGE_ID:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_IMAGE);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere(PROVIDER_NAME.PRODUCT_IMAGE_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case BARCODE_PRODUCT:
			qb.setTables(PROVIDER_NAME.TABLE_BARCODE_PRODUCT);
			break;
		case BARCODE_PRODUCT_ID:
			qb.setTables(PROVIDER_NAME.TABLE_BARCODE_PRODUCT);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere(PROVIDER_NAME.BARCODE+"="
					+ uri.getPathSegments().get(1));
		case WISHLIST:
			qb.setTables(PROVIDER_NAME.TABLE_WISHLIST);
			break;
		case WISHLIST_ID:
			qb.setTables(PROVIDER_NAME.TABLE_WISHLIST);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere(PROVIDER_NAME.WISHLIST_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case WISHLIST_PRODUCT:
			qb.setTables(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT);
			break;
		/*case WISHLIST_PRODUCT_ID:
			qb.setTables(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT);
			qb.setProjectionMap(PROVIDER_NAME.sNotesProjectionMap);
			qb.appendWhere("WISH_PRODUCT_ID = "
					+ uri.getPathSegments().get(1));*/

		case QUOTE:
			qb.setTables(PROVIDER_NAME.TABLE_QUOTES);
			break;
		case QUOTE_ID:
			qb.setTables(PROVIDER_NAME.TABLE_QUOTES);
			qb.appendWhere(PROVIDER_NAME.QUOTE_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case QUOTE_PRODUCT:
			qb.setTables(PROVIDER_NAME.TABLE_QUOTE_PRODUCT);
			break;
		case QUOTE_PRODUCT_ID:
			qb.setTables(PROVIDER_NAME.TABLE_QUOTE_PRODUCT);
			qb.appendWhere(PROVIDER_NAME.QUOTE_PRODUCT_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		case PRODUCT_DESC:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_DESC);
			break;
		/*case PRODUCT_DESC_ID:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_DESC);
			qb.appendWhere("PRODUCT_DESC_ID = "
					+ uri.getPathSegments().get(1));*/
		case PRODUCT_DESC_ID:
			qb.setTables(PROVIDER_NAME.TABLE_PRODUCT_DESC);
			qb.appendWhere(PROVIDER_NAME.PRODUCT_ID + "="
					+ uri.getPathSegments().get(1));
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
		//SQLiteDatabase db = mOpenHelper;
		Cursor c = qb.query(mOpenHelper, projection, selection, selectionArgs, null,
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
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_ORDERS, values,
					PROVIDER_NAME.ORDER_ID + "=" + segment, selectionArgs);
			break;

		case ORDER:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_ORDERS, values,
					selection, selectionArgs);
			break;

		case PRODUCT_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT, values,
					PROVIDER_NAME.PRODUCT_ID + "=" + segment,
					selectionArgs);
			break;

		case PRODUCT:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT, values,
					selection, selectionArgs);
			break;

		case PRODUCT_IMAGE_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT_IMAGE,
					values, PROVIDER_NAME.PRODUCT_IMAGE_ID + "=" + segment,
					selectionArgs);
			break;

		case PRODUCT_IMAGE:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT_IMAGE,
					values, selection, selectionArgs);
			break;

		case BARCODE_PRODUCT_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_BARCODE_PRODUCT,
					values, PROVIDER_NAME.BARCODE+"=" + segment,
					selectionArgs);
			break;
		case BARCODE_PRODUCT:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_BARCODE_PRODUCT,
					values, selection, selectionArgs);
			break;

		/*case WISHLIST_PRODUCT_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT,
					values, "WISH_PRODUCT_ID = " + segment,
					selectionArgs);
			break;
          */
		case WISHLIST_PRODUCT:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_WISHLIST_PRODUCT,
					values, selection, selectionArgs);
			break;

		case WISHLIST_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_WISHLIST, values,
					PROVIDER_NAME.WISHLIST_ID + "=" + segment, selectionArgs);
			break;

		case WISHLIST:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_WISHLIST, values,
					selection, selectionArgs);
			break;

		case QUOTE_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_QUOTES, values,
					PROVIDER_NAME.QUOTE_ID + "=" + segment, selectionArgs);
			break;

		case QUOTE:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_QUOTES, values,
					selection, selectionArgs);
			break;
		case QUOTE_PRODUCT_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_QUOTE_PRODUCT, values,
					PROVIDER_NAME.QUOTE_PRODUCT_ID + "=" + segment,
					selectionArgs);
			break;

		case QUOTE_PRODUCT:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_QUOTE_PRODUCT, values,
					selection, selectionArgs);
			break;
		case PRODUCT_DESC_ID:
			segment = uri.getPathSegments().get(1);
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT_DESC,values, 
					PROVIDER_NAME.PRODUCT_ID + " = " + segment,
					selectionArgs);
			break;

		case PRODUCT_DESC:
			count = mOpenHelper.update(PROVIDER_NAME.TABLE_PRODUCT_DESC,
					values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
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
}
