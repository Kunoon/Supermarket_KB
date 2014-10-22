package com.koobest.m.syncadapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.sync.network.NetworkUtilities;
import com.koobest.m.sync.toolkits.DefaultHandlerFactory;
import com.koobest.m.tookits.log.OurLog;
import com.koobest.m.toolkits.parsexml.ParseXml;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * AccountStateSyncAdapte implementation for syncing Account State to the
 * platform ContactOperations provider.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";
    private final Context mContext;
    //private static final String ACTION_SYNC = "com.koobest.action.BROADCAST_ACCOUNT_SYNC";
     
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
       //mAccountManager = AccountManager.get(context);
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
    	Log.e(TAG+" authortity:", authority);  
    	if(authority.equalsIgnoreCase(mContext.getString(R.string.authority_customer))){
    		Log.e(TAG," start sync customer profile");  
    		syncCustomerProfile(account, authority);
    	} else if(authority.equalsIgnoreCase(mContext.getString(R.string.authority_currency))){
    		Log.e(TAG," start sync currency profile");  
    		syncCurrencyProfile(account, authority);
    	} else if(authority.equalsIgnoreCase(mContext.getString(R.string.authority_constants))){
    		Log.e(TAG," start sync constants profile");  
    		syncConstantsProfile(account, authority);
    	} 
	}  
    
    private void syncCustomerProfile(Account account,String authority){
    	OurLog.writeCurrentTimeToFile("start download userprofile");
    	try {   Log.e("onPerformSync","sync start");
    	    final byte[] buffer=NetworkUtilities.downLoadCustomer(account, mContext);
	        Log.e(TAG,String.valueOf(buffer));
            if(buffer!=null){
            	DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_CUSTOMERPROFILE_XML,mContext,buffer);
            	try {
            		ParseXml.handleXmlInSAX(buffer, handler);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e){
					e.printStackTrace();
				}
				if(ContentResolver.getIsSyncable(account,
						mContext.getString(R.string.authority_orders))<=0){
					ContentResolver.setIsSyncable(account,
					    mContext.getString(R.string.authority_orders), 1);
					ContentResolver.setSyncAutomatically(account,
						mContext.getString(R.string.authority_orders), true);
				}
            }
			// Intent intent = new Intent(ACTION_SYNC);
			//mContext.sendBroadcast(intent);
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (ResponseException e) {
			e.printStackTrace();
		}    
    }
    
    private void syncCurrencyProfile(Account account,String authority){
    	OurLog.writeCurrentTimeToFile("start download Currency profile");  	
    	try {   Log.e("onPerformSync","sync start");
    	    final byte[] buffer=NetworkUtilities.downLoadCurrency(account, mContext);
	        Log.e(TAG,buffer.toString());
            if(buffer!=null){
            	DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_CURRENCYLIST_XML,mContext);
            	try {
            		ParseXml.handleXmlInSAX(buffer, handler);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
            }
			// Intent intent = new Intent(ACTION_SYNC);
			//mContext.sendBroadcast(intent);
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (ResponseException e) {
			e.printStackTrace();
		}    
    }
    
    private void syncConstantsProfile(Account account,String authority){
    	OurLog.writeCurrentTimeToFile("start download Constants profile");  
    	Log.e("onPerformSync","sync start");
    	syncWeightUnitOfConstants(account);
    	syncLengthUnitOfConstants(account);
    	syncManufacturerOfConstants(account);
    	syncBrandOfConstants(account);
    	syncCategoryOfConstants(account);
    	syncContainerOfConstants(account);
    	
    }
    
    private void syncWeightUnitOfConstants(Account account){
		try {
			byte[] buffer = NetworkUtilities.downLoadWeightUnitsProfile(account, mContext);
			Log.e(TAG,buffer.toString());
	         if(buffer!=null){
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_WEIGHT_UNITLIST_XML,mContext);
	        	 try {
	        		ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	         }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ResponseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    }
    
    private void syncLengthUnitOfConstants(Account account){
    	try {   
    		byte[] buffer=NetworkUtilities.downLoadLengthUnitsProfile(account, mContext);
	        if(buffer!=null){
	        	Log.e(TAG,buffer.toString());
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_LENGHT_UNITLIST_XML,mContext);
	        	 try {
				      ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	        }
    		// Intent intent = new Intent(ACTION_SYNC);
    		//mContext.sendBroadcast(intent);
    	 } catch (IOException e) {
    	     e.printStackTrace();
    	 } catch (ResponseException e) {
    		 e.printStackTrace();
    	 } 
    }
    
    private void syncManufacturerOfConstants(Account account){
    	try {   
    		byte[] buffer=NetworkUtilities.downLoadManufacturerConstants(account, mContext);
	        if(buffer!=null){
	        	Log.e(TAG,buffer.toString());
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_MANUFACTURERLIST_XML,mContext);
	        	 try {
				      ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	        }
    		// Intent intent = new Intent(ACTION_SYNC);
    		//mContext.sendBroadcast(intent);
    	 } catch (IOException e) {
    	     e.printStackTrace();
    	 } catch (ResponseException e) {
    		 e.printStackTrace();
    	 } 
    }
    
    private void syncBrandOfConstants(Account account){
    	try {   
    		byte[] buffer=NetworkUtilities.downLoadBrandConstants(account, mContext);
	        if(buffer!=null){
	        	Log.e(TAG,buffer.toString());
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_BRAND_LIST_XML,mContext);
	        	 try {
				      ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	        }
    		// Intent intent = new Intent(ACTION_SYNC);
    		//mContext.sendBroadcast(intent);
    	 } catch (IOException e) {
    	     e.printStackTrace();
    	 } catch (ResponseException e) {
    		 e.printStackTrace();
    	 } 
    }
    
    private void syncCategoryOfConstants(Account account){
    	try {   
    		byte[] buffer=NetworkUtilities.downLoadCategoryConstants(account, mContext);
	        if(buffer!=null){
	        	Log.e(TAG,buffer.toString());
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_CATEGORYLIST_XML,mContext);
	        	 try {
				      ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	        }
    		// Intent intent = new Intent(ACTION_SYNC);
    		//mContext.sendBroadcast(intent);
    	 } catch (IOException e) {
    	     e.printStackTrace();
    	 } catch (ResponseException e) {
    		 e.printStackTrace();
    	 } 
    }
    
    private void syncContainerOfConstants(Account account){
    	try {   
    		byte[] buffer=NetworkUtilities.downLoadContainerConstants(account, mContext);
	        if(buffer!=null){
	        	 Log.e(TAG,buffer.toString());
	        	 DefaultHandler handler=DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_CONTAINERLIST_XML,mContext);
	        	 try {
				      ParseXml.handleXmlInSAX(buffer, handler);
				 } catch (ParserConfigurationException e) {
					  e.printStackTrace();
				 } catch (SAXException e) {
			          e.printStackTrace();
				 }
	        }
    		// Intent intent = new Intent(ACTION_SYNC);
    		//mContext.sendBroadcast(intent);
    	 } catch (IOException e) {
    	     e.printStackTrace();
    	 } catch (ResponseException e) {
    		 e.printStackTrace();
    	 } 
    }
} 


	
