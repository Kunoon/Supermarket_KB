package com.koobest.m.network.toolkits;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class NetWork {
	protected static final int REGISTRATION_TIMEOUT = 10 * 1000;
	public static final String HOST="http://bestiot.com/store/index.php";
	protected static HttpClient mHttpClient=null;
	protected final static String mAuthenticateAccountURL = CommonNetworkUtilities.HOST+
		"?route=xkb/m/common/silentlogin&email=%s" +
		"&token=%s&language_code=%s&currency_code=%s";
	
	protected static HttpClient maybeCreateHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = HttpClientFactory.createHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
        }
        return mHttpClient;
    }
	
    public static void setHttpClientToNull(){
    	if(mHttpClient!=null){
    		mHttpClient.getConnectionManager().shutdown();
    	}
    	mHttpClient=null;
    }
}
