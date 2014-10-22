package com.koobest.m.supermarket.toolkits.ofcurrency;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class GetCurrencyInformation {
	  private static final String TAG="GetCurrencyInformation";
	  public static final String EXCHANGE_RATE_KEY="rate";
	  public static final String SYMBOL="symbol";
	  public static final String IS_LEFT_SYMBOL="is_left_symbol";
      public static Bundle getRateAndSymbol(Context context){
    	  SharedPreferences preferences = context.getSharedPreferences(
      			context.getString(R.string.config_file_key), Context.MODE_WORLD_READABLE);
    	  int currencyId=preferences.getInt(context.getString(R.string.config_currencyId_key), -1);
    	  Cursor cursor=null;
    	  try{
    		  Log.e(TAG,"currency id:"+currencyId);
          	  if(currencyId!=-1){
          		   cursor = context.getContentResolver().query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
    					new String[]{SYNC_PROVIDER_NAME.VALUE,
    					SYNC_PROVIDER_NAME.SYMBOL_LEFT,SYNC_PROVIDER_NAME.SYMBOL_RIGHT},
    					SYNC_PROVIDER_NAME.CURRENCY_ID+"="+currencyId,
    					null, null);
    			   if(cursor.moveToFirst()){
    			    	Bundle result = new Bundle();
    			    	result.putDouble(EXCHANGE_RATE_KEY, cursor.getDouble(0));
    				    String symbol;
    				    if((symbol=cursor.getString(1))==null){
    					    result.putString(SYMBOL, cursor.getString(2));
    					    result.putBoolean(IS_LEFT_SYMBOL, false);
    				    	return result;
    			     	}
    			    	result.putString(SYMBOL, symbol);
  				        result.putBoolean(IS_LEFT_SYMBOL, true);
  			        	return result;
    		     	}
    		    	cursor.close();
        	   }else{
        			cursor = context.getContentResolver().query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
        					new String[]{SYNC_PROVIDER_NAME.VALUE,
        					SYNC_PROVIDER_NAME.SYMBOL_LEFT,SYNC_PROVIDER_NAME.SYMBOL_RIGHT},
        					SYNC_PROVIDER_NAME.VALUE+"="+1,
        					null, null);
        			if(cursor.moveToFirst()){
        				Bundle result = new Bundle();
        				result.putDouble(EXCHANGE_RATE_KEY, cursor.getDouble(0));
        				String symbol;
        				if((symbol=cursor.getString(1))==null){
        					result.putString(SYMBOL, cursor.getString(2));
        					result.putBoolean(IS_LEFT_SYMBOL, false);
        					return result;
        				}
        				result.putString(SYMBOL, symbol);
      				    result.putBoolean(IS_LEFT_SYMBOL, true);
      				    return result;
        			}
        			cursor.close();
        	  }
          	  Bundle result = new Bundle();
          	  result.putBoolean(IS_LEFT_SYMBOL, true);
          	  result.putString(SYMBOL, context.getString(R.string.default_currencysymbol));
          	  result.putDouble(EXCHANGE_RATE_KEY, 1);
              return result; 
    	  }finally{
    		  if(cursor!=null&&!cursor.isClosed()){
    			  cursor.close();
    		  }
    	  }
    }
      
    private GetCurrencyInformation(){};
}
