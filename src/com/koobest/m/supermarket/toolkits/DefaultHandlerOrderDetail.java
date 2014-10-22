package com.koobest.m.supermarket.toolkits;

import java.util.Stack;

import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.toolkits.transform.currency.CurrencyTransform;
import com.koobest.m.toolkits.transform.exceptions.TransformException;
import com.koobest.m.toolkits.transform.units.UnitTransform;

public class DefaultHandlerOrderDetail extends DefaultHandler {
	private final boolean mAsATemplate;
	// StringBuffer mString;
	String charString = null;
	private String mTag="",mProductName="",mAddress1="",mCity="";
	Stack<String> tags = new Stack<String>();
	ContentResolver resolver;
	ContentValues order_values;
	ContentValues order_product_values;
	ContentValues price_values;
	ContentValues product_values;
	private ContentValues quo_pay_values;
	SQLiteDatabase mDatabase;
	Context mContext;
	// String customer_id;
	int mQuoteId,mOrderId,mProductId,weight_class_id,length_class_id,qty;
	private Double weight,volume=1.0,tVolume=0.0,tWeight=0.0;
    Cursor cursor;
    private int deposit,period;
    /**
     * 
     * @param context
     * @param asATemplate true indicate it is used as a template to create a new quote,
     *                    otherwise it stand for update quote
     */
	public DefaultHandlerOrderDetail(Context context,boolean asATemplate) {
		mContext=context;
		mAsATemplate=asATemplate;
	}

	public void endDocument() throws SAXException {
		Log.e("endDocument", "ok");
		if(mDatabase.isOpen()){
			mDatabase.close();
		}
	}

	public void startDocument() throws SAXException {
		resolver = mContext.getContentResolver();
		mDatabase= new DatabaseHelper(mContext.getApplicationContext()).getWritableDatabase();
		order_values = new ContentValues();
		order_product_values = new ContentValues();
	    price_values = new ContentValues();
		product_values = new ContentValues();
		quo_pay_values = new ContentValues();
	}

	public void startElement(String p0, String p1, String p2, Attributes p3)
			throws SAXException {
		tags.push(p1);
		if(p1.equals("weight_class")){
			mTag=p1;
		} else if(p1.equals("length_class")){
			mTag=p1;
		} else if(p1.equals("qty_class")){
			mTag=p1;
		} else if(p1.equals("container")){
			mTag=p1;
		}
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("order")) {
			order_values.put(PROVIDER_NAME.VOLUME, tVolume);
			order_values.put(PROVIDER_NAME.WEIGHT, tWeight);
			resolver.update(PROVIDER_NAME.QUOTE_CONTENT_URI,
					order_values,PROVIDER_NAME.ORDER_ID+"="+mOrderId,null);
			order_values.clear();
		} else if (p1.equals("product")) {
			order_product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);
			order_product_values.put(PROVIDER_NAME.QUOTE_ID, mQuoteId);
			resolver.insert(PROVIDER_NAME.QUOTE_PRODUCT_CONTENT_URI,order_product_values);
			order_product_values.clear();
			try {
				weight=UnitTransform.weightTransform(mContext, weight, weight_class_id);
				tWeight+=weight*qty;
				volume=UnitTransform.volumeTransform(mContext, volume, length_class_id);
				tVolume+=volume*qty;
				cursor=resolver.query(PROVIDER_NAME.PRODUCT_CONTENT_URI, new String[]{PROVIDER_NAME.PRODUCT_ID},
						PROVIDER_NAME.PRODUCT_ID+"="+mProductId, null, null);
				if(cursor.getCount()==0){
					resolver.insert(PROVIDER_NAME.PRODUCT_PRICE_CONTENT_URI, price_values);
					price_values.clear();
					product_values.put(PROVIDER_NAME.PRODUCT_NAME,mProductName);
					product_values.put(PROVIDER_NAME.WEIGHT,weight);
					product_values.put(PROVIDER_NAME.VOLUME,volume);
					resolver.insert(PROVIDER_NAME.PRODUCT_CONTENT_URI, product_values);
					product_values.clear();						
				}
				mProductName="";
				volume=1.0;
				cursor.close();
			} catch (TransformException e) {
				e.printStackTrace();
				throw new SAXException(e.getMessage());
			}
		} else if(p1.equals("shipping_address")){
			cursor = resolver.query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, 
					new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
					SYNC_PROVIDER_NAME.ADDRESS_1+"=\""+mAddress1+"\" and "+
					SYNC_PROVIDER_NAME.CITY+"=\""+mCity+"\"",
					null, null);
			if(!cursor.moveToFirst()){
				cursor.close();
//				throw new SAXException("no customer infor needed in local");
				order_values.put(NAME.SHIPPING_ADDRESS_ID, 0);
				return;
			}
			order_values.put(NAME.SHIPPING_ADDRESS_ID, cursor.getInt(0));
			cursor.close();
		} else if(p1.equals("payment_address")){
			cursor = resolver.query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI, 
					new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
					SYNC_PROVIDER_NAME.ADDRESS_1+"=\""+mAddress1+"\" and "+
					SYNC_PROVIDER_NAME.CITY+"=\""+mCity+"\"",
					null, null);
			if(!cursor.moveToFirst()){
				cursor.close();
//				throw new SAXException("no customer infor needed in local");
				order_values.put(NAME.BILLING_ADDRESS_ID, 0);
				return;
			}
			order_values.put(NAME.BILLING_ADDRESS_ID, cursor.getInt(0));
			cursor.close();
		} else if(p1.equalsIgnoreCase("payment_term")){
    		Cursor cursor = mDatabase.query(NAME.TABLE_PAYMENT_TERM, 
    				new String[]{NAME.PAYTERM_ID}, NAME.DEPOSIT+"="+deposit+" AND "+NAME.GRACE_PERIOD+"="+period,
    				null, null, null, null);
    		if(cursor.moveToFirst()){
    			quo_pay_values.put(NAME.PAYTERM_ID, cursor.getInt(0));
    		}else{
    			quo_pay_values.put(NAME.PAYTERM_ID, -1);
    		}
    		cursor.close();
    		if(quo_pay_values.getAsString(NAME.PAYMENT_ID)!=null||quo_pay_values.getAsInteger(NAME.PAYTERM_ID)!=-1){
    			quo_pay_values.put(NAME.QUOTE_ID, mQuoteId);
    			mDatabase.insert(NAME.TABLE_QUOTE_TO_PAYMENT, null, quo_pay_values);
    		}
    		quo_pay_values.clear();quo_pay_values=null;
    	}

	}

	public void characters(char[] p0, int p1, int p2) throws SAXException {

		String tag = tags.peek();

		if (tag.equals("product_id")) {
			mProductId = Integer.valueOf(new String(p0, p1, p2));
			order_product_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
			price_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
			product_values.put(PROVIDER_NAME.PRODUCT_ID, mProductId);
		} else if (tag.equals("name")) {
			mProductName=mProductName+new String(p0, p1, p2);
		} else if (tag.equals("quantity")) {
			qty=Integer.valueOf(new String(p0, p1, p2));
			order_product_values.put(PROVIDER_NAME.QTY, qty);
		} else if (tag.equals("price")) {
			Double price;
			try {
				if(p0[0]>'9'||p0[0]<'0'){
					price=Double.valueOf(new String(p0, p1+1, p2-1).replace(",", ""));
					price=CurrencyTransform.transform(mContext, price, p0[0], true);					
				}else{
					price=Double.valueOf(new String(p0, p1, p2-1).replace(",", ""));
					price=CurrencyTransform.transform(mContext, price, p0[p2-1], false);
				}
			} catch (TransformerException e) {
				e.printStackTrace();
				throw new SAXException(e.getMessage());
			}
			price_values.put(PROVIDER_NAME.PRICE, price);
//			order_product_values.put(PROVIDER_NAME.PRICE, price);
		} else if (tag.equals("minimum")) {
			price_values.put(PROVIDER_NAME.QTY, Integer
					.valueOf(new String(p0, p1, p2)));
			product_values.put(PROVIDER_NAME.MINQTY,Integer
					.valueOf(new String(p0, p1, p2)));
		} //else if (tag.equals("minimum")) {
//			product_values.put(PROVIDER_NAME.QTY_UNIT,new String(p0, p1, p2));
		//}
		else if (tag.equals("weight")){
			weight=Double.valueOf(new String(p0, p1, p2));
		} else if (tag.equals("length")){
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
				order_values.put(PROVIDER_NAME.CONTAINER_CLASS_ID,Integer.valueOf(new String(
						p0, p1, p2)));
				mTag="";
			}
		} else if (tag.equals("address_1")) {
			mAddress1=new String(p0, p1, p2);
		} else if (tag.equals("city")){
			mCity=new String(p0, p1, p2);
		} else if (tag.equals("order_id")) {
			if(!mAsATemplate){
				mOrderId=Integer.valueOf(new String(p0, p1, p2));
			}else{
				mOrderId=-1;
			}
			order_values.put(PROVIDER_NAME.ORDER_ID, mOrderId);
			resolver.insert(PROVIDER_NAME.QUOTE_CONTENT_URI,
					order_values);
			cursor = resolver.query(
					PROVIDER_NAME.QUOTE_CONTENT_URI,
					new String[] { PROVIDER_NAME.QUOTE_ID },
					PROVIDER_NAME.ORDER_ID
							+ "="
							+ order_values
									.getAsString(PROVIDER_NAME.ORDER_ID),
					null, null);
			cursor.moveToFirst();
			mQuoteId = cursor.getInt(0);
			cursor.close();
		} else if (tag.equals("customer_id")) {
			order_values.put(PROVIDER_NAME.CUSTOMER_ID, Integer
					.valueOf(new String(p0, p1, p2)));
		} else if (tag.equals("description")) {
			order_values.put(PROVIDER_NAME.DESCRIPTION,new String(p0, p1, p2));
		} else if (tag.equals("payment_method_id")){
    		Cursor cursor = mDatabase.query(NAME.TABLE_PAYMENT, new String[]{NAME.PAYMENT_ID},
    				NAME.PAYMENT_ID+"=\""+new String(p0, p1, p2)+"\"", null, null, null, null);
    		if(cursor.moveToFirst()){
    			quo_pay_values.put(NAME.PAYMENT_ID, new String(p0, p1, p2));
    		}
    		cursor.close();
    	} else if (tag.equals("deposit")){
    		deposit = Integer.valueOf(new String(p0, p1, p2));
    	} else if (tag.equals("grace_period")){
    		period = Integer.valueOf(new String(p0, p1, p2));
    	}
	}
	//used for sync
}
