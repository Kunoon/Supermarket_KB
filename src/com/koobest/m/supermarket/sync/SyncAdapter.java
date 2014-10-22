package com.koobest.m.supermarket.sync;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;


import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.DefaultHandlerProductsPrice;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.tookits.log.OurLog;

class SyncAdapter extends AbstractThreadedSyncAdapter{
    private static final String TAG = "SyncAdapter";
    private final Context mContext;
    static final String ACTION_SYNC = "com.koobest.action.BROADCAST_ACCOUNT_SYNC";
    private Date mLastUpdated=null;
     
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
    	Log.e(TAG+" authortity:", authority);  
    	if(authority.equalsIgnoreCase(mContext.getString(R.string.authority_orders))){
    		Log.e(TAG," start sync orders profile");  
    		syncOrdersProfile(account, authority);
    	} else if(authority.equalsIgnoreCase(mContext.getString(R.string.authority_price))){
    		Log.e(TAG," start sync product prices profile");  
    		syncProductPricesProfile(account, authority);
    	}      	   
	}    
   
    
    private void syncOrdersProfile(Account account,String authority){
    	OurLog.writeCurrentTimeToFile("start download ordersprofile");
   	
    	try {   
    		Log.e("onPerformSync","sync orderlist start");
    	    final byte[] buffer=NetworkUtilities.downLoadOrderList(account, mContext,mLastUpdated);
    	    if(buffer==null){
    	    	return;
    	    }
    	    
	        Log.e(TAG,String.valueOf(buffer));
	        int customer_id=-1;
	        Cursor cursor=mContext.getContentResolver().query(
	        		SYNC_PROVIDER_NAME.CUSTOMER_CONTENT_URI, 
	        		new String[]{SYNC_PROVIDER_NAME.CUSTOMER_ID}, 
	        		SYNC_PROVIDER_NAME.EMAIL+"=\""+account.name+"\"", null,null);
			if(cursor.moveToFirst()){
				customer_id=cursor.getInt(0);
				cursor.close();
				Log.e(TAG, "customer_id:"+customer_id);
			} else {
				cursor.close();
				return;
			}
			
            if(customer_id!=-1){
            	DefaultHandler handler=DefaultHandlerFactory.createSyncOrderlistHandler(
    					mContext, customer_id);
            	try {
    				handleXmlInSAX(buffer, handler);
    			} catch (ParserConfigurationException e) {
    				e.printStackTrace();
    			} catch (SAXException e) {
    				e.printStackTrace();
    			}	
    			Log.e(TAG, "sync compelete");
            }
			// Intent intent = new Intent(ACTION_SYNC);
			//mContext.sendBroadcast(intent);
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (ResponseException e) {
			e.printStackTrace();
		}    
    }

    
    private void syncProductPricesProfile(Account account,String authority){
    	OurLog.writeCurrentTimeToFile("start update products prices");
    	
//    	deleteProducts();
    	try {   
    		Log.e("onPerformSync","sync orderlist start");
    		Cursor cursor=mContext.getContentResolver().query(
    				PROVIDER_NAME.PRODUCT_CONTENT_URI,
    				new String[]{PROVIDER_NAME.PRODUCT_ID},
    				null,null,null);
    		if(cursor.getCount()==0){
    			cursor.close();
    			return;
    		}
    		StringBuilder product_list=new StringBuilder();
    		int productIds[] = new int[cursor.getCount()];
    		while(cursor.moveToNext()){
    			product_list.append(";"+cursor.getInt(0));
    			productIds[cursor.getPosition()]=cursor.getInt(0);
    			
    		}
    		cursor.close();	
    	    final byte[] buffer=NetworkUtilities.downLoadPriceList(
    	    		account, mContext, product_list.deleteCharAt(0).toString());
    	    if(buffer==null){
    	    	return;
    	    }
    	    
	        Log.e(TAG,new String(buffer));
			DefaultHandler handler=new DefaultHandlerProductsPrice(mContext,productIds);
        	try {
				handleXmlInSAX(buffer, handler);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}	
			Log.e(TAG, "sync compelete");
			// Intent intent = new Intent(ACTION_SYNC);
			//mContext.sendBroadcast(intent);
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (ResponseException e) {
			e.printStackTrace();
		}    
    }
//    /**
//     * used by {syncProductPricesProfile(Account account,String authority)}method,
//     * before start sync some past due product will be deleted
//     */
//    private void deleteProducts(){
////    	int differDate=7;
////    	long differTime=differDate*24*60*60*1000;//millSecord
////    	long currentTime=System.currentTimeMillis();
////    	mContext.getContentResolver().delete(PROVIDER_NAME.PRODUCT_CONTENT_URI,
////    			PROVIDER_NAME.LAST_ACCESS_DATE+"<="+(currentTime-differTime), null);
////    	TODO think clear more
//
//    }
    
    /**
     * used by all the sync method which need to parse xml file with SAX in this class 
     * @param xml_buffer the xml in byte[] need to parse
     * @param handler DefaultHandler or its Child
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static void handleXmlInSAX(byte[] xml_buffer,DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException{
    	InputSource is=new InputSource(new ByteArrayInputStream(xml_buffer));
		SAXParserFactory spf = SAXParserFactory.newInstance(); 
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader=saxParser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.parse(is);
    }
}
