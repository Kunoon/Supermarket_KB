package com.koobest.m.supermarket.activities.quotehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.toolkits.ofcurrency.CurrencyNote;

public abstract class SummaryPageHandler extends Activity{
//	private static final int DLG_PAYMENT=0; 
	TextView summary_price;
	TextView discount_price;
	TextView present_price;
	EditText et_describe;
	Spinner sp_bill;
	Spinner sp_ship;
	Spinner sp_payment,sp_payterm;
	TextView tv_payment_desc,tv_payterm_desc;
//	Context mContext;
	CurrencyNote mCurrencyNote;
//	String mPaymentMethod;
//	View mPaymentDialogView;
//	int mShipId_to_PayDlg=-1;
	int mQuoteID;
	double mTotalPrice=0,mDiscount=0;
	int mCurrPaymentCheckedPosition=-1, mCurrPayTermCheckedPosition=-1;
//	private boolean Lock=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrencyNote=((EditQuote)getParent()).mCurrencyNote;
		mQuoteID=((EditQuote)getParent()).mQuoteID;
		setContentView(R.layout.prepare_quote_summary);
		initView();
		
//        new PaymentSearchTask().execute();
		new PrepareDateTask().execute();
		/**
		 * the Button of Submit quote on Click Event. use SupermarketSevice to
		 * submit the quote list we want to
		 */
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
//				if(et_describe.getText().toString().trim().length()==0){
//					Toast.makeText(getApplicationContext(), "",Toast.LENGTH_SHORT).show();
//					return;
//				}
				if(sp_bill.getSelectedItemPosition()<=0){
					Toast.makeText(getApplicationContext(), getString(R.string.quo_nobilladdr),Toast.LENGTH_SHORT).show();
					return;
				}
				if(sp_ship.getSelectedItemPosition()<=0){
					Toast.makeText(getApplicationContext(), getString(R.string.quo_noshipaddr),Toast.LENGTH_SHORT).show();
					return;
				}
				if(sp_payment.getSelectedItemPosition()<=0){
					Toast.makeText(getApplicationContext(), getString(R.string.quo_nopayment),Toast.LENGTH_SHORT).show();
					return;
				}
				if(sp_payterm.getSelectedItemPosition()<=0){
					Toast.makeText(getApplicationContext(), getString(R.string.quo_nopayterm),Toast.LENGTH_SHORT).show();
					return;
				}
				v.setEnabled(false);
				((EditQuote)getParent()).submitQuote();
			}
		});
	}
	/**
	 * This method will be called in the construction method 
	 * @param activity
	 */
	private void initView(){
		summary_price=(TextView) findViewById(R.id.summary_price);
		discount_price=(TextView) findViewById(R.id.discount_price);
		present_price=(TextView)findViewById(R.id.present_price);
		et_describe=(EditText)findViewById(R.id.description);
		sp_bill=(Spinner) findViewById(R.id.billing_address);
		sp_ship=(Spinner)findViewById(R.id.shipping_address);
		sp_payment=(Spinner)findViewById(R.id.payment);
		sp_payterm=(Spinner)findViewById(R.id.payterm);
		tv_payment_desc=(TextView)findViewById(R.id.payment_desc);
		tv_payterm_desc=(TextView)findViewById(R.id.payterm_desc);
	}

	
	public void setTotalPrice(Double price){
		mTotalPrice=price;
		if(price==0){
			findViewById(R.id.submit).setEnabled(false);
		}else{
			findViewById(R.id.submit).setEnabled(true);
		}
		dispalyTotalInfor();    		
	}
	
	private void setDiscount(double discount,boolean isInMainThread){
//		Log.e("discount",""+discount);
		mDiscount=discount;
		if(isInMainThread){
			dispalyTotalInfor();
		}else{
			discount_price.post(new Runnable() {
				public void run() {
					dispalyTotalInfor();
				}
			});
		}
	}
	
	private void dispalyTotalInfor(){
		if(mCurrencyNote.isLeftSymbol){
			summary_price.setText(mCurrencyNote.currencySymbol+MyConstants.getFormatedPrice(
					mCurrencyNote.decimalFormat.format(mTotalPrice)));
    		discount_price.setText(mCurrencyNote.currencySymbol+MyConstants.getFormatedPrice(
    				mCurrencyNote.decimalFormat.format(mTotalPrice*mDiscount/100)));
    		present_price.setText(mCurrencyNote.currencySymbol+MyConstants.getFormatedPrice(
    				mCurrencyNote.decimalFormat.format(mTotalPrice*(100-mDiscount)/100)));
		} else{
			summary_price.setText(MyConstants.getFormatedPrice(mCurrencyNote.decimalFormat.format(mTotalPrice))
					+mCurrencyNote.currencySymbol);
    		discount_price.setText(MyConstants.getFormatedPrice(mCurrencyNote.decimalFormat.format(mTotalPrice*mDiscount/100))
    				+mCurrencyNote.currencySymbol);
    		present_price.setText(MyConstants.getFormatedPrice(mCurrencyNote.decimalFormat.format(mTotalPrice*(100-mDiscount)/100))
    				+mCurrencyNote.currencySymbol);
		}
	}
	
	public void saveToDB(SQLiteDatabase database){
		database.delete(NAME.TABLE_QUOTE_TO_PAYMENT, NAME.QUOTE_ID+"="+mQuoteID, null);
		ContentValues values = new ContentValues();
		Map<String, Object> tempItem;
		if(sp_payment.getSelectedItemPosition()>0){
			tempItem=(Map<String, Object>)sp_payment.getSelectedItem();
	        values.put(NAME.PAYMENT_ID, (String)tempItem.get(NAME.PAYMENT_ID));
//	        values.put(NAME.PAYMENT_NAME, (String)tempItem.get(NAME.PAYMENT_NAME));
//	        values.put(NAME.PAYMENT_COMMENT, (String)tempItem.get(NAME.PAYMENT_COMMENT));
		}else{
			values.putNull(NAME.PAYMENT_ID);
		}
		if(sp_payterm.getSelectedItemPosition()>0){
			tempItem=(Map<String, Object>) sp_payterm.getSelectedItem();
	        values.put(NAME.PAYTERM_ID, (Integer)tempItem.get(NAME.PAYTERM_ID));
//	        values.put(NAME.DEPOSIT, (Integer)tempItem.get(NAME.DEPOSIT));
//	        values.put(NAME.GRACE_PERIOD, (Integer)tempItem.get(NAME.GRACE_PERIOD));
//	        values.put(NAME.DISCOUNT, (Double)tempItem.get(NAME.DISCOUNT));
//	        values.put(NAME.DESCRIPTION, (String)tempItem.get(NAME.DESCRIPTION));
		}else{
			values.put(NAME.PAYTERM_ID, -1);
		}
		if(values.get(NAME.PAYMENT_ID) != null||values.getAsInteger(NAME.PAYTERM_ID)!=-1){
			values.put(NAME.QUOTE_ID, mQuoteID);
	        database.insert(NAME.TABLE_QUOTE_TO_PAYMENT, null, values);
		}else{
			database.delete(NAME.TABLE_QUOTE_TO_PAYMENT, NAME.QUOTE_ID+"="+mQuoteID, null);
		}
	}
	
	public void addSummaryParams(List<NameValuePair> responseParams){
		responseParams.add(new BasicNameValuePair("description",et_describe.getText().toString().trim()));
		//!------payment------------
		if(sp_payment.getSelectedItemPosition()<=0){
			throw new IllegalArgumentException();
		}
		if(sp_payterm.getSelectedItemPosition()<=0){
			throw new IllegalArgumentException();
		}
		Map<String, Object> tempItem=(Map<String, Object>)sp_payment.getSelectedItem();
		responseParams.add(new BasicNameValuePair("payment_method_id", (String)tempItem.get(NAME.PAYMENT_ID)));
		tempItem=(Map<String, Object>) sp_payterm.getSelectedItem();
		responseParams.add(new BasicNameValuePair("payment_term_id", String.valueOf((Integer)tempItem.get(NAME.PAYTERM_ID))));		
		//---------------
	}
	
	private class PrepareDateTask extends AsyncTask<Object, Object, Object>{
		private int mPaymentSelectItem=0,mPayTermSelectItem=0;
		private SimpleAdapter mPaymentAdapter,mPayTermAdapter;
		private int mPayTermId=-1;
		private String mPaymentId=null;
		@Override
		protected Object doInBackground(Object... params) {
			SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getReadableDatabase();
	        try{
	        	Cursor cursor = database.query(NAME.TABLE_QUOTE_TO_PAYMENT,
						new String[]{NAME.PAYMENT_ID,//payment method
	        			NAME.PAYTERM_ID}, //payment term
						NAME.QUOTE_ID+"="+mQuoteID, null, null,null,null);
	    		if(cursor.moveToFirst()){
	    			mPaymentId=cursor.getString(0);
	    			mPayTermId=cursor.getInt(1);
	    		}
	    		cursor.close();
	    		//getDiscount
	    		if(mPayTermId!=-1){
	    			cursor = database.query(NAME.TABLE_PAYMENT_TERM,
							new String[]{NAME.DISCOUNT}, //payment term
							NAME.PAYTERM_ID+"="+mPayTermId, null, null,null,null);
		    		if(cursor.moveToFirst()){
		    			setDiscount(cursor.getDouble(0), false);
		    		}
		    		cursor.close();
	    		}
	    		//get payment list
	    		{
	    			cursor = database.query(NAME.TABLE_PAYMENT,
							new String[]{NAME.PAYMENT_ID,NAME.PAYMENT_NAME,NAME.PAYMENT_COMMENT}, //payment term
							null, null, null,null,null);
		    		List<Map<String, String>> paymentList=new ArrayList<Map<String,String>>();;
		    		Map<String, String> item=new HashMap<String, String>(5);
					item.put(NAME.PAYMENT_NAME, getString(R.string.gen_empty_selection));
					paymentList.add(item);
		    		while(cursor.moveToNext()){
		    			if(mPaymentId!=null&&mPaymentId.equalsIgnoreCase(cursor.getString(0))){
		    				mPaymentSelectItem = cursor.getPosition()+1;
		    			}
		    			item = new HashMap<String, String>(5);
		    			item.put(NAME.PAYMENT_ID, cursor.getString(0));
		    			item.put(NAME.PAYMENT_NAME, cursor.getString(1));
		    			item.put(NAME.PAYMENT_COMMENT, cursor.getString(2));
		    			paymentList.add(item);
		    		}
		    		cursor.close();
		    		mPaymentAdapter = new SimpleAdapter(getApplicationContext(), paymentList,
							R.layout.my_spinner, 
							new String[]{NAME.PAYMENT_NAME}, new int[]{android.R.id.text1});
					mPaymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	    		}
	    		//get payterm list
	    		{
	    			cursor = database.query(NAME.TABLE_PAYMENT_TERM,
							new String[]{NAME.PAYTERM_ID,NAME.DEPOSIT,NAME.GRACE_PERIOD,NAME.DISCOUNT,NAME.DESCRIPTION}, //payment term
							null, null, null,null,null);
		    		List<Map<String, Object>> payTermList=new ArrayList<Map<String,Object>>();;
		    		Map<String, Object> item=new HashMap<String, Object>(7);
					item.put(NAME.DESCRIPTION, getString(R.string.gen_empty_selection));
					payTermList.add(item);
		    		while(cursor.moveToNext()){
		    			if(mPayTermId==cursor.getInt(0)){
		    				mPayTermSelectItem = cursor.getPosition()+1;
		    			}
		    			item = new HashMap<String, Object>(5);
		    			item.put(NAME.PAYTERM_ID, cursor.getInt(0));
		    			item.put(NAME.DEPOSIT, cursor.getInt(1));
		    			item.put(NAME.GRACE_PERIOD, cursor.getInt(2));
		    			item.put(NAME.DISCOUNT, cursor.getDouble(3));
		    			item.put(NAME.DESCRIPTION, cursor.getString(4));
		    			payTermList.add(item);
		    		}
		    		cursor.close();
		    		mPayTermAdapter = new SimpleAdapter(getApplicationContext(), payTermList,
							R.layout.my_spinner, 
							new String[]{NAME.DESCRIPTION}, new int[]{android.R.id.text1});
					
					mPayTermAdapter.setDropDownViewResource(R.layout.payterm_dropdown_item); 
	    		}
	        }finally{
	        	database.close();
	        }
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			sp_payment.setAdapter(mPaymentAdapter);
			sp_payment.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Map<String, Object> item = (Map<String, Object>) sp_payment.getItemAtPosition(arg2);
					tv_payment_desc.setText(item.get(NAME.PAYMENT_COMMENT)!=null?Html.fromHtml((String)item.get(NAME.PAYMENT_COMMENT)):null);
					if (tv_payment_desc.getText().toString().trim().length()>0) {
						tv_payment_desc.setVisibility(View.VISIBLE);
					}else{
						tv_payment_desc.setVisibility(View.GONE);
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			sp_payment.setSelection(mPaymentSelectItem);
			sp_payment.setClickable(true);
			
			
			sp_payterm.setAdapter(mPayTermAdapter);
			sp_payterm.setOnItemSelectedListener(new OnItemSelectedListener() {
//                private String temp=getString(R.string.)
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(arg2==0){
						setDiscount(0, true);
//						dispalyTotalInfor();
						tv_payterm_desc.setText(null);
						tv_payterm_desc.setVisibility(View.GONE);
						return;
					}
					Map<String, Object> currentItem = (Map<String, Object>) sp_payterm.getItemAtPosition(arg2);
//					((TextView)sp_payterm.findViewById(android.R.id.text1)).
//					        setText(getString(R.string.quo_deposit)+currentItem.get(NAME.DEPOSIT)+"%;"+
//					        		getString(R.string.quo_period)+currentItem.get(NAME.GRACE_PERIOD));
					tv_payterm_desc.setText((String)currentItem.get(NAME.DESCRIPTION));
					if (tv_payterm_desc.getText().toString().trim().length()>0) {
						tv_payterm_desc.setVisibility(View.VISIBLE);
					}else{
						tv_payterm_desc.setVisibility(View.GONE);
					}
//					mDiscount=(Double) currentItem.get(NAME.DISCOUNT);
					setDiscount((Double) currentItem.get(NAME.DISCOUNT), true);
//					dispalyTotalInfor();
				}

				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			sp_payterm.setSelection(mPayTermSelectItem);
			sp_payterm.setClickable(true);
		}
		
	}
		
//	private class PaymentSearchTask extends NetworkWithSAXTask<Object, Object>{
//		private List<Map<String, Object>> mPaymentResponse;
//		private String mCurrentPaymentID;
//        private int mCurrentPayTermID;
//		private int mCurrentPaymentSelectPosition=0;
//		@Override
//		protected void onPreExecute() {
//			mPaymentResponse=new ArrayList<Map<String,Object>>();
//		}
//		
//		@Override
//		public Context createContext() {
//			return SummaryPageHandler.this;
//		}
//
//		@Override
//		protected DefaultHandler createHandler(byte[] xmlBuffer) {
//			publishProgress();
//			Map<String, Object> balckitem=new HashMap<String, Object>(3);
//			balckitem.put(NAME.PAYMENT_NAME, getString(R.string.gen_empty_selection));
//			mPaymentResponse.add(balckitem);
//			return new DefaultHandlerPayment( mPaymentResponse,mCurrentPaymentID,new CallBack() {
//				
//				public void notifyCurrentSelectPosition(int positon) {
//					mCurrentPaymentSelectPosition=positon;
//				}
//			});
//		}
//
//		@Override
//		protected byte[] upOrDownloadFromNet() throws IOException,
//				ResponseException {
//			return NetworkUtilities.downLoadPayment();
//		}
//		
//		@Override
//		protected Bundle doInBackground(Object... params) {
//			SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getReadableDatabase();
//	        try{
//	        	Cursor cursor = database.query(NAME.TABLE_QUOTE_TO_PAYMENT,
//						new String[]{NAME.PAYMENT_ID,//payment method
//	        			NAME.PAYTERM_ID,NAME.DISCOUNT}, //payment term
//						NAME.QUOTE_ID+"="+mQuoteID, null, null,null,null);
//	    		if(cursor.moveToFirst()){
//	    			mCurrentPaymentID=cursor.getString(0);
//	    			mCurrentPayTermID=cursor.getInt(1);
//	    			mDiscount=cursor.getDouble(2);
//	    		}
//	    		cursor.close();
//	        }finally{
//	        	database.close();
//	        }
//			return super.doInBackground(params);
//		}
//		
//		@Override
//		protected void onProgressUpdate(Object... values) {
//			new PaymentTermDownTask(mCurrentPayTermID).execute();					
//		}
//		
//		@Override
//		protected void onPostExecute(Bundle result) {
//			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), mPaymentResponse,
//					R.layout.my_spinner, 
//					new String[]{NAME.PAYMENT_NAME}, new int[]{android.R.id.text1});
//			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
//			sp_payment.setAdapter(adapter);
//			sp_payment.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//				public void onItemSelected(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					Map<String, Object> item = (Map<String, Object>) sp_payment.getItemAtPosition(arg2);
//					tv_payment_desc.setText((String)item.get(NAME.PAYMENT_COMMENT));
//					if (tv_payment_desc.getText().toString().trim().length()>0) {
//						tv_payment_desc.setVisibility(View.VISIBLE);
//					}else{
//						tv_payment_desc.setVisibility(View.GONE);
//					}
//				}
//
//				public void onNothingSelected(AdapterView<?> arg0) {
//				}
//			});
//			sp_payment.setSelection(mCurrentPaymentSelectPosition);
//			sp_payment.setClickable(true);
//		}
//	}
//	
//	private class PaymentTermDownTask extends NetworkWithSAXTask<Object, Object>{
//		private List<Map<String, Object>> mPayTermResponse;
//		private int mCurrentPayTermID;
//		private int mCurrentSelectPosition=0;
//		
//		public PaymentTermDownTask(int currentPayTermID) {
//			mCurrentPayTermID=currentPayTermID;
//		}
//		@Override
//		protected void onPreExecute() {
//			mPayTermResponse=new ArrayList<Map<String,Object>>();
//		}
//		@Override
//		public Context createContext() {
//			return SummaryPageHandler.this;
//		}
//
//		@Override
//		protected DefaultHandler createHandler(byte[] xmlBuffer) {
//			Map<String, Object> balckitem=new HashMap<String, Object>(3);
//			balckitem.put(NAME.DESCRIPTION, getString(R.string.gen_empty_selection));
//			mPayTermResponse.add(balckitem);
//			return new DefaultHandlerPayTerm(mPayTermResponse,mCurrentPayTermID,new DefaultHandlerPayTerm.CallBack() {
//				
//				public void notifyCurrentSelectPosition(int positon) {
//					mCurrentSelectPosition=positon;
//				}
//			});
//		}
//
//		@Override
//		protected byte[] upOrDownloadFromNet() throws IOException,
//				ResponseException {
//			return NetworkUtilities.downLoadPayTerm();
//		}
//		
//		@Override
//		protected void onPostExecute(Bundle result) {
//			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), mPayTermResponse,
//					R.layout.my_spinner, 
//					new String[]{NAME.DESCRIPTION}, new int[]{android.R.id.text1});
//			
//			adapter.setDropDownViewResource(R.layout.payterm_dropdown_item); 
//			sp_payterm.setAdapter(adapter);
//			sp_payterm.setOnItemSelectedListener(new OnItemSelectedListener() {
////                private String temp=getString(R.string.)
//				public void onItemSelected(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//					if(arg2==0){
//						mDiscount=0;
//						dispalyTotalInfor();
//						return;
//					}
//					Map<String, Object> currentItem = (Map<String, Object>) sp_payterm.getItemAtPosition(arg2);
//					((TextView)sp_payterm.findViewById(android.R.id.text1)).
//					        setText(getString(R.string.quo_deposit)+currentItem.get(NAME.DEPOSIT)+"%;"+
//					        		getString(R.string.quo_period)+currentItem.get(NAME.GRACE_PERIOD));
//					tv_payterm_desc.setText((String)currentItem.get(NAME.DESCRIPTION));
//					mDiscount=(Double) currentItem.get(NAME.DISCOUNT);
//					dispalyTotalInfor();
//				}
//
//				public void onNothingSelected(AdapterView<?> arg0) {
//				}
//			});
//			sp_payterm.setSelection(mCurrentSelectPosition);
//			sp_payterm.setClickable(true);
//		}
//	}
}
