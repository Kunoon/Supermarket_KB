package com.koobest.m.sync.toolkits;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

public class DefaultHandlerFactory {
	public final static int PARSE_CUSTOMERPROFILE_XML=0;
	public final static int PARSE_CURRENCYLIST_XML=1;
	public final static int PARSE_ORDERDETAIL_XML=2;
	public final static int PARSE_SIMPLE_QUOTE_XML=3;
	public final static int PARSE_WEIGHT_UNITLIST_XML=4;
	public final static int PARSE_LENGHT_UNITLIST_XML=5;
	public final static int PARSE_CONTAINERLIST_XML=6;
	public static final int PARSE_MANUFACTURERLIST_XML = 7;
	public static final int PARSE_CATEGORYLIST_XML = 8;
	public static final int PARSE_BRAND_LIST_XML = 9;
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
     	 case PARSE_CUSTOMERPROFILE_XML:
     		 if(context==null||source==null)
     		     return null;
     		 else
     			 return new CustomerContentHandler(context,source);
     	 case PARSE_CURRENCYLIST_XML:
     		 if(context==null)
    		     return null;
    		 else
     		     return new CurrencyListHandler(context);
     	 case PARSE_WEIGHT_UNITLIST_XML:
     		 if(context==null)
    		     return null;
    		 else
     		     return new WeightUnitListHandler(context);
     	 case PARSE_LENGHT_UNITLIST_XML:
     		 if(context==null)
   		         return null;
   		     else
     		     return new LengthUnitListHandler(context);
     	 case PARSE_CONTAINERLIST_XML:
   		     if(context!=null)
   		         return new ContainerListHandler(context);
     	 case PARSE_MANUFACTURERLIST_XML:
  		     if(context!=null)
  		         return new ManufacturerListHandler(context);
         case PARSE_CATEGORYLIST_XML:
  		     if(context!=null)
  		         return new ProductCategoryListHandler(context);
         case PARSE_BRAND_LIST_XML:
  		     if(context!=null)
  		         return new BrandListHandler(context);
     	 default:
     		 return null;
     	 }		
     }
     
     public static DefaultHandler createHandler(int type,Context context){
    	 return createHandler(type, context,null);	
     }
}

class CustomerContentHandler extends DefaultHandler {
	private static final String TAG="CustomerContentHandler";
	private Context mContext;
	private Stack<String> tags;
	private ContentResolver resolver;				
	private ContentValues customer_values;
	private ContentValues address_values;
	private ContentValues payment_values;
	private int customer_id;
	private String address_id,payment_id;
	private String address_selection, selectionArgs[];
	private String xml_address;
	private byte[] buffer;
	private Cursor cursor;
	
	
	public CustomerContentHandler(Context context,byte[] buffer) {				
		customer_values=new ContentValues();
		address_values=new ContentValues();
		payment_values=new ContentValues();
		address_selection=SYNC_PROVIDER_NAME.CUSTOMER_ID + "=? and " +SYNC_PROVIDER_NAME.ADDRESS_ID+"=?";
		tags=new Stack<String>();
		mContext = context;
		this.buffer=buffer;
	}

	public void endDocument() throws SAXException {
		 Log.e("endDocument",String.valueOf("endDocument"));
		
	}
	
	public void startDocument() throws SAXException{
		resolver=mContext.getContentResolver();
	}
	
	public void startElement(String p0, String p1, String p2, Attributes p3) throws SAXException { 	
		tags.push(p1); 	
	}
	

	@Override
	public void endElement(String p0, String p1, String p2)
		throws SAXException {
		tags.pop();
		if(p1.equals("customer")){
			try {
				xml_address="customer"+customer_id+".xml";
				FileOutputStream outputStream = mContext.openFileOutput(xml_address,Activity.MODE_WORLD_WRITEABLE);
		        outputStream.write(buffer);
				outputStream.close();
				customer_values.put(SYNC_PROVIDER_NAME.CUSTOMER_XML,xml_address);
				cursor = resolver.query(SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI,
					    new String[] { SYNC_PROVIDER_NAME.CUSTOMER_ID },
						     SYNC_PROVIDER_NAME.CUSTOMER_ID + "="+customer_id, null, null);
				if(cursor.moveToFirst()){
				    resolver.update(SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, customer_values, SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+ customer_id, null);
				}else{
				    resolver.insert(SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, customer_values);
				}
				customer_values.clear();
				cursor.close();
			} catch (IOException e) {
		        e.printStackTrace();
			}   
		} else if(p1.equals("address")){
		    selectionArgs=new String[]{String.valueOf(customer_id),address_id};
            cursor = resolver.query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI,
					new String[] { SYNC_PROVIDER_NAME.CUSTOMER_ID },
					address_selection,selectionArgs, null);
            Log.e(TAG,"cursor.count:" + cursor.getCount()+"cursor.movetofirst"+cursor.moveToFirst());
			if(cursor.moveToFirst()){
				resolver.update(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, address_values, 
						address_selection, selectionArgs);
			} else{
				resolver.insert(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, address_values);		
			}
			cursor.close();
		    address_values.clear();
		    address_values.put(SYNC_PROVIDER_NAME.CUSTOMER_ID, customer_id);
        }
       
        else if(p1.equals("payment_method")){
        	cursor = mContext.getContentResolver().query(SYNC_PROVIDER_NAME.PAYMENT_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.PAYMENT_ID}, 
					SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+customer_id+" and "+
					SYNC_PROVIDER_NAME.PAYMENT_ID+"=\""+payment_id+"\"", null, null);
        	if(cursor.getCount()>0){
        		cursor.close();
        		resolver.update(SYNC_PROVIDER_NAME.PAYMENT_CONTENT_URI,payment_values,
        				SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+customer_id+" and "+
    					SYNC_PROVIDER_NAME.PAYMENT_ID+"=\""+payment_id+"\"", null);
        	}else{
        		cursor.close();
        		resolver.insert(SYNC_PROVIDER_NAME.PAYMENT_CONTENT_URI, payment_values);
        	}
		}
    }
	public void characters(char[] p0, int p1, int p2) throws SAXException { 
		String tag=(String) tags.peek(); 
	    
		if (tag.equals("customer_id")) {
			 Log.e("customer_id",String.valueOf(new String(p0,p1,p2)));
			customer_id=Integer.valueOf(new String(p0,p1,p2));
			customer_values.put(SYNC_PROVIDER_NAME.CUSTOMER_ID,customer_id);
			address_values.put(SYNC_PROVIDER_NAME.CUSTOMER_ID,customer_id);	
			payment_values.put(SYNC_PROVIDER_NAME.CUSTOMER_ID,customer_id);
	    } else if (tag.equals("firstname")){
			customer_values.put(SYNC_PROVIDER_NAME.CUSTOMER_FIRSTNAME,new String(p0,p1,p2));
		} else if (tag.equals("lastname")) {	
			customer_values.put(SYNC_PROVIDER_NAME.CUSTOMER_LASTNAME,new String(p0,p1,p2));
		} else if (tag.equals("email")){
			customer_values.put(SYNC_PROVIDER_NAME.EMAIL,new String(p0,p1,p2));
			 Log.e("email",String.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("telephone")){
			customer_values.put(SYNC_PROVIDER_NAME.TELEPHONE,new String(p0,p1,p2));
			Log.e("telephone",String.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("date_added")){
			customer_values.put(SYNC_PROVIDER_NAME.DATE_ADDED,new String(p0,p1,p2));
			Log.e("date_added",String.valueOf(new String(p0,p1,p2)));
		}
		
		else if (tag.equals("address_id")){
			address_id=new String(p0,p1,p2);
			address_values.put(SYNC_PROVIDER_NAME.ADDRESS_ID,Integer.valueOf(address_id));		
		} else if (tag.equals("address_1")){
			address_values.put(SYNC_PROVIDER_NAME.ADDRESS_1, new String(p0,p1,p2));
		} else if (tag.equals("address_2")){
			address_values.put(SYNC_PROVIDER_NAME.ADDRESS_2,new String(p0,p1,p2));
		} else if (tag.equals("city")){
			address_values.put(SYNC_PROVIDER_NAME.CITY,new String(p0,p1,p2));
		} else if (tag.equals("zone")){
			address_values.put(SYNC_PROVIDER_NAME.ZONE,new String(p0,p1,p2));
		}				

        else if (tag.equals("id")){
        	payment_id=new String(p0,p1,p2);
			payment_values.put(SYNC_PROVIDER_NAME.PAYMENT_ID,payment_id);
        }else if (tag.equals("title")){
        	payment_values.put(SYNC_PROVIDER_NAME.TITLE,new String(p0,p1,p2));
        }else if (tag.equals("description")){
        	payment_values.put(SYNC_PROVIDER_NAME.DESCRIPTION,new String(p0,p1,p2));
        }else if (tag.equals("discount")){
        	payment_values.put(SYNC_PROVIDER_NAME.DISCOUNT,Double.valueOf(new String(p0,p1,p2)));
        }
	}	
}

class CurrencyListHandler extends DefaultHandler {
	private static final String TAG="CurrencyListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private Context mContext;
	private Cursor cursor;
	private String currency_id;
	private String selection;
	private String[] selectionArgs;
	public CurrencyListHandler(Context context) {
		mContext =context;
	}

	public void endDocument() throws SAXException {
	}

	public void startDocument() throws SAXException {
		resolver = mContext.getContentResolver();
		values = new ContentValues();
		selection=SYNC_PROVIDER_NAME.CURRENCY_ID+"=?";
		selectionArgs = new String[1];
		 Log.e(TAG,"start Parse");
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("currency")) {
			 selectionArgs[0]=currency_id;
	         cursor = resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
				        new String[] { SYNC_PROVIDER_NAME.CURRENCY_ID },
					    selection,selectionArgs, null);
	         Log.e(TAG,"cursor.count:" + cursor.getCount()+"cursor.movetofirst"+cursor.moveToFirst());
			 if(cursor.moveToFirst()){
				 Log.e(TAG, "uodate address_id:"+currency_id);
				 resolver.update(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI, values, 
							selection, selectionArgs);
			 } else{
				 Log.e(TAG, "insert address_id:"+currency_id);
				 resolver.insert(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI, values);		
			 }
			 cursor.close();
			 values.clear();
		}
	}
	public void characters(char[] p0, int p1, int p2)
	     throws SAXException {
		String tag = tags.peek();
		if (tag.equals("currency_id")) {
			currency_id=new String(p0, p1, p2);
			values.put(SYNC_PROVIDER_NAME.CURRENCY_ID,currency_id);
			
		} else if (tag.equals("code")){
			values.put(SYNC_PROVIDER_NAME.CODE, new String(
					p0, p1, p2));
		} else if (tag.equals("title")){
			values.put(SYNC_PROVIDER_NAME.TITLE, new String(
					p0, p1, p2));
		} else if (tag.equals("symbol_left")){
			values.put(SYNC_PROVIDER_NAME.SYMBOL_LEFT, new String(
					p0, p1, p2));
		} else if (tag.equals("symbol_right")){
			values.put(SYNC_PROVIDER_NAME.SYMBOL_RIGHT, new String(
					p0, p1, p2));
		} else if (tag.equals("decimal_place")){
			values.put(SYNC_PROVIDER_NAME.DECIMAL_PLACE, new String(
					p0, p1, p2));
		} else if (tag.equals("value")){
			values.put(SYNC_PROVIDER_NAME.VALUE, new String(
					p0, p1, p2));
		} else if (tag.equals("date_modified")){
			values.put(SYNC_PROVIDER_NAME.DATA_MODIFIED, new String(
					p0, p1, p2));
		}
	}
}

class WeightUnitListHandler extends DefaultHandler {
	private static final String TAG="WeightUnitListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private Context mContext;
	private String weight_class_id;
	public WeightUnitListHandler(Context context) {
		mContext =context;
	}

	public void endDocument() throws SAXException {
	}

	public void startDocument() throws SAXException {
		resolver = mContext.getContentResolver();
		resolver.delete(SYNC_PROVIDER_NAME.WEIGHT_UNITS_CONTENT_URI, null, null);
		values = new ContentValues();
		 Log.e(TAG,"start Parse");
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("weight_class")) {
			resolver.insert(SYNC_PROVIDER_NAME.WEIGHT_UNITS_CONTENT_URI, values);
			values.clear();
		}
	}
	public void characters(char[] p0, int p1, int p2)
	     throws SAXException {
		String tag = tags.peek();
		if (tag.equals("weight_class_id")) {
			weight_class_id=new String(p0, p1, p2);
			values.put(SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID,weight_class_id);
		} else if (tag.equals("title")){
			values.put(SYNC_PROVIDER_NAME.TITLE, new String(
					p0, p1, p2));
		} else if (tag.equals("unit")){
			values.put(SYNC_PROVIDER_NAME.UNIT, new String(
					p0, p1, p2));
		} else if (tag.equals("value")){
			values.put(SYNC_PROVIDER_NAME.VALUE, new String(
					p0, p1, p2));
		} 
	}
}

class LengthUnitListHandler extends DefaultHandler {
	private static final String TAG="WeightUnitListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private Context mContext;
	private String length_class_id;
	public LengthUnitListHandler(Context context) {
		mContext =context;
	}

	public void endDocument() throws SAXException {
	}

	public void startDocument() throws SAXException {
		resolver = mContext.getContentResolver();
		resolver.delete(SYNC_PROVIDER_NAME.LENGTH_UNITS_CONTENT_URI, null, null);
		values = new ContentValues();
		Log.e(TAG,"start Parse");
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("length_class")) {
			values.put(SYNC_PROVIDER_NAME.LENGHT_CLASS_ID,Integer.valueOf(length_class_id));
			resolver.insert(SYNC_PROVIDER_NAME.LENGTH_UNITS_CONTENT_URI, values);		
			values.clear();
		}
	}
	public void characters(char[] p0, int p1, int p2)
	     throws SAXException {
		String tag = tags.peek();
		if (tag.equals("length_class_id")) {
			length_class_id=new String(p0, p1, p2);
		} else if (tag.equals("title")){
			values.put(SYNC_PROVIDER_NAME.TITLE, new String(
					p0, p1, p2));
		} else if (tag.equals("unit")){
			values.put(SYNC_PROVIDER_NAME.UNIT, new String(
					p0, p1, p2));
		} else if (tag.equals("value")){
			values.put(SYNC_PROVIDER_NAME.VALUE, new String(
					p0, p1, p2));
		} 
	}
}

class ContainerListHandler extends DefaultHandler {
    private static final String TAG="OrderListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private int container_class_id=-1;//,length_class_id,weight_class_id;
	private Cursor cursor;
	private double volume=1;//,weight=0;
    //private String mTag="";
	public ContainerListHandler(Context context) {
		resolver = context.getContentResolver();
	}
	
	public void endDocument() throws SAXException {
		Log.e(TAG,"SAX parse compelete");
	}

	public void startDocument() throws SAXException {
		resolver.delete(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI, null, null);
		values = new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
		/*if(p1.equals("length_class")||p1.equals("weight_class")){
			mTag=p1;
		}*/
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("container")) {
			values.put(SYNC_PROVIDER_NAME.VOLUME, volume);
			volume=1;
			//values.put(SYNC_PROVIDER_NAME.WEIGHT, weight);
			resolver.insert(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI, values);
		}
	}
	
	public void characters(char[] p0, int p1, int p2)
	throws SAXException {
        String tag = tags.peek();
        if (tag.equals("container_id")) {
        	container_class_id=Integer.valueOf(new String(p0, p1, p2));
	         values.put(SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID,container_class_id);	
        } else if (tag.equals("name")){
        	 values.put(SYNC_PROVIDER_NAME.NAME,new String(p0, p1, p2));	
        } else if (tag.equals("length")||tag.equals("width")||tag.equals("height")){
        	 volume*=Double.valueOf(new String(p0, p1, p2));
        } else if (tag.equals("max_load_weight")){
        	 values.put(SYNC_PROVIDER_NAME.WEIGHT,Double.valueOf(new String(p0, p1, p2)));	
        } /*else if(tag.equals("id")){
        	if(mTag.equalsIgnoreCase("length_class")){
        		length_class_id=Integer.valueOf(new String(p0, p1, p2));
        	} else if(mTag.equalsIgnoreCase("weight_class")){
        		weight_class_id=Integer.valueOf(new String(p0, p1, p2));
        	}
        }*/
    }
}

class ManufacturerListHandler extends DefaultHandler {
    private static final String TAG="OrderListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private int manufacturer_class_id=-1;
    private String mName="";
	public ManufacturerListHandler(Context context) {
		resolver = context.getContentResolver();
	}
	
	public void endDocument() throws SAXException {
		Log.e(TAG,"SAX parse compelete");
	}

	public void startDocument() throws SAXException {
		resolver.delete(SYNC_PROVIDER_NAME.MANUFACTURER_CONTENT_URI, null, null);
		values = new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("manufacturer")) {
        	values.put(SYNC_PROVIDER_NAME.NAME,mName);    	
			resolver.insert(SYNC_PROVIDER_NAME.MANUFACTURER_CONTENT_URI, values);
			mName="";
		}
	}
	
	public void characters(char[] p0, int p1, int p2)
	throws SAXException {
        String tag = tags.peek();
        if (tag.equals("manufacturer_id")) {
        	 manufacturer_class_id=Integer.valueOf(new String(p0, p1, p2));
	         values.put(SYNC_PROVIDER_NAME.MANUFACTURER_ID,manufacturer_class_id);	
        } else if (tag.equals("name")){
//        	Log.i("asdfasdf",new String(p0, p1, p2));
//        	if(new String(p0,p1,p2).equalsIgnoreCase("&amp;")){
//        		name=name+"&";
//        	}else{
        		mName=mName+new String(p0, p1, p2);
//        	}
        	 	
        } 
    }
}

class BrandListHandler extends DefaultHandler {
    private static final String TAG="BrandListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private int brand_class_id=-1;
    private String mName="",mImage="";
	public BrandListHandler(Context context) {
		resolver = context.getContentResolver();
	}
	
	public void endDocument() throws SAXException {
		Log.e(TAG,"SAX parse compelete");
	}

	public void startDocument() throws SAXException {
		resolver.delete(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI, null, null);
		values = new ContentValues();
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("brand")) {

        	values.put(SYNC_PROVIDER_NAME.NAME,mName);   
        	values.put(SYNC_PROVIDER_NAME.IMAGE,mImage);
			resolver.insert(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI, values);
			mName="";mImage="";
		}
	}
	
	public void characters(char[] p0, int p1, int p2)
	throws SAXException {
        String tag = tags.peek();
        if (tag.equals("brand_id")) {
        	 brand_class_id=Integer.valueOf(new String(p0, p1, p2));
	         values.put(SYNC_PROVIDER_NAME.BRAND_ID,brand_class_id);	
        } else if (tag.equals("name")){
//        	Log.i("asdfasdf",new String(p0, p1, p2));
//        	if(new String(p0,p1,p2).equalsIgnoreCase("&amp;")){
//        		name=name+"&";
//        	}else{
        		mName=mName+new String(p0, p1, p2);
//        	}
        	 	
        } else if (tag.equals("image")){
        	mImage=mImage+new String(p0, p1, p2);
        } 
    }
}

class ProductCategoryListHandler extends DefaultHandler {
    private static final String TAG="OrderListHandler";
	private Stack<String> tags = new Stack<String>();
	private ContentResolver resolver;
	private ContentValues values;
	private int category_class_id=-1;
	private Cursor cursor;
    private String mName="";
//    int count=0;//TODO delete
	public ProductCategoryListHandler(Context context) {
		resolver = context.getContentResolver();
	}
	
	public void endDocument() throws SAXException {
//		System.out.println("SAX parse compelete:"+count);
	}

	public void startDocument() throws SAXException {
		values = new ContentValues();
		resolver.delete(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI, null, null);
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("category")) {
//			cursor= resolver.query(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI,
//					new String[]{SYNC_PROVIDER_NAME.CATEGORY_ID},
//					SYNC_PROVIDER_NAME.CATEGORY_ID+"="+category_class_id,
//					null,null);
			values.put(SYNC_PROVIDER_NAME.NAME, mName);
//			if(cursor.getCount()>0){
//				resolver.update(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI, values,
//						SYNC_PROVIDER_NAME.CATEGORY_ID+"="+category_class_id, null);
//			} else {
				resolver.insert(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI, values);
//			}
//			cursor.close();
			mName="";
		}
	}
	
	public void characters(char[] p0, int p1, int p2)
	throws SAXException {
        String tag = tags.peek();
        
//        System.out.println(tag+":"+new String(p0)+";po.l:"+p0.length+";p2:"+p2);
//	    if(p0.toString().trim().length()==0){
//	    	count++;
//	    }
        if (tag.equals("category_id")) {
        	 category_class_id=Integer.valueOf(new String(p0, p1, p2));
	         values.put(SYNC_PROVIDER_NAME.CATEGORY_ID,category_class_id);	
        } else if (tag.equals("name")){
        	 mName=mName+new String(p0, p1, p2);
        } 
    }
}