package com.koobest.m.supermarket.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.koobest.m.supermarket.activities.setting.Setting;
import com.koobest.m.supermarket.application.MarketApplication;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.sync.toolkits.DefaultHandlerFactory;
import com.koobest.m.toolkits.parsexml.ParseXml;

import dalvik.system.VMRuntime;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Loading extends Activity{
	private final static String TAG="Loading";
	private Handler mHandler;
	private Bitmap mBackGround;
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	((MarketApplication)getApplication()).hasLoding=true;
    	//!---delete---------
    	Runtime time=Runtime.getRuntime();	
		Log.e("Memory","MEMORY::Max:"+time.maxMemory()+";Total:"+time.totalMemory()+";Free:"+time.freeMemory());
        Log.e("Memory", "VM_HEAP::heap:"+VMRuntime.getRuntime().getMinimumHeapSize()+";"+";Utilization:"+VMRuntime.getRuntime().getTargetHeapUtilization()+";Alloc:"+VMRuntime.getRuntime().getExternalBytesAllocated());
        //----------------
    	setContentView(R.layout.loading_page);
    	try{
    		mBackGround=BitmapFactory.decodeResource(getResources(), R.drawable.loding_bg, null);
    	}catch (OutOfMemoryError e) {
    		BitmapFactory.Options options=new BitmapFactory.Options();
        	options.inSampleSize=2;
        	mBackGround=BitmapFactory.decodeResource(getResources(), R.drawable.loding_bg, options);
		}
    	findViewById(R.id.loading).setBackgroundDrawable(new BitmapDrawable(mBackGround));
    	mHandler = new Handler();
    	new LoadingTask(getApplicationContext(), 
    			(ProgressBar)findViewById(R.id.load_progress)).execute();
    	
    }
    
    @Override
    protected void onDestroy() {
    	mBackGround.recycle();
    	super.onDestroy();
    }
	
	private boolean setCustomerId(final String username){
		Cursor cursor=getContentResolver().query(SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, 
				new String[]{SYNC_PROVIDER_NAME.CUSTOMER_ID}, 
				SYNC_PROVIDER_NAME.EMAIL+"=\""+username+"\"", null,null);
		try{
			if(!cursor.moveToFirst()){
				return false;
			}
			Constants.setCustomerId(cursor.getInt(0));
			return true;
		}finally{
			cursor.close();	
		}
	}
	
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
//			android.os.Process.killProcess(android.os.Process.myPid());
//			finish();//modified on July 26th by Liu Jian
//		}
//		return false;
//	}

	private class LoadingTask extends AsyncTask<Object, Integer, Intent>{
		private ProgressBar mProgressBar;
		private Context mContext;
		
		public LoadingTask(Context context,ProgressBar bar) {
			mContext = context;
			mProgressBar = bar;
		}

		@Override
		protected Intent doInBackground(Object... arg0) {
//            deletePaymentOfQuote();
			SharedPreferences preference=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			if(!preference.getBoolean("hasBeenInstall", false)){
	    		installUnitsFile();
	    		String language = Locale.getDefault().getLanguage();
	    		installProductCategory(language);
	    		installManufacturer(language);
	    		installBrand(language);
	    		installContainer(language);
	    		Setting.setProductCachePeriod(getBaseContext(), 7);
	    		SharedPreferences.Editor editor = preference.edit();
	    		initSettingDate(editor);
	    		editor.putBoolean("hasBeenInstall",true);
	    		editor.commit();
	    	}else{
	    		publishProgress(20);
	    	}
			
			final AccountManager am=AccountManager.get(mContext);
	        final Account[] accounts = am.getAccountsByType(getString(R.string.AccountType));
	        Log.e(TAG,"accounts size"+accounts.length);
	        if(accounts.length>0){
	        	if(setCustomerId(accounts[0].name)){
					publishProgress(10);
					try {
						if(NetworkUtilities.login(Loading.this, accounts[0].name,false)){
//	TODO reuse						ContentResolver.requestSync(accounts[0],
//								    getString(R.string.authority_customer), new Bundle());
							Constants.setUsername(accounts[0].name);
							Intent intent = new Intent();
				    		intent.putExtra(SupermarketMain.IS_LOGIN_NEED, false);
							return intent;
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
	            }
	        }
	        Intent intent = new Intent();
    		intent.putExtra(SupermarketMain.IS_LOGIN_NEED, true);
			return intent;
		}
		
//		private void deletePaymentOfQuote() {
//	        SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
//	        try{
//	        	database.delete(NAME.TABLE_QUOTE_TO_PAYMENT, null, null);
//	        }finally{
//	        	database.close();
//	        }
//		}

		private void initSettingDate(Editor editor) {
			editor.putString(getString(R.string.cache_pro_detail), getString(R.string.default_cache_prod));
			editor.putString(getString(R.string.period_currency), getString(R.string.default_period_curr));
			editor.putString(getString(R.string.period_orderlist), getString(R.string.default_period_order));
			editor.commit();
		}

		private void installManufacturer(String language) {
			try {
				InputStream is = null;
				if(language.equalsIgnoreCase("zh")){
					is=getAssets().open("zh_manufacturer.xml");
				} else{
					is=getAssets().open("manufacturer.xml");
				}
				if(is==null)return;
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				DefaultHandler handler = DefaultHandlerFactory.createHandler(
						DefaultHandlerFactory.PARSE_MANUFACTURERLIST_XML,mContext);
				ParseXml.handleXmlInSAX(buffer, handler);
				publishProgress(5);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void installBrand(String language) {
			try {
				InputStream is = null;
				if(language.equalsIgnoreCase("zh")){
					is=getAssets().open("zh_brand.xml");
				} else{
					is=getAssets().open("brand.xml");
				}
				if(is==null)return;
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				DefaultHandler handler = DefaultHandlerFactory.createHandler(
						DefaultHandlerFactory.PARSE_BRAND_LIST_XML,mContext);
				ParseXml.handleXmlInSAX(buffer, handler);
				publishProgress(5);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private boolean installProductCategory(String language) {
			try {
				InputStream is = null;
				if(language.equalsIgnoreCase("zh")){
					is=getAssets().open("zh_product_category.xml");
				} else{
					is=getAssets().open("product_category.xml");
				}
				if(is==null)return false;
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				DefaultHandler handler = DefaultHandlerFactory.createHandler(
						DefaultHandlerFactory.PARSE_CATEGORYLIST_XML,mContext);
				ParseXml.handleXmlInSAX(buffer, handler);
				publishProgress(5);
				return true;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		private void installContainer(String language) {
			try {
				InputStream is = null;
				if(language.equalsIgnoreCase("zh")){
					is=getAssets().open("zh_container.xml");
				} else{
					is=getAssets().open("container.xml");
				}
				InputStream is_length = is;
				byte[] buffer = new byte[is_length.available()];
				is_length.read(buffer);
				DefaultHandler handler = DefaultHandlerFactory.createHandler(
						DefaultHandlerFactory.PARSE_CONTAINERLIST_XML,mContext);
				ParseXml.handleXmlInSAX(buffer, handler);
				publishProgress(5);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void installUnitsFile() {
			try {
				byte[] buffer; 
				
				try {
					InputStream is_weight = getAssets().open("weight_units.xml");
					buffer = new byte[is_weight.available()];
					is_weight.read(buffer);
					DefaultHandler handler = DefaultHandlerFactory.createHandler(
							DefaultHandlerFactory.PARSE_WEIGHT_UNITLIST_XML,mContext);
					ParseXml.handleXmlInSAX(buffer, handler);
					publishProgress(5);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}	
				
				try {
					InputStream is_length = getAssets().open("length_units.xml");
					buffer = new byte[is_length.available()];
					is_length.read(buffer);
					DefaultHandler handler = DefaultHandlerFactory.createHandler(
							DefaultHandlerFactory.PARSE_LENGHT_UNITLIST_XML,mContext);
					ParseXml.handleXmlInSAX(buffer, handler);
					publishProgress(5);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
				
							
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(mProgressBar.getProgress()+values[0]);
		}

		protected void onPostExecute(Intent result) {
			result.setClass(Loading.this, SupermarketMain.class);
			startActivity(result);
			finish();
		}
	}
	
	public void notifyStartLoadCustomer(){
		mHandler.post(new Runnable() {
			
			public void run() {
				((TextView)findViewById(R.id.load_statu)).setText(getString(R.string.load_statu_customer));
			}
		});
	}
	
	public void notifyStartLoadCurrency() {
		mHandler.post(new Runnable() {
			
			public void run() {
				((TextView)findViewById(R.id.load_statu)).setText(getString(R.string.load_statu_currency));
			}
		});
	}

	public void notifyStartLoadPayment() {
		mHandler.post(new Runnable() {
			
			public void run() {
				((TextView)findViewById(R.id.load_statu)).setText(getString(R.string.load_statu_payment));
			}
		});
	}

	public void notifyStartLoadPayterm() {
		mHandler.post(new Runnable() {
			
			public void run() {
				((TextView)findViewById(R.id.load_statu)).setText(getString(R.string.load_statu_pay_term));
			}
		});
	}
}
