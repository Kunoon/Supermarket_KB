package com.koobest.m.supermarket.toolkits;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.transform.TransformerException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.ContentProviderOperation.Builder;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.toolkits.transform.currency.CurrencyTransform;
import com.koobest.m.toolkits.transform.exceptions.TransformException;
import com.koobest.m.toolkits.transform.units.UnitTransform;

public class DefaultHandlerFactory {
	 public final static int PARSE_PRODUCT_XML=0;
	 public final static int PARSE_ORDERLIST_XML=1;
//	 public final static int PARSE_ORDERDETAIL_XML=2;
	 public final static int PARSE_SIMPLE_QUOTE_XML=3;
	 public final static int PARSE_PRODUCTLIST_XML=4;
	 public final static int PARSE_ORDER_TEMPLATE_LIST_XML=6;
	 public final static int PARSE_ORDER_TEMPLATE_DETAIL_XML=7;
	 public final static int PARSE_ORDER_TO_QUOTE=8;
	 /**
	  * this method could create a SubDefaultHandler while the type is among the following static key of this class
	  * PARSE_PRODUCT_XML;
	  * PARSE_ORDERLIST_XML;
	  * PARSE_ORDERDETAIL_XML;
	  * PARSE_SIMPLE_QUOTE_XML;
	  * @param type
	  * @param context
	  * @param source the duplicate of xml file while the type is PARSE_PRODUCT_XML it should not be null;
	  * @return
	  */
     public static DefaultHandler createHandler(int type,Context context,byte[] source){
    	 switch(type){
     	 case PARSE_PRODUCT_XML:
     		 if(context==null||source==null)
     		     return null;
     		 else
     			 return new ProductHandler(context,source);
     	 case PARSE_ORDERLIST_XML:
     		 if(context!=null)
     		     return new OrderListHandler(context);
//     	 case PARSE_ORDERDETAIL_XML:
//     		 if(context!=null)
//     		     return new OrderDetailHandler(context);
     	 case PARSE_ORDER_TEMPLATE_LIST_XML:
    		 if(context!=null)
    		     return new OrderTemplateListHandler(context);
     	 case PARSE_ORDER_TEMPLATE_DETAIL_XML:
     		 if(context==null||source==null)
   		         return null;
   		     else
    		     return new OrderTemplateDetailHandler(context,source);
     	case PARSE_ORDER_TO_QUOTE:
    		 if(context!=null)
    		     return new ProductParseToNewQuoteHandler(context);
     	 default:
     		 return null;
     	 }		
     }
     
     public static DefaultHandler createHandler(int type,Context context){
    	 return createHandler(type, context,null);	
     }
     
     public static DefaultHandler createProductlistHandler(Context context,long barcode){
    	 if(context==null || barcode<0){
    		 return null;
    	 }
    	 return new ProductListSaveToDBHandler(context,barcode);
     }
     
     public static DefaultHandler createProductlistHandler(Context context,Bundle responseResult){
    	 if(context==null || responseResult==null){
    		 return null;
    	 }
    	 return new ProductListHandler(context,responseResult);
     }
     
     public static DefaultHandler createSyncOrderlistHandler(Context context,int customer_id){
    	 if(context==null || customer_id<0){
    		 return null;
    	 }
    	 return new OrderListHandler(context,customer_id);
     }
     
     public static DefaultHandler createSimpleQuoteHandler(Context context,byte[] source,Bundle response){
		     return new SimpleQuoteHandler(context,source,response);
     }
}



class ProductHandler extends DefaultHandler {
    private final static String TAG = "ProductHandler";
    private SQLiteDatabase mDatabase;
	private Stack<String> tags = new Stack<String>();
	private ArrayList<ContentProviderOperation> operations;
	private byte[] product_xml;
	private int mProduct_id,img_count,weight_class_id,length_class_id;
	private String mTag="",mProductName="";
	private ContentValues product_values;
	private ContentValues image_values;
	private ContentValues price_values;
	private Context mContext;
    private Double weight,volume=1.0;
	public ProductHandler(Context context,byte[] buffer) {
		product_xml = buffer;
		mContext = context;
	}

	public void endDocument() throws SAXException {
		if(mDatabase.isOpen()){
			if(mDatabase.inTransaction()){
				mDatabase.endTransaction();
			}
			mDatabase.close();
		}
	}

	public void startDocument() throws SAXException {
		mDatabase=new DatabaseHelper(mContext).getWritableDatabase();
		mDatabase.beginTransaction();
		operations = new ArrayList<ContentProviderOperation>();
		product_values = new ContentValues();
		image_values = new ContentValues();
		price_values = new ContentValues();
		product_values.put(PROVIDER_NAME.LAST_ACCESS_DATE,
				System.currentTimeMillis());
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		if(p1.equals("weight_class")){
			mTag=p1;
		} else if(p1.equals("length_class")){
			mTag=p1;
		} else if(p1.equals("qty_class")){
			mTag=p1;
		} else if(p1.equals("product")){
			mTag=p1;
		} else if(p1.equals("manufacturer")){
			mTag=p1;
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("price")) {
			mDatabase.insert(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, price_values);
			price_values= new ContentValues();
			price_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
		} else if (p1.equals("image")) {
			mDatabase.insert(PROVIDER_NAME.TABLE_PRODUCT_IMAGE, null, image_values);
			price_values= new ContentValues();
			price_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
		} else if (p1.equals("product")) {
			try {
				product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);mProductName="";mTag="";
				weight=UnitTransform.weightTransform(mContext, weight, weight_class_id);
				product_values.put(PROVIDER_NAME.WEIGHT,weight);
				volume=UnitTransform.volumeTransform(mContext, volume, length_class_id);
				product_values.put(PROVIDER_NAME.VOLUME,volume);
			    
				String xml_address = "pro"
					+ mProduct_id + ".xml";
				FileOutputStream outputStream = mContext.openFileOutput(xml_address,
						Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(product_xml);
				outputStream.close();
				product_values.put(PROVIDER_NAME.PRODUCT_XML,
						xml_address);
				mDatabase.insert(PROVIDER_NAME.TABLE_PRODUCT, null, product_values);
				mDatabase.setTransactionSuccessful();
			} catch (TransformException e1) {
				e1.printStackTrace();
				throw new SAXException(e1.getMessage());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				mDatabase.endTransaction();
				mDatabase.close();
			}
		} 
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();

		if (tag.equals("product_id")) {
			mProduct_id = Integer.valueOf(new String(p0, p1, p2));
			mDatabase.delete(PROVIDER_NAME.TABLE_PRODUCT, PROVIDER_NAME.PRODUCT_ID+"="+mProduct_id,null);
			mDatabase.delete(PROVIDER_NAME.TABLE_PRODUCT_IMAGE, PROVIDER_NAME.PRODUCT_ID+"="+mProduct_id,null);
			mDatabase.delete(PROVIDER_NAME.TABLE_PRODUCT_PRICE, PROVIDER_NAME.PRODUCT_ID+"="+mProduct_id,null);
			product_values.put(PROVIDER_NAME.PRODUCT_ID, mProduct_id);
			image_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
			price_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
		} else if (tag.equals("minimum")){
			product_values.put(PROVIDER_NAME.MINQTY,
					Integer.valueOf(new String(p0, p1, p2)));
			price_values.put(PROVIDER_NAME.QTY, Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("name")){
			if(mTag.equalsIgnoreCase("product")){
				mProductName=mProductName+new String(p0, p1, p2);		
//				mTag="";
			}else if(mTag.equalsIgnoreCase("qty_class")){
				product_values.put(PROVIDER_NAME.QTY_UNIT,new String(p0, p1, p2));
				mTag="";
			}
		} else if (tag.equals("quantity")){
			price_values.put(PROVIDER_NAME.QTY, Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("price")){
			price_values.put(PROVIDER_NAME.PRICE,
					Double.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("weight")){
			weight=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("length")){
			Log.e(TAG,"length:"+new String(p0, p1, p2));
			volume*=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("height")){
			volume*=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("width")){
		    volume*=Double.valueOf(new String(p0, p1, p2));
    	} else if (tag.equals("id")){
			if(mTag.equals("weight_class")){
				weight_class_id=Integer.valueOf(new String(p0,
						p1, p2));
				mTag="";
			} else if(mTag.equals("length_class")){
				length_class_id=Integer.valueOf(new String(p0,
						p1, p2));
				mTag="";
			}
		} 
   	    else if (tag.equals("image")) {
			image_values.put(PROVIDER_NAME.IMAGE, String.valueOf(mProduct_id)
					+ img_count + ".png");
//			try {
//				byte[] data = NetworkUtilities.downloadImage(new String(p0, p1, p2));
//
//				FileOutputStream outputStream = mContext.openFileOutput(
//						String.valueOf(mProduct_id) + img_count + ".png",
//						Activity.MODE_WORLD_WRITEABLE);
//				outputStream.write(data);
//				outputStream.close();
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ResponseException e) {
//				e.printStackTrace();
//			}
			img_count++;
	    }
	}
}

class ProductListSaveToDBHandler extends DefaultHandler {
    private final static String TAG = "ProductListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver cr;
	private Cursor cursor;
	private long currentTime;
	private String product_id,mProductName="",mManufacturer="";
	private StringBuffer product_ids;
	private ContentValues desc_values;
	private ContentValues barcode_values;
	private FileOutputStream outputStream;
	private Context mContext;
	public ProductListSaveToDBHandler(Context context,long barcode) {
		barcode_values = new ContentValues();
		barcode_values.put(PROVIDER_NAME.BARCODE, barcode);
		product_ids = new StringBuffer();
		mContext = context;
	}

	public void endDocument() throws SAXException {
		
	}

	public void startDocument() throws SAXException {
		Log.e(TAG,"start parse product list");
		currentTime = System.currentTimeMillis();
		desc_values = new ContentValues();
		cr = mContext.getContentResolver();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("product")) {
			desc_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);mProductName="";
			desc_values.put(PROVIDER_NAME.MANUFACTURER,mManufacturer);mManufacturer="";
			desc_values.put(PROVIDER_NAME.LAST_UPDATE_DATE, currentTime);
			
			cursor= cr.query(PROVIDER_NAME.PRODUCT_DESC_CONTENT_URI,
					null, PROVIDER_NAME.PRODUCT_ID+"="+product_id, null, null);
			if(!cursor.moveToFirst()){
				cr.insert(PROVIDER_NAME.PRODUCT_DESC_CONTENT_URI,
						desc_values);
				desc_values.clear();
				cursor.close();
				return;
			}
			cursor.close();
			cr.update(PROVIDER_NAME.PRODUCT_DESC_CONTENT_URI, desc_values,
					PROVIDER_NAME.PRODUCT_ID+"="+product_id,null);
			desc_values.clear();
		}else if (p1.equals("product_list")) {
			barcode_values.put(PROVIDER_NAME.CREATE_DATE, currentTime);
			
			barcode_values.put(PROVIDER_NAME.PRODUCT_IDS,(product_ids.deleteCharAt(0)).toString());
			Log.e(TAG,barcode_values.getAsString(PROVIDER_NAME.PRODUCT_IDS));
			cr.insert(PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI,
					barcode_values);
			barcode_values.clear();
		} 
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("product_id")) {
			product_id = new String(p0, p1, p2);
			product_ids.append(","+product_id);
			desc_values.put(PROVIDER_NAME.PRODUCT_ID, Integer.valueOf(product_id));
		} else if (tag.equals("name")){
			mProductName=mProductName+new String(p0, p1, p2);
		} else if (tag.equals("manufacturer")){
			mManufacturer=mManufacturer+new String(p0, p1, p2);
		} else if (tag.equals("price")){
			desc_values.put(PROVIDER_NAME.PRICE,
					new String(p0, p1, p2));
		} else if (tag.equals("image")) {
			desc_values.put(PROVIDER_NAME.IMAGE,
                    "s"+product_id + ".png");
			try {
				byte[] data = NetworkUtilities.downloadImage(new String(p0, p1, p2));
				outputStream = mContext.openFileOutput(
						 "s"+product_id + ".png",
						Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(data);
				outputStream.close();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ResponseException e) {
				e.printStackTrace();
			}
		} else if (tag.equals("total")){
			barcode_values.put(PROVIDER_NAME.PRODUCT_NUM,
					Integer.valueOf(new String(p0, p1, p2)));
		} 
	}
}

class ProductListHandler extends DefaultHandler {
    private final static String TAG = "ProductListHandler";
	private Stack<String> tags = new Stack<String>();
	private Bundle mResponse;
	private int product_id[],index=0;
	private String name[],manufacturer[],image[],mProductName="",mManufacturer="";
	private double price[];
	public ProductListHandler(Context context,Bundle responseResult) {
		mResponse = responseResult;
	}

	public void endDocument() throws SAXException {
		
	}

	public void startDocument() throws SAXException {
		Log.e(TAG,"start parse product list");
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("product")) {
			name[index]=mProductName;mProductName="";
			manufacturer[index]=mManufacturer;mManufacturer="";
			index++;
		} else if (p1.equals("product_list")) {
			mResponse.putIntArray("product_id",product_id );
			mResponse.putStringArray("name",name);
			mResponse.putStringArray("manufacturer",manufacturer);
			mResponse.putDoubleArray("price",price);
			mResponse.putStringArray("image",image);
		} 
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("total")) {
			int num = Integer.valueOf(new String(p0, p1, p2));
			mResponse.putInt("number", num);
            product_id=new int[num];
            name=new String[num];
            price=new double[num];
            image=new String[num];
            manufacturer=new String[num];
		} else if (tag.equals("page")){
			mResponse.putInt("page_index", Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("page_limit")){
			mResponse.putInt("page_limit", Integer.valueOf(new String(p0, p1, p2)));
		} else if(tag.equals("product_id")) {
			product_id[index] = Integer.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("name")){
			mProductName=mProductName+new String(p0, p1, p2);
		} else if (tag.equals("manufacturer")){
			mManufacturer=mManufacturer+new String(p0, p1, p2);
		} else if (tag.equals("price")){
			price[index]=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("image")) {
			image[index]=new String(p0, p1, p2);
		}		
	}
}

class SimpleQuoteHandler extends DefaultHandler {

	Stack<String> tags = new Stack<String>();
	ContentResolver resolver;
	ContentValues order_values;
	Context mContext;
	private byte[] buffer;
	private final Bundle mResponse;
	//ContentResolver resolver;

	public SimpleQuoteHandler(Context context,byte[] buffer,Bundle response) {
		order_values = new ContentValues();
		resolver= context.getContentResolver();
		mContext=context;
		mResponse = response;
		this.buffer=buffer;
	}

	public void endDocument() throws SAXException {

	}

	public void startDocument() throws SAXException {

	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {

		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("order")) {
			Log.e("SimpleQuoteHandler","save order");
			resolver.insert(PROVIDER_NAME.ORDER_CONTENT_URI,
					order_values);
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();

		if (tag.equals("order_id")) {
			String order_id=new String(p0, p1, p2);
			mResponse.putInt(NAME.ORDER_ID, Integer.valueOf(order_id));
			String xml_address = "order" + order_id+".xml";
			
			try {
				OutputStream outputStream = mContext.openFileOutput(
						xml_address, Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(buffer);
				outputStream.close();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			order_values.put(PROVIDER_NAME.ORDER_XML,xml_address);
//			Log.e("ORDER_XML", order_values
//					.getAsString(PROVIDER_NAME.ORDER_XML));
			order_values.put(PROVIDER_NAME.ORDER_ID, Integer
					.valueOf(order_id));
		} else if (tag.equals("description")){
			order_values.put(PROVIDER_NAME.ORDER_COMMENT, new String(
					p0, p1, p2));
		} else if (tag.equals("order_status")){
			order_values.put(PROVIDER_NAME.STATUS, new String(p0,
					p1, p2));
		} else if (tag.equals("date_added")) {
			order_values.put(PROVIDER_NAME.ORDER_DATE, new String(
					p0, p1, p2));
		} else if(tag.equals("customer_id")){
			order_values
			.put(PROVIDER_NAME.CUSTOMER_ID, new String(
					p0, p1, p2));
		}
	}
}

class OrderListHandler extends DefaultHandler {
    private static final String TAG="OrderListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private Uri authority;
	private ContentValues values;
	private int customer_id=-1;
	private Context mContext;
//	private Cursor cursor;
//	private int have_order;
    
	public OrderListHandler(Context context) {
		mContext =context;
		authority=PROVIDER_NAME.SYNC_ORDERLIST_CONTENT_URI;
	}
    
	public OrderListHandler(Context context,int customer_id) {
		mContext =context;
		authority=PROVIDER_NAME.SYNC_ORDERLIST_CONTENT_URI;
		this.customer_id=customer_id;
	}
	
	public void endDocument() throws SAXException {
		Log.e(TAG,"SAX parse compelete");
	}

	public void startDocument() throws SAXException {
		resolver = mContext.getContentResolver();
		values = new ContentValues();
		if(customer_id==-1){
			customer_id = Constants.getCustomerId();
		}
//		resolver.delete(PROVIDER_NAME.ORDER_CONTENT_URI, PROVIDER_NAME.CUSTOMER_ID+"="+customer_id, null);
		resolver.delete(PROVIDER_NAME.ORDER_CONTENT_URI, NAME.CUSTOMER_ID+"="+customer_id, null);
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {

		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("order")) {
			values.put(PROVIDER_NAME.CUSTOMER_ID,customer_id);
			resolver.insert(authority,values);
			values.clear();
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();

		if (tag.equals("order_id")) {
			String xml_address = "order" + new String(p0, p1, p2)
					+ ".xml";
			values.put(PROVIDER_NAME.ORDER_XML, xml_address);
			values.put(PROVIDER_NAME.ORDER_ID, Integer
					.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("description"))
			values.put(PROVIDER_NAME.ORDER_COMMENT, new String(
					p0, p1, p2));
		else if (tag.equals("order_status"))
			values.put(PROVIDER_NAME.STATUS, new String(p0,
					p1, p2));
		else if (tag.equals("date_modified"))
			values.put(PROVIDER_NAME.ORDER_DATE, new String(
					p0, p1, p2));
		else if (tag.equals("order_status_id"))
			values.put(PROVIDER_NAME.STATUS_ID,Integer.valueOf(new String(
					p0, p1, p2)));
	}
}

//class OrderDetailHandler extends DefaultHandler {
//	// StringBuffer mString;
//	String charString = null;
//	private String mTag="",mProductName="",mAddress1="",mCity="";
//	Stack<String> tags = new Stack<String>();
//	ContentResolver resolver;
//	ContentValues order_values;
//	ContentValues order_product_values;
//	ContentValues price_values;
//	ContentValues product_values;
//	private ContentValues quo_pay_values;
//	SQLiteDatabase mDatabase;
//	Context mContext;
//	// String customer_id;
//	int mQuoteId,mOrderId,mProductId,weight_class_id,length_class_id,qty;
//	private Double weight,volume=1.0,tVolume=0.0,tWeight=0.0;
//    Cursor cursor;
//    private int deposit,period;
//	public OrderDetailHandler(Context context) {
//		mContext=context;
//	}
//
//	public void endDocument() throws SAXException {
//		Log.e("endDocument", "ok");
//		if(mDatabase.isOpen()){
//			mDatabase.close();
//		}
//	}
//
//	public void startDocument() throws SAXException {
//		resolver = mContext.getContentResolver();
//		mDatabase= new DatabaseHelper(mContext.getApplicationContext()).getWritableDatabase();
//		order_values = new ContentValues();
//		order_product_values = new ContentValues();
//	    price_values = new ContentValues();
//		product_values = new ContentValues();
//		quo_pay_values = new ContentValues();
//	}
//
//	public void startElement(String p0, String p1, String p2, Attributes p3)
//			throws SAXException {
//		tags.push(p1);
//		if(p1.equals("weight_class")){
//			mTag=p1;
//		} else if(p1.equals("length_class")){
//			mTag=p1;
//		} else if(p1.equals("qty_class")){
//			mTag=p1;
//		} else if(p1.equals("container")){
//			mTag=p1;
//		}
//	}
//
//	public void endElement(String p0, String p1, String p2)
//			throws SAXException {
//		tags.pop();
//		if (p1.equals("order")) {
//			order_values.put(PROVIDER_NAME.VOLUME, tVolume);
//			order_values.put(PROVIDER_NAME.WEIGHT, tWeight);
//			resolver.update(PROVIDER_NAME.QUOTE_CONTENT_URI,
//					order_values,PROVIDER_NAME.ORDER_ID+"="+mOrderId,null);
//			order_values.clear();
//		} else if (p1.equals("product")) {
//			order_product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);
//			order_product_values.put(PROVIDER_NAME.QUOTE_ID, mQuoteId);
//			resolver.insert(PROVIDER_NAME.QUOTE_PRODUCT_CONTENT_URI,order_product_values);
//			order_product_values.clear();
//			try {
//				weight=UnitTransform.weightTransform(mContext, weight, weight_class_id);
//				tWeight+=weight*qty;
//				volume=UnitTransform.volumeTransform(mContext, volume, length_class_id);
//				tVolume+=volume*qty;
//				cursor=resolver.query(PROVIDER_NAME.PRODUCT_CONTENT_URI, new String[]{PROVIDER_NAME.PRODUCT_ID},
//						PROVIDER_NAME.PRODUCT_ID+"="+mProductId, null, null);
//				if(cursor.getCount()==0){
//					resolver.insert(PROVIDER_NAME.PRODUCT_PRICE_CONTENT_URI, price_values);
//					price_values.clear();
//					product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);
//					product_values.put(PROVIDER_NAME.WEIGHT,weight);
//					product_values.put(PROVIDER_NAME.VOLUME,volume);
//					resolver.insert(PROVIDER_NAME.PRODUCT_CONTENT_URI, product_values);
//					product_values.clear();						
//				}
//				mProductName="";
//				volume=1.0;
//				cursor.close();
//			} catch (TransformException e) {
//				e.printStackTrace();
//				throw new SAXException(e.getMessage());
//			}
//		} else if(p1.equals("shipping_address")){
//			cursor = resolver.query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, 
//					new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
//					SYNC_PROVIDER_NAME.ADDRESS_1+"=\""+mAddress1+"\" and "+
//					SYNC_PROVIDER_NAME.CITY+"=\""+mCity+"\"",
//					null, null);
//			if(!cursor.moveToFirst()){
//				cursor.close();
////				throw new SAXException("no customer infor needed in local");
//				order_values.put(NAME.SHIPPING_ADDRESS_ID, 0);
//				return;
//			}
//			order_values.put(NAME.SHIPPING_ADDRESS_ID, cursor.getInt(0));
//			cursor.close();
//		} else if(p1.equals("payment_address")){
//			cursor = resolver.query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, 
//					new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
//					SYNC_PROVIDER_NAME.ADDRESS_1+"=\""+mAddress1+"\" and "+
//					SYNC_PROVIDER_NAME.CITY+"=\""+mCity+"\"",
//					null, null);
//			if(!cursor.moveToFirst()){
//				cursor.close();
////				throw new SAXException("no customer infor needed in local");
//				order_values.put(NAME.BILLING_ADDRESS_ID, 0);
//				return;
//			}
//			order_values.put(NAME.BILLING_ADDRESS_ID, cursor.getInt(0));
//			cursor.close();
//		} else if(p1.equalsIgnoreCase("payment_term")){
//    		Cursor cursor = mDatabase.query(NAME.TABLE_PAYMENT_TERM, 
//    				new String[]{NAME.PAYTERM_ID}, NAME.DEPOSIT+"="+deposit+" AND "+NAME.GRACE_PERIOD+"="+period,
//    				null, null, null, null);
//    		if(cursor.moveToFirst()){
//    			quo_pay_values.put(NAME.PAYTERM_ID, cursor.getInt(0));
//    		}else{
//    			quo_pay_values.put(NAME.PAYTERM_ID, -1);
//    		}
//    		cursor.close();
//    		if(quo_pay_values.getAsString(NAME.PAYMENT_ID)!=null||quo_pay_values.getAsInteger(NAME.PAYTERM_ID)!=-1){
//    			quo_pay_values.put(NAME.QUOTE_ID, mQuoteId);
//    			mDatabase.insert(NAME.TABLE_QUOTE_TO_PAYMENT, null, quo_pay_values);
//    		}
//    		quo_pay_values.clear();quo_pay_values=null;
//    	}
//
//	}
//
//	public void characters(char[] p0, int p1, int p2) throws SAXException {
//
//		String tag = tags.peek();
//
//		if (tag.equals("product_id")) {
//			mProductId = Integer.valueOf(new String(p0, p1, p2));
//			order_product_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
//			price_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
//			product_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
//		} else if (tag.equals("name")) {
//			mProductName=mProductName+new String(p0, p1, p2);
//		} else if (tag.equals("quantity")) {
//			qty=Integer.valueOf(new String(p0, p1, p2));
//			order_product_values.put(PROVIDER_NAME.QTY, qty);
//		} else if (tag.equals("price")) {
//			Double price;
//			try {
//				if(p0[0]>'9'||p0[0]<'0'){
//					price=Double.valueOf(new String(p0, p1+1, p2-1).replace(",", ""));
//					price=CurrencyTransform.transform(mContext, price, p0[0], true);					
//				}else{
//					price=Double.valueOf(new String(p0, p1, p2-1).replace(",", ""));
//					price=CurrencyTransform.transform(mContext, price, p0[p2-1], false);
//				}
//			} catch (TransformerException e) {
//				e.printStackTrace();
//				throw new SAXException(e.getMessage());
//			}
//			price_values.put(PROVIDER_NAME.PRICE, price);
////			order_product_values.put(PROVIDER_NAME.PRICE, price);
//		} else if (tag.equals("minimum")) {
//			price_values.put(PROVIDER_NAME.QTY, Integer
//					.valueOf(new String(p0, p1, p2)));
//			product_values.put(PROVIDER_NAME.MINQTY,Integer
//					.valueOf(new String(p0, p1, p2)));
//		} //else if (tag.equals("minimum")) {
////			product_values.put(PROVIDER_NAME.QTY_UNIT,new String(p0, p1, p2));
//		//}
//		else if (tag.equals("weight")){
//			weight=Double.valueOf(new String(p0, p1, p2));
//		} else if (tag.equals("length")){
//			volume*=Double.valueOf(new String(p0, p1, p2));
//		} else if (tag.equals("height")){
//			volume*=Double.valueOf(new String(p0, p1, p2));
//		} else if (tag.equals("width")){
//		    volume*=Double.valueOf(new String(p0, p1, p2));
//    	} else if (tag.equals("id")){
//			if(mTag.equals("weight_class")){
//				weight_class_id=Integer.valueOf(new String(p0,
//						p1, p2));
//				mTag="";
//			} else if(mTag.equals("length_class")){
//				length_class_id=Integer.valueOf(new String(p0,
//						p1, p2));
//				mTag="";
//			} else if(mTag.equals("container")){
//				order_values.put(PROVIDER_NAME.CONTAINER_CLASS_ID,Integer.valueOf(new String(
//						p0, p1, p2)));
//				mTag="";
//			}
//		} else if (tag.equals("address_1")) {
//			mAddress1=new String(p0, p1, p2);
//		} else if (tag.equals("city")){
//			mCity=new String(p0, p1, p2);
//		} else if (tag.equals("order_id")) {
//			mOrderId=Integer.valueOf(new String(p0, p1, p2));
//			order_values.put(PROVIDER_NAME.ORDER_ID, mOrderId);
//			resolver.insert(PROVIDER_NAME.QUOTE_CONTENT_URI,
//					order_values);
//			cursor = resolver.query(
//					PROVIDER_NAME.QUOTE_CONTENT_URI,
//					new String[] { PROVIDER_NAME.QUOTE_ID },
//					PROVIDER_NAME.ORDER_ID
//							+ "="
//							+ order_values
//									.getAsString(PROVIDER_NAME.ORDER_ID),
//					null, null);
//			cursor.moveToFirst();
//			mQuoteId = cursor.getInt(0);
//			cursor.close();
//		} else if (tag.equals("customer_id")) {
//			order_values.put(PROVIDER_NAME.CUSTOMER_ID, Integer
//					.valueOf(new String(p0, p1, p2)));
//		} else if (tag.equals("description")) {
//			order_values.put(PROVIDER_NAME.DESCRIPTION,new String(p0, p1, p2));
//		} else if (tag.equals("payment_method_id")){
//    		Cursor cursor = mDatabase.query(NAME.TABLE_PAYMENT, new String[]{NAME.PAYMENT_ID},
//    				NAME.PAYMENT_ID+"=\""+new String(p0, p1, p2)+"\"", null, null, null, null);
//    		if(cursor.moveToFirst()){
//    			quo_pay_values.put(NAME.PAYMENT_ID, new String(p0, p1, p2));
//    		}
//    		cursor.close();
//    	} else if (tag.equals("deposit")){
//    		deposit = Integer.valueOf(new String(p0, p1, p2));
//    	} else if (tag.equals("grace_period")){
//    		period = Integer.valueOf(new String(p0, p1, p2));
//    	}
//	}
//	//used for sync
//}

class OrderTemplateListHandler extends DefaultHandler{
    private final static String TAG = "OrderTemplateListHandler";
	private Stack<String> tags = new Stack<String>();
	private SQLiteDatabase mDataBase;
	private ContentValues values;
	private String mTag="",mName="";
	private Context mContext;
	public OrderTemplateListHandler(Context context) {
		mContext=context;
	}

	public void endDocument() throws SAXException {
		Log.e(TAG,"parse finished");
		if(mDataBase.isOpen()){
			mDataBase.close();
		}
	}

	public void startDocument() throws SAXException {
		Log.e(TAG,"parse start");
		mDataBase=new DatabaseHelper(mContext).getWritableDatabase();
		mDataBase.delete(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS, null, null);
		mDataBase.delete(PROVIDER_NAME.TABLE_TEMP_ORDERS, null, null);
		mDataBase.delete(PROVIDER_NAME.TABLE_TEMP_ORDER_DETAIL_FACETS, null, null);
		mDataBase.delete(PROVIDER_NAME.TABLE_TEMP_ORDER_PRODUCTS, null, null);
        values=new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		if(p1.equalsIgnoreCase("container")){
            mTag=p1;
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("facet")) {
			values.put(PROVIDER_NAME.NAME,mName);
			mDataBase.insert(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS, null, values);
		}else if(p1.equals("order_template")){
            values.put(PROVIDER_NAME.NAME,mName);mName="";
			mDataBase.insert(PROVIDER_NAME.TABLE_TEMP_ORDERS, null, values);
		}else if(p1.equalsIgnoreCase("criteria")){
			mName="";
		}else if(p1.equalsIgnoreCase("facets")){
            values.clear();
		}else if(p1.equals("container")){
			mTag="";
		}else if(p1.equals("order_template_list")){
			mDataBase.close();
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("name")) {
			if(!mTag.equalsIgnoreCase("container")){
				mName=mName+new String(p0,p1,p2);				
			}
		} else if (tag.equals("value")){
			values.put(PROVIDER_NAME.VALUE,new String(p0,p1,p2));
		} else if (tag.equals("number_of_template")){
			values.put(PROVIDER_NAME.NUMBER,Integer.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("template_ids")){
			values.put(PROVIDER_NAME.TEMP_IDS,new String(p0,p1,p2));
		} 
		else if (tag.equals("id")){
			if(mTag.equalsIgnoreCase("container")){
				values.put(PROVIDER_NAME.CONTAINER_ID,Integer.valueOf(new String(p0,p1,p2)));
			} else{
				values.put(PROVIDER_NAME.ORDER_ID,Integer.valueOf(new String(p0,p1,p2)));
			}
		} else if (tag.equals("date_end")){
			values.put(PROVIDER_NAME.END_DATE,Integer.valueOf(new String(p0,p1,p2)));
		} 
	}
}

class OrderTemplateDetailHandler extends DefaultHandler{
    private final static String TAG = "OrderTemplateListHandler";
	private Stack<String> tags = new Stack<String>();
	private SQLiteDatabase mDataBase;
	private ContentValues values;
	private String mTag="",mName="",mFacetValue="";
	private int mOrderID,mProductId;
	private Context mContext;
	private byte[] mTempOrderBuffer;
	public OrderTemplateDetailHandler(Context context,byte[] buffer) {
		mContext=context;
		mTempOrderBuffer=buffer;
	}

	public void endDocument() throws SAXException {
		Log.e(TAG,"parse finished");
		if(mDataBase.isOpen()){
			if(mDataBase.inTransaction()){
				mDataBase.endTransaction();
			}
			mDataBase.close();
		}
	}

	public void startDocument() throws SAXException {
		Log.e(TAG,"parse start");
		mDataBase=new DatabaseHelper(mContext).getWritableDatabase();
		mDataBase.beginTransaction();
        values=new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		if(p1.equalsIgnoreCase("container")){
            mTag=p1;
		} else if(p1.equalsIgnoreCase("order_template")){
            mTag=p1;
		} else if(p1.equalsIgnoreCase("qty_class")){
            mTag=p1;
		} else if(p1.equalsIgnoreCase("facets")){
            mName="";
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("value")) {
			values.put(PROVIDER_NAME.NAME,mName);
			values.put(PROVIDER_NAME.VALUE,mFacetValue);mFacetValue="";
			mDataBase.insert(PROVIDER_NAME.TABLE_TEMP_ORDER_DETAIL_FACETS, null, values);
		}else if(p1.equals("facet")){
			mName="";
		}else if(p1.equals("product")){
			values.put(PROVIDER_NAME.NAME,mName);mName="";
			mDataBase.insert(PROVIDER_NAME.TABLE_TEMP_ORDER_PRODUCTS, null, values);
		}else if(p1.equalsIgnoreCase("facets")){
            values.clear();
            values.put(PROVIDER_NAME.ORDER_ID,mOrderID);
		}else if(p1.equals("product_list")){
			String xml_address = "temp_order"
				+ mOrderID + ".xml";
			FileOutputStream outputStream;
			try {
				outputStream = mContext.openFileOutput(xml_address,
						Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(mTempOrderBuffer);
				outputStream.close();
				values.clear();
				values.put(PROVIDER_NAME.PRODUCT_XML,
						xml_address);
				mDataBase.update(PROVIDER_NAME.TABLE_TEMP_ORDERS,values, PROVIDER_NAME.ORDER_ID+"="+mOrderID,null);
				mDataBase.setTransactionSuccessful();
				mDataBase.endTransaction();
				mDataBase.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("name")) {
			if(mTag.equalsIgnoreCase("qty_class")){
				values.put(PROVIDER_NAME.QTY_UNIT,new String(p0,p1,p2));
				mTag="";
			}else{
				mName=mName+new String(p0,p1,p2);
			}
		} else if (tag.equals("value")){
			mFacetValue=mFacetValue+new String(p0,p1,p2);
		} else if (tag.equals("product_id")){
			mProductId=Integer.valueOf(new String(p0,p1,p2));
			values.put(PROVIDER_NAME.PRODUCT_ID,mProductId);
		} else if (tag.equals("price")){
			values.put(PROVIDER_NAME.PRICE,Double.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("quantity")){
			values.put(PROVIDER_NAME.QTY,Integer.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("image")){
			//TODO
			/*String address="temp_order"+mProductId+".png";
			values.put(PROVIDER_NAME.IMAGE,address);
			try {
				byte[] data = NetworkUtilities.downloadImage(new String(p0, p1, p2));

				FileOutputStream outputStream = mContext.openFileOutput(
						String.valueOf(mProduct_id) + img_count + ".png",
						Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(data);
				outputStream.close();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();      
			} catch (ResponseException e) {
				e.printStackTrace();
			}
			img_count++;*/
			try {
				values.put(PROVIDER_NAME.IMAGE,NetworkUtilities.downloadImage(new String(p0, p1, p2)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ResponseException e) {
				e.printStackTrace();
			}
		} else if (tag.equals("id")&&mTag.equalsIgnoreCase("order_template")){
			mOrderID=Integer.valueOf(new String(p0,p1,p2));
			values.put(PROVIDER_NAME.ORDER_ID,mOrderID);
			mTag="";
		} 
	}
}

class ProductParseToNewQuoteHandler extends DefaultHandler {
    private final static String TAG = "ProductHandler";
    private SQLiteDatabase mDatabase;
	private Stack<String> tags = new Stack<String>();
	private int mQuoteId,mProduct_id,weight_class_id,length_class_id;
	private String mTag="",mProductName="";
	private ContentValues product_values;
	private ContentValues price_values;
	private ContentValues quote_values;
	private ContentValues quote_product_values;
	private Context mContext;
    private Double weight,volume=1.0,tWeight=0.0,tVolume=0.0;
    private boolean mHasThisProduct=false;
//    int count=0;//TODO delete
	public ProductParseToNewQuoteHandler(Context context) {
		mContext = context;
	}

	public void endDocument() throws SAXException {
//		System.out.println("ProductHandler"+count);//TODO delete
		if(mDatabase.isOpen()){
			if(mDatabase.inTransaction()){
				mDatabase.endTransaction();
			}
			mDatabase.close();
		}
	}

	public void startDocument() throws SAXException {
		mDatabase=new DatabaseHelper(mContext).getWritableDatabase();
		mDatabase.beginTransaction();
		quote_values=new ContentValues();
	    quote_product_values=new ContentValues();
		product_values = new ContentValues();
		price_values = new ContentValues();
		product_values.put(PROVIDER_NAME.LAST_ACCESS_DATE,
				System.currentTimeMillis());
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		if(p1.equals("weight_class")){
			mTag=p1;
		} else if(p1.equals("length_class")){
			mTag=p1;
		} else if(p1.equals("qty_class")){
			mTag=p1;
		} else if(p1.equals("product")){
			mTag=p1;
		} else if(p1.equals("order_template")){
			mTag=p1;
		} else if(p1.equals("container")){
			mTag=p1;
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("price")) {
			if(!mHasThisProduct){
				mDatabase.insert(PROVIDER_NAME.TABLE_PRODUCT_PRICE, null, price_values);
			}
			price_values= new ContentValues();
			price_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
		} else if (p1.equals("product")) {
			try {
				product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);
				quote_product_values.put(PROVIDER_NAME.PRODUCT_NAME, mProductName);mProductName="";mTag="";
				weight=UnitTransform.weightTransform(mContext, weight, weight_class_id);
				volume=UnitTransform.volumeTransform(mContext, volume, length_class_id);
				if(!mHasThisProduct){					
						product_values.put(PROVIDER_NAME.WEIGHT,weight);
						product_values.put(PROVIDER_NAME.VOLUME,volume);
						mDatabase.insert(PROVIDER_NAME.TABLE_PRODUCT, null, product_values);					
				}

				tWeight+=weight*quote_product_values.getAsInteger(PROVIDER_NAME.QTY);
				tVolume+=volume*quote_product_values.getAsInteger(PROVIDER_NAME.QTY);
				volume=1.0;
				weight=0.0;
				mDatabase.insert(PROVIDER_NAME.TABLE_QUOTE_PRODUCT, null, quote_product_values);
			} catch (TransformException e1) {
				e1.printStackTrace();
				throw new SAXException("unit transform exception");
			} 
		} else if (p1.equals("product_list")) {
			quote_values.put(PROVIDER_NAME.WEIGHT,tWeight);
			quote_values.put(PROVIDER_NAME.VOLUME,tVolume);
			mDatabase.update(PROVIDER_NAME.TABLE_QUOTES, quote_values, PROVIDER_NAME.QUOTE_ID+"="+mQuoteId, null);
			mDatabase.setTransactionSuccessful();		
		}
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		
		String tag = tags.peek();
//		System.out.println("tags:"+new String(p0));
//	    if(p0.toString().trim().length()==0){
//	    	count++;
//	    }
		if (tag.equals("product_id")) {
			mProduct_id = Integer.valueOf(new String(p0, p1, p2));
			Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_PRODUCT, new String[]{PROVIDER_NAME.PRODUCT_ID},
					PROVIDER_NAME.PRODUCT_ID+"="+mProduct_id, null, null, null, null);
			if(cursor.moveToFirst()){
				mHasThisProduct=true;
			}else{
				mHasThisProduct=false;
			}
			cursor.close();
			quote_product_values.put(PROVIDER_NAME.PRODUCT_ID, mProduct_id);
			product_values.put(PROVIDER_NAME.PRODUCT_ID, mProduct_id);
			price_values.put(PROVIDER_NAME.PRODUCT_ID,mProduct_id);
		} else if (tag.equals("minimum")){
			product_values.put(PROVIDER_NAME.MINQTY,
					Integer.valueOf(new String(p0, p1, p2)));
			price_values.put(PROVIDER_NAME.QTY, Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("name")){
			if(mTag.equalsIgnoreCase("product")){
				mProductName=mProductName+new String(p0, p1, p2);
//				mTag="";
			}else if(mTag.equalsIgnoreCase("qty_class")){
				product_values.put(PROVIDER_NAME.QTY_UNIT,new String(p0, p1, p2));
				mTag="";
			}
		} else if (tag.equals("quantity")){
			quote_product_values.put(PROVIDER_NAME.QTY, Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("minimum")){
			price_values.put(PROVIDER_NAME.QTY, Integer.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("price")){
			price_values.put(PROVIDER_NAME.PRICE,
					Double.valueOf(new String(p0, p1, p2)));
//			quote_product_values.put(NAME.PRICE, Double.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("weight")){
			weight=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("length")){
			Log.e(TAG,"length:"+new String(p0, p1, p2));
			volume*=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("height")){
			volume*=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("width")){
		    volume*=Double.valueOf(new String(p0, p1, p2));
    	} else if (tag.equals("id")){
			if(mTag.equals("weight_class")){
				weight_class_id=Integer.valueOf(new String(p0,
						p1, p2));
				mTag="";
			} else if(mTag.equals("length_class")){
				length_class_id=Integer.valueOf(new String(p0,
						p1, p2));
				mTag="";
			} else if(mTag.equals("container")){
				quote_values.put(PROVIDER_NAME.CONTAINER_CLASS_ID, Integer.valueOf(new String(p0, p1, p2)));
				mTag="";
			} else if(mTag.equals("order_template")){
				//mQuoteId=Integer.valueOf(new String(p0, p1, p2));
                quote_values.put(PROVIDER_NAME.TEMP_ORDER_ID, Integer.valueOf(new String(p0, p1, p2)));
                quote_values.put(PROVIDER_NAME.CUSTOMER_ID, Constants.getCustomerId());
                quote_values.put(PROVIDER_NAME.BILLING_ADDRESS_ID, -1);
                quote_values.put(PROVIDER_NAME.SHIPPING_ADDRESS_ID, -1);
                quote_values.put(PROVIDER_NAME.CONTAINER_CLASS_ID, 1);
//                quote_values.put(PROVIDER_NAME.PAYMENT_ID, "null");
                quote_values.put(PROVIDER_NAME.ORDER_ID,-1);
				mDatabase.insert(PROVIDER_NAME.TABLE_QUOTES, null, quote_values);
				Cursor cursor = mDatabase.query(PROVIDER_NAME.TABLE_QUOTES,
						new String[]{PROVIDER_NAME.QUOTE_ID}, 
						PROVIDER_NAME.TEMP_ORDER_ID+"="+quote_values.getAsInteger(PROVIDER_NAME.TEMP_ORDER_ID)+
						" and "+PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null, null, null);
				if(cursor.moveToFirst()){
					mQuoteId=cursor.getInt(0);
				}
				quote_product_values.put(PROVIDER_NAME.QUOTE_ID, mQuoteId);
				cursor.close();
				mTag="";
			}
		} 
	}
}

