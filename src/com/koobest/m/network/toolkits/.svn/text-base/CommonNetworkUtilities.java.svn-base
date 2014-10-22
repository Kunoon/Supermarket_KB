package com.koobest.m.network.toolkits;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.koobest.m.authenticate.constant.AuthenticatorConstants;
import com.koobest.m.authenticate.toolkit.ConfirmAccountByServer;
import com.koobest.m.supermarket.activities.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class CommonNetworkUtilities extends NetWork{
	private static final String TAG="CommonNetworkUtilities";
	protected static final String CUSTOMER_DOWNLOAD_URI = HOST+"?route=xkb/m/account/profile";
	protected static final String WEIGHTUNITS_DOWNLOAD_URI = HOST+"?route=xkb/m/common/localization/getweightclasses";
	protected static final String LENGTHUNITS_DOWNLOAD_URI = HOST+"?route=xkb/m/common/localization/getlengthclasses";
	
	public static HttpClient getHttpClient(){
    	return mHttpClient;
    }
    
	protected static String getAuthToken(Context context,Account account,Bundle options){
		try {
			return AccountManager.get(context).getAuthToken(account, context.getString(R.string.AuthTokenType), options,null, null, null).getResult().getString(AccountManager.KEY_AUTHTOKEN);			 
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   		    
        return null;
	}
	
	public static boolean connectAccountToServer(Context context,String username,String authToken
			) throws UnknownHostException, IOException, ResponseException{
		return ConfirmAccountByServer.confirm(context,username, authToken);
	}
	
	protected static byte[] executeInGetWay(String theLastURL)throws IOException,ResponseException{
		Log.i(TAG, "url:"+theLastURL);
		HttpGet request = new HttpGet(theLastURL);
		if(mHttpClient==null){
			return null;
		}
		
		HttpResponse response = mHttpClient.execute(request);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == 200) {
					return EntityUtils.toByteArray(response
							.getEntity());	
			} else{
				response.getEntity().consumeContent();
				ResponseException exception=new ResponseException();
				Log.e(TAG,"downLoadProduct method"+response.getStatusLine().getStatusCode());
				exception.setResponseCode(response.getStatusLine().getStatusCode());
				throw exception;
			}
		}
		return null; 
	}
	
	 //the following are used by syncAdapter
	protected static boolean syncLogin(Context context,Account account) throws UnknownHostException, IOException, ResponseException {
		Bundle options=new Bundle();
		options.putBoolean(AuthenticatorConstants.GET_AUTHTOKEN_WIRHOUT_NETCONFIRM, true);
		String authToken=getAuthToken(context,account,options);
		if(authToken!=null&&connectAccountToServer(context,account.name, authToken)){
			return true;
		}
		return false;
	}
	
	protected static byte[] executeInGetWayOfSync(Context context,Account account,String theLastURL)throws IOException,ResponseException{
		HttpGet request = new HttpGet(theLastURL);
		if(mHttpClient==null){
			if(!syncLogin(context, account)){
				return null;
			}
		}
		
		HttpResponse response = mHttpClient.execute(request);
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == 200) {
					return EntityUtils.toByteArray(response
							.getEntity());	
			} else{
				response.getEntity().consumeContent();
				ResponseException exception=new ResponseException();
				Log.e(TAG,"downLoadProduct method"+response.getStatusLine().getStatusCode());
				exception.setResponseCode(response.getStatusLine().getStatusCode());
				throw exception;
			}
		}
		return null; 
	}
	
	
	/**
	 * used to get IP.If net is not connecting null will be returned.
	 * @return 
	 */
	public String getLocalIpAddress() {   
        try {   
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {   
                 NetworkInterface intf = en.nextElement();   
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {   
                     InetAddress inetAddress = enumIpAddr.nextElement();   
                    if (!inetAddress.isLoopbackAddress()) {   
                        return inetAddress.getHostAddress().toString();   
                    }   
                }   
             }   
         } catch (SocketException e) {   
             e.printStackTrace();
         }   
         return null;   
    }
}
