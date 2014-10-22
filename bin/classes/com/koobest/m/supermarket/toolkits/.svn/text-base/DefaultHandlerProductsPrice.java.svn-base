package com.koobest.m.supermarket.toolkits;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.ContentProviderOperation.Builder;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.util.Log;

import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.DatabaseUtilities;

public class DefaultHandlerProductsPrice extends DefaultHandler{
	private final static String TAG = "ProductListHandler";
	private Stack<String> tags = new Stack<String>();
	private ArrayList<ContentProviderOperation> operations;
	private ContentResolver resolver;
	private Context mContext;
	private List<String> mInvalidPrioductIds;
	private Builder builder;
	private int product_id;
	//private ContentValues value;//,values;
//	public DefaultHandlerProductsPrice(Context context) {
//		resolver = context.getContentResolver();
//		mContext = context;
//		//assume all the product in $productIds are invalid
//	}
	
	public DefaultHandlerProductsPrice(Context context,int[] productIds) {
		resolver = context.getContentResolver();
		mContext = context;
		mInvalidPrioductIds=new ArrayList<String>();
		//assume all the product in $productIds are invalid
		for(int i=0;i<productIds.length;i++){
			mInvalidPrioductIds.add(String.valueOf(productIds[i]));
		}
	}

	public void endDocument() throws SAXException {
		try {
			resolver.applyBatch(PROVIDER_NAME.AUTHORITY_PRICE, operations);
			if(!mInvalidPrioductIds.isEmpty()){
				SQLiteDatabase db = new DatabaseHelper(mContext).getWritableDatabase();
				try{
					for(String productId:mInvalidPrioductIds){
						DatabaseUtilities.deleteProduct(db, productId);
					}
				}finally{
					db.close();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}

	public void startDocument() throws SAXException {
		Log.e(TAG,"start parse product list");
		operations=new ArrayList<ContentProviderOperation>();
		builder=ContentProviderOperation.newDelete(
				PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI);
		operations.add(builder.build());
		
	//	resolver.delete(PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI, null, null);
		//value = new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		if(p1.equals("product_price")){
			builder = ContentProviderOperation.newInsert(
					PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI);
			builder.withValue(PROVIDER_NAME.QTY, 1);
			//value.put(PROVIDER_NAME.QTY,1);
		}else if(p1.equals("discount")){
			builder = ContentProviderOperation.newInsert(
					PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI);
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("price")) {
			builder.withValue(PROVIDER_NAME.PRODUCT_ID,product_id);
			operations.add(builder.build());
			//resolver.insert(PROVIDER_NAME.SYNC_PRO_PRICE_CONTENT_URI, value);
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("product_id")) {
			product_id=Integer.valueOf(new String(p0, p1, p2));
			mInvalidPrioductIds.remove(new String(p0, p1, p2));
			//value.put(PROVIDER_NAME.PRODUCT_ID, Integer.valueOf(new String(p0, p1, p2)));	
		} else if (tag.equals("quantity")){
			builder.withValue(PROVIDER_NAME.QTY, 
					Double.valueOf(new String(p0, p1, p2)));
			//value.put(PROVIDER_NAME.QTY,
				//	Double.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("price")){
			builder.withValue(PROVIDER_NAME.PRICE, 
					Double.valueOf(new String(p0, p1, p2)));
			//value.put(PROVIDER_NAME.PRICE,
				//	Double.valueOf(new String(p0, p1, p2)));
		} 
	}
}
