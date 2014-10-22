package com.koobest.m.supermarket.activities.quotehandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.xml.sax.helpers.DefaultHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.QuoteConfirm;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.activities.SupermarketMain;
import com.koobest.m.supermarket.activities.utilities.BaseTabActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.DatabaseUtilities;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.ofcurrency.CurrencyNote;
import com.koobest.m.supermarket.toolkits.ofcurrency.GetCurrencyInformation;

public class EditQuote extends BaseTabActivity{
	private static final String TAG="EditQuote";
	private final static int NEED_LOGIN_DIG=100;
	public int mQuoteID;
	private int mOrderID;
	public CurrencyNote mCurrencyNote;
	private ProductPageHandler mProductPage;
	private SummaryPageHandler mSummmaryPageHandler;
	private boolean isSubmitted=false;
	private Dialog mDialog=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prepare_a_quote_page);
		Bundle data = GetCurrencyInformation.getRateAndSymbol(getApplicationContext());
        mCurrencyNote=new CurrencyNote();
		if(data!=null){
			mCurrencyNote.isLeftSymbol=data.getBoolean(GetCurrencyInformation.IS_LEFT_SYMBOL,true);
			mCurrencyNote.currencySymbol=data.getString(GetCurrencyInformation.SYMBOL);
			mCurrencyNote.exchangeRateTobase=data.getDouble(GetCurrencyInformation.EXCHANGE_RATE_KEY, 1);
		}else{
			mCurrencyNote.currencySymbol=getString(R.string.default_currencysymbol);
		}
	}
	
	@Override
	protected void onStart() {
		mOrderID=getIntent().getIntExtra(NAME.ORDER_ID, -1);
		mQuoteID=getIntent().getIntExtra(NAME.QUOTE_ID, -1);
		
		if(mQuoteID==-1&&mOrderID!=-1){
			Cursor cursor=getContentResolver().query(PROVIDER_NAME.QUOTE_CONTENT_URI,
					new String[]{PROVIDER_NAME.QUOTE_ID},
					PROVIDER_NAME.ORDER_ID+"="+mOrderID, null, null);
			if(cursor.moveToFirst()){
				mQuoteID=cursor.getInt(0);
			}else{
				mOrderID=-1;
			}
			cursor.close();
		}else if(mQuoteID!=-1&&mOrderID==-1){
			Cursor cursor=getContentResolver().query(PROVIDER_NAME.QUOTE_CONTENT_URI,
					new String[]{PROVIDER_NAME.ORDER_ID},
					PROVIDER_NAME.QUOTE_ID+"="+mQuoteID, null, null);
			if(cursor.moveToFirst()){
				mOrderID=cursor.getInt(0);
			}
			cursor.close();
		}
		if(mQuoteID==-1){
			Cursor cursor=getContentResolver().query(PROVIDER_NAME.QUOTE_CONTENT_URI,
					new String[]{PROVIDER_NAME.QUOTE_ID,NAME.ORDER_ID},
					NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, PROVIDER_NAME.QUOTE_ID+" DESC");
			if(cursor.getCount()==0){
				ContentValues values = new ContentValues();
				values.put(PROVIDER_NAME.CUSTOMER_ID,
								Constants.getCustomerId());
				values.put(PROVIDER_NAME.VOLUME, 0);
				values.put(PROVIDER_NAME.WEIGHT, 0);
				values.put(PROVIDER_NAME.BILLING_ADDRESS_ID, -1);
				values.put(PROVIDER_NAME.SHIPPING_ADDRESS_ID, -1);
				values.put(PROVIDER_NAME.CONTAINER_CLASS_ID, 1);
//				values.put(PROVIDER_NAME.PAYMENT_ID, "null");
				values.put(PROVIDER_NAME.ORDER_ID,-1);
				Log.e("custom_id",values.getAsString(PROVIDER_NAME.CUSTOMER_ID));
				//values.put(PROVIDER_NAME.STATUS,"prepare_to_quote");
				getContentResolver().insert(PROVIDER_NAME.QUOTE_CONTENT_URI,values);
				values.clear();
				cursor.requery();
			}
			cursor.moveToFirst();
			mQuoteID=cursor.getInt(0);
			mOrderID=cursor.getInt(1);
			cursor.close();
		}
		
		setContentView(LayoutInflater.from(this).inflate(R.layout.prepare_a_quote_page, 
				null, true));
		if(mOrderID==-1){
			((TextView)findViewById(R.id.quo_edit_tag)).setText(getString(R.string.quo_edit_tag_new));
		}else{
			((TextView)findViewById(R.id.quo_edit_tag)).setText(getString(R.string.quo_edit_tag_up));
		}
		findViewById(android.R.id.tabcontent).setBackgroundDrawable(SupermarketMain.getBackGround(getApplicationContext()));
		final TabHost tabHost = getTabHost();
		getLocalActivityManager().removeAllActivities();
		Log.e(TAG, "start");
	    tabHost.addTab(tabHost.newTabSpec("tab1")
	                .setIndicator((composeLayout(getString(R.string.quo_edit_detail))))
	                .setContent(new Intent(this, ProductPageHandler.class)));
	    if(mOrderID==-1){
	    	tabHost.addTab(tabHost.newTabSpec("tab2")
	                .setIndicator((composeLayout(getString(R.string.e_quote_summary))))
	                .setContent(new Intent(this,SummaryOfCreate.class)));
		}else{
			tabHost.addTab(tabHost.newTabSpec("tab2")
	                .setIndicator((composeLayout(getString(R.string.e_quote_summary))))
	                .setContent(new Intent(this,SummaryOfUpdate.class)));
		}
                
        mProductPage = (ProductPageHandler) getLocalActivityManager().getActivity("tab1");
        mSummmaryPageHandler=(SummaryPageHandler)getLocalActivityManager().getActivity("tab2");
        if(mProductPage==null){
        	throw new IllegalArgumentException();//TODO delete
        }

		
		
		findViewById(R.id.gen_btn_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.gen_btn_main).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SupermarketMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		super.onStart();
	}
	
	@Override
	protected void onTabChange(String tabId) {
		if(tabId.equalsIgnoreCase("tab2")){
  			Log.e("TabTitle",tabId);
  			Log.e("null",""+(mProductPage==null)+":"+(mSummmaryPageHandler==null));
  			if(mSummmaryPageHandler==null){
  				mSummmaryPageHandler=(SummaryPageHandler)getLocalActivityManager().getActivity("tab2");
  			}
  			Log.e("null",""+(mProductPage==null)+":"+(mSummmaryPageHandler==null));
  			mSummmaryPageHandler.setTotalPrice(mProductPage.getTotalPrice());
  		} 
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id){
		case NEED_LOGIN_DIG:
			dialog=new AlertDialog.Builder(this).setMessage("you have not logined,is logining now")
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();					
				}
			}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					try {
						NetworkUtilities.login(EditQuote.this, Constants.getUsername(),true);
					} catch (UnknownHostException e) {
						Toast.makeText(getApplicationContext(), "can't connect to the server.", Toast.LENGTH_SHORT);
						e.printStackTrace();
					} 
					dialog.dismiss();
				}
			}).create();
			dialog.show();
			break;
		}
		return super.onCreateDialog(id);
	}
	
	
	@SuppressWarnings("unchecked")
	private void saveToQuote(){		
		SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
		try{
			if(mProductPage.getProductCount()==0){
				DatabaseUtilities.deleteQuote(database, mQuoteID);
			}else{
				mProductPage.saveToDB(database);
				if(mSummmaryPageHandler!=null){
					mSummmaryPageHandler.saveToDB(database);	
				}
			}
		}finally{
			database.close();
		}	
	}
	
	@Override
	protected void onPause() {
		if(!isSubmitted ){
			saveToQuote();
		}
		super.onPause();
	}
	
	public void submitQuote(){
		if(Constants.getIsLoginNeed()){
			showDialog(NEED_LOGIN_DIG, null);
			return;
		}
		isSubmitted=true;
		List<NameValuePair> responseParams = new ArrayList<NameValuePair>();
		if(!mProductPage.addPostParams(responseParams)){
			Toast.makeText(getBaseContext(), getString(R.string.e_tot_noproductsub), Toast.LENGTH_SHORT).show();
			isSubmitted=false;
			return;
		}
		mSummmaryPageHandler.addSummaryParams(responseParams);
		
		HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(responseParams,"UTF-8");
			new SubmitTask(mQuoteID,entity).execute();
		} catch (UnsupportedEncodingException e) {
			//!!!!!!!!!!!
			e.printStackTrace();
		}
	}
	
	public void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					EditQuote.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	public void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
	private class SubmitTask extends NetworkWithSAXTask<Object, Integer>{
        private final HttpEntity mEntity;
        private int mQuoteId;
        private Bundle responseBundle;
        public SubmitTask(int quoteId,HttpEntity entity) {
			mQuoteId=quoteId;
			mEntity=entity;
		}
    	@Override
    	protected void onPreExecute() {
    		isSubmitted=true;
    		showProgress();
    		responseBundle=new Bundle();
    	}
    	@Override
    	public Context createContext() {
			return EditQuote.this;
		}
		@Override
		public DefaultHandler createHandler(byte[] xml_buffer) {
			return DefaultHandlerFactory.createSimpleQuoteHandler(getBaseContext(),xml_buffer,responseBundle);
		}
		@Override
		public byte[] upOrDownloadFromNet() throws IOException, ResponseException {
			return NetworkUtilities.uploadQuote(mEntity);
		}
		@Override
		protected Bundle doInBackground(Object... params) {
			Bundle result= super.doInBackground(params);
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
				getContentResolver().delete(
						PROVIDER_NAME.QUOTE_CONTENT_URI,
						PROVIDER_NAME.QUOTE_ID + "=" + mQuoteId,
						null);
			}
			return result;
		}
		@Override
    	protected void onPostExecute(Bundle result) {
    		//int resultCode=result.getInt("resultCode",0);
    		hideProgress();
    		if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
    			new RefreshOrderListTask().execute();
    			Intent intent = new Intent();
				intent.setClass(getApplicationContext(),QuoteConfirm.class);
				intent.putExtra(NAME.ORDER_ID, responseBundle.getInt(NAME.ORDER_ID));
				EditQuote.this.startActivity(intent);
				finish();
    		} else{
    			if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY,-1)!=-1){
    				Toast.makeText(getBaseContext(), getString(R.string.e_dig_invalidquote), Toast.LENGTH_LONG).show();
    			}
    		} 		
    	}
    }
	
	private class RefreshOrderListTask extends NetworkWithSAXTask<Object, Integer>{
        @Override
        public Context createContext() {
			return getApplicationContext();
		}

		@Override
		public DefaultHandler createHandler(byte[] xml_buffer) {
			return DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_ORDERLIST_XML, getApplicationContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException, ResponseException {
			return NetworkUtilities.downLoadOrderList();
		}
	}
}
