package com.koobest.m.supermarket.activities.quotehandler;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
/**
 * the different code to SummaryOfCreate has marked with tag:
 * 
 * @author 001
 *
 */
public class SummaryOfUpdate extends SummaryPageHandler{
	
	private static final String TAG="SummaryOfUpdate";
    private int mCustomerID;
    private int mSpinnerLayout = R.layout.my_spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG,"start");
		mCustomerID=Constants.getCustomerId();
        new PrepareData().execute();
	}
	
	public void initAddressViewData(ArrayAdapter<String> addressAdapter,Bundle data){
        if(addressAdapter!=null){
        	sp_bill.setAdapter(addressAdapter);
			int selectIndex;
            if((selectIndex=data.getInt("bill_select_item",0))>=0){
            	sp_bill.setSelection(selectIndex);
            }
			sp_ship.setAdapter(addressAdapter);
			if((selectIndex=data.getInt("ship_select_item",0))>=0){
            	sp_ship.setSelection(selectIndex);
//            	tv_payment.setEnabled(true);
            }
		}
	}
	
	
	public void setTotalPrice(Double price,String symbol ,boolean isLeftSymbol){
		mTotalPrice=price;
		if(isLeftSymbol){
			summary_price.setText(symbol+mCurrencyNote.decimalFormat.format(price));
    		discount_price.setText(symbol+mCurrencyNote.decimalFormat.format(price*mDiscount/100));
    		present_price.setText(symbol+mCurrencyNote.decimalFormat.format(price*(100-mDiscount)/100));
		} else{
			summary_price.setText(mCurrencyNote.decimalFormat.format(price)+symbol);
    		discount_price.setText(mCurrencyNote.decimalFormat.format(price*mDiscount/100)+symbol);
    		present_price.setText(mCurrencyNote.decimalFormat.format(price*(100-mDiscount)/100)+symbol);
		}    		
	}
	
	public void addSummaryParams(List<NameValuePair> responseParams){
        super.addSummaryParams(responseParams);		
		//!--------address----------
		Cursor cursor = getContentResolver().query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI,
				new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
				SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null);
		if(!cursor.moveToPosition(sp_bill.getSelectedItemPosition()-1)){
			cursor.close();
			throw new IllegalArgumentException();
		}
		responseParams.add(new BasicNameValuePair("payment_address_id",String.valueOf(cursor.getInt(0))));
		if(!cursor.moveToPosition(sp_ship.getSelectedItemPosition()-1)){
			cursor.close();
			throw new IllegalArgumentException();
		}
		responseParams.add(new BasicNameValuePair("shipping_address_id",String.valueOf(cursor.getInt(0))));
		cursor.close();
		//-----------------
		//tag:!------------orderId
		cursor = getContentResolver().query(PROVIDER_NAME.QUOTE_CONTENT_URI,
				new String[]{PROVIDER_NAME.ORDER_ID}, 
				PROVIDER_NAME.QUOTE_ID+"="+mQuoteID, null, null);
		if(cursor.moveToFirst()){
			responseParams.add(new BasicNameValuePair("replaced_order_id",String.valueOf(cursor.getInt(0))));
		}
		Log.e(TAG,"quoteId"+cursor.getInt(0));
		cursor.close();
		//------orderId
	}
	
	@Override
	public void saveToDB(SQLiteDatabase database) {
		super.saveToDB(database);
		ContentValues values = new ContentValues();
		values.put(PROVIDER_NAME.DESCRIPTION, et_describe.getText().toString().trim());
		if(sp_bill.getChildCount()>0){
			Cursor cursor = getContentResolver().query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI,
    				new String[]{SYNC_PROVIDER_NAME.ADDRESS_ID}, 
    				SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null);   		
    		if(cursor.moveToPosition(sp_bill.getSelectedItemPosition()-1)){
    			values.put(PROVIDER_NAME.BILLING_ADDRESS_ID, cursor.getInt(0));
    		}
    		if(cursor.moveToPosition(sp_ship.getSelectedItemPosition()-1)){
    			values.put(PROVIDER_NAME.SHIPPING_ADDRESS_ID,cursor.getInt(0));
    		}
    		cursor.close();
		}
		
		getContentResolver().update(PROVIDER_NAME.QUOTE_CONTENT_URI, values,
				PROVIDER_NAME.QUOTE_ID+"="+mQuoteID, null);
	}
	
	private class PrepareData extends AsyncTask<Object, Bundle, Bundle>{
		private ArrayAdapter<String> mAddressAdapter=null;
		
        private String mDescription;
		@Override
		protected Bundle doInBackground(Object... params) {
			SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getReadableDatabase();
	        try{
	        	Cursor cursor = database.query(NAME.TABLE_QUOTES,
						new String[]{PROVIDER_NAME.SHIPPING_ADDRESS_ID,
						NAME.BILLING_ADDRESS_ID,
//						NAME.PAYMENT_ID,
						NAME.DESCRIPTION}, 
//						NAME.CUSTOMER_ID+"="+mCustomerID+" and "+
						NAME.QUOTE_ID+"="+mQuoteID,
						null, null,null,null);
				if(!cursor.moveToFirst()){
					cursor.close();
					return null;
				}
				int shipping_id=cursor.getInt(0),
				    billing_id=cursor.getInt(1);
				mDescription = cursor.getString(2);
				cursor.close();
	        	return initAddressData(database,shipping_id,billing_id);
	        }finally{
	        	database.close();
	        }
		}
		
		private Bundle initAddressData(SQLiteDatabase database,int shipping_id,int billing_id){
			
			Cursor cursor = getContentResolver().query(SYNC_PROVIDER_NAME.ADDRESS_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.CITY,
					SYNC_PROVIDER_NAME.ADDRESS_1,SYNC_PROVIDER_NAME.ADDRESS_ID}, 
					SYNC_PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null);
			String[] addresses=new String[cursor.getCount()+1];
			addresses[0]=getString(R.string.gen_empty_selection);
			if(cursor.getCount()>0){
				for(int j=1;cursor.moveToNext();j++){
					if(shipping_id==cursor.getInt(2)){
						shipping_id=j;
					}
					if(billing_id==cursor.getInt(2)){
						billing_id=j;
					}
					addresses[j] = cursor.getString(1)+","+cursor.getString(0);
				}
			}
			cursor.close();
			mAddressAdapter = new ArrayAdapter<String>(getApplicationContext(),
					mSpinnerLayout, addresses);
			mAddressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Bundle bundle = new Bundle();
			bundle.putInt("ship_select_item", shipping_id<0?0:shipping_id);
			bundle.putInt("bill_select_item", billing_id<0?0:billing_id);
			return bundle;
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			et_describe.setText(mDescription);
			initAddressViewData(mAddressAdapter, result);
		}
	}
}
