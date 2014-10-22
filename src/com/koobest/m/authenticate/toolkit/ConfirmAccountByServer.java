package com.koobest.m.authenticate.toolkit;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.koobest.m.authenticate.activities.AuthenticatorActivity;
import com.koobest.m.network.toolkits.CommonNetworkUtilities;
import com.koobest.m.network.toolkits.HttpClientFactory;
import com.koobest.m.network.toolkits.NetWork;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.Loading;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.DatabaseUtilities;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.toolkits.regex.xmlparse.RegexParse;
import com.koobest.m.sync.toolkits.DefaultHandlerFactory;
import com.koobest.m.toolkits.parsexml.ParseXml;

public class ConfirmAccountByServer extends NetWork{
    private static final String TAG="ConfirmAccountByServer";
    private static final String CUSTOMER_DOWNLOAD_URI =CommonNetworkUtilities.HOST+"?route=xkb/m/account/profile";
    
//	public static boolean confirm(Context context,String username, 
//			String authToken,String currency) 
//	        throws UnknownHostException,IOException, ResponseException {
//		return confirm(context,username,authToken,currency);
//	}
	
	public static boolean confirm(Context context,String username, 
			String authToken)
	        throws UnknownHostException, IOException, ResponseException{
		SharedPreferences prefs = context.getSharedPreferences(
				context.getString(R.string.config_file_key), Context.MODE_WORLD_READABLE);
		String currency = prefs.getString(
				context.getString(R.string.config_currencyCode_key),"");
		final String language = Locale.getDefault().getLanguage()+"_"+Locale.getDefault().getCountry();
		Log.e(TAG+"url", String.format(
				mAuthenticateAccountURL,
				username, authToken,language,currency));
		HttpGet request = new HttpGet(String.format(
				mAuthenticateAccountURL,
				username, authToken,language,currency));
		setHttpClientToNull();
		maybeCreateHttpClient();
		if(mHttpClient==null){
			 mHttpClient = HttpClientFactory.createHttpClient();	
		}
		try {
			mHttpClient.getParams().setBooleanParameter(
					ClientPNames.HANDLE_REDIRECTS, false);
			mHttpClient.getConnectionManager().closeIdleConnections(6000,
					TimeUnit.MILLISECONDS);
			HttpResponse response = null;
			response = mHttpClient.execute(request);
			
			if (response != null) {
				Log.e(TAG+"statucode", String.valueOf(response.getStatusLine()
						.getStatusCode()));
				if (response.getStatusLine().getStatusCode() != 302){
					ResponseException exception=new ResponseException();
			    	exception.setResponseCode(response.getStatusLine().getStatusCode());
				    throw exception;
				}
				if(context!=null&&(context instanceof Loading||context instanceof AuthenticatorActivity)){//!hasCustomer(context)){
					return initData(context);
				}
				//used to confirm the customer profile has download before. if it is not download now
//				else if(context!=null){
//					if(!getCustomerId(context,username)){
						
//					}
//				}
				return true;	
			}
		} finally {
			mHttpClient.getParams().setBooleanParameter(
					ClientPNames.HANDLE_REDIRECTS, true);
		}
		return false;
	}
	
	private static boolean initData(Context context) throws IOException, ResponseException {
		//load customer profile
		{
			if(context instanceof Loading){
				((Loading) context).notifyStartLoadCustomer();
			}else if(context instanceof AuthenticatorActivity){
				((AuthenticatorActivity) context).notifyStartLoadCustomer();
			}
			byte[] buffer=NetworkUtilities.downloadCustomerProfile();
			//!!!borrow default handler factory from Sync part
			if(buffer==null||!handleXmlInSAX(buffer,DefaultHandlerFactory.createHandler(
					DefaultHandlerFactory.PARSE_CUSTOMERPROFILE_XML,context,buffer))){
				return false;
			}
		}
		//load currency and payment method
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//			Log.e("UpdateDay",prefs.getString("UpdateDay","2001-01-01"));
			if(prefs.getLong("UpdateDay", 0)==0){
				updateDayDistanceData(context,true);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putLong("UpdateDay", System.currentTimeMillis());
				editor.commit();
			}else{
				Calendar preDown=Calendar.getInstance();
				preDown.setTimeInMillis(prefs.getLong("UpdateDay",0));
				Calendar now = Calendar.getInstance();
				if(preDown.get(Calendar.YEAR)<now.get(Calendar.YEAR)||
						preDown.get(Calendar.MONTH)<now.get(Calendar.MONTH)||
						preDown.get(Calendar.DAY_OF_MONTH)<now.get(Calendar.DAY_OF_MONTH)){
//					new RefreshCurrencyTask(context.getApplicationContext()).execute();
					Log.e("Note","start down load currency in thread!");
					final Context localContext = context.getApplicationContext();
					new Thread(new Runnable() {
						public void run() {
							try {
								updateDayDistanceData(localContext, false);
								SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(localContext).edit();
								editor.putLong("UpdateDay", System.currentTimeMillis());
								editor.commit();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ResponseException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		}
		return true;
	}

//	private static boolean getCustomerId(Context context,String username){
//		Cursor cursor=context.getContentResolver().query(
//				SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, 
//				new String[]{SYNC_PROVIDER_NAME.CUSTOMER_ID}, 
//				SYNC_PROVIDER_NAME.EMAIL+"=\""+username+"\"", null,null);
//		if(cursor.moveToFirst()){
//			cursor.close();
//			return true;
//		}
//		cursor.close();	
//		return false;
//	}
	
	private static boolean handleXmlInSAX(byte[] xml_buffer,DefaultHandler handler) {
		try {
			ParseXml.handleXmlInSAX(xml_buffer, handler);
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
	
	private static void updateDayDistanceData(Context context,boolean needNotify) throws IOException, ResponseException{
		byte[] buffer ;
		//update currency
		{
			if(needNotify){
				if(context instanceof Loading){
					((Loading) context).notifyStartLoadCurrency();
				}else if(context instanceof AuthenticatorActivity){
					((AuthenticatorActivity) context).notifyStartLoadCurrency();
				}
			}
			buffer = NetworkUtilities.downloadCurrencyList();
			handleXmlInSAX(buffer,com.koobest.m.sync.toolkits.DefaultHandlerFactory.createHandler(
					com.koobest.m.sync.toolkits.DefaultHandlerFactory.PARSE_CURRENCYLIST_XML,context.getApplicationContext()));	
		}
		
		SQLiteDatabase db = new DatabaseHelper(context.getApplicationContext()).getWritableDatabase();
		try{
			//download payment method
			{
				buffer=null;
				if(needNotify){
					if(context instanceof Loading){
						((Loading) context).notifyStartLoadPayment();
					}else if(context instanceof AuthenticatorActivity){
						((AuthenticatorActivity) context).notifyStartLoadPayment();
					}
				}
				buffer = NetworkUtilities.downLoadPayment();
				RegexParse.parsePaymentList(db, new String(buffer));
			}
			
			//download payment term
			{
				buffer=null;
				if(needNotify){
					if(context instanceof Loading){
						((Loading) context).notifyStartLoadPayterm();
					}else if(context instanceof AuthenticatorActivity){
						((AuthenticatorActivity) context).notifyStartLoadPayterm();
					}
				}
				buffer = NetworkUtilities.downLoadPayTerm();
				RegexParse.parsePaymentTermList(db, new String(buffer));
			}
			
			//delete out of date product
			//TODO: Note product with no xml has not been solve at this
			/**
			 * Delete out of date product.The valid period is "5".
			 * Note: product with no xml{when a order parse to quote will have no xml infor}
			 *       has not been solve at this
			 */
			{
				
				Cursor cursor = db.query(NAME.TABLE_PRODUCT, new String[]{NAME.PRODUCT_ID},
						NAME.LAST_ACCESS_DATE+"<="+(System.currentTimeMillis()-5*24*60*60*1000), null, null, null, null);
				Cursor tempCursor;
				while(cursor.moveToNext()){
					tempCursor=db.query(NAME.TABLE_QUOTE_PRODUCT, new String[]{NAME.PRODUCT_ID},
							NAME.PRODUCT_ID+"="+cursor.getInt(0), null, null, null, null);
					if(tempCursor.getCount()<=0){
						DatabaseUtilities.deleteProduct(db, String.valueOf(cursor.getInt(0)));
					}
					tempCursor.close();
				}
				cursor.close();
			}
			
		}finally{
			db.close();
		}
	}
}
