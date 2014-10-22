package com.koobest.m.sync.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.koobest.m.authenticate.constant.AuthenticatorConstants;
import com.koobest.m.authenticate.toolkit.ConfirmAccountByServer;
import com.koobest.m.network.toolkits.CommonNetworkUtilities;
import com.koobest.m.network.toolkits.HttpClientFactory;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
public class NetworkUtilities extends CommonNetworkUtilities{
	private static final String TAG="syc.NetworkUtilities";
	
	private static final String CURRENCY_DOWNLOAD_URI = HOST+"?route=xkb/m/common/localization/getcurrencies";
	private static final String MANUFACTURER_DOWNLOAD_URI = HOST+"?route=xkb/m/common/manufacturer";
	private static final String BRAND_DOWNLOAD_URI = HOST+"?route=xkb/m/common/brand";
	private static final String CATEGORY_DOWNLOAD_URI = HOST+"?route=xkb/m/common/category";
	private static final String CONTAINER_DOWNLOAD_URI = HOST+"?route=xkb/m/common/localization/getcontainers";
	private static final String PREWIRED_CONTAINER_URI = HOST+"?...";//TODO:replace the final url
	
	
	public static byte[] downLoadCustomer(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, CUSTOMER_DOWNLOAD_URI);
			
	}
	
	public static byte[] downLoadCurrency(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, CURRENCY_DOWNLOAD_URI);
	}
	
	public static byte[] downLoadWeightUnitsProfile(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, WEIGHTUNITS_DOWNLOAD_URI);
	}
	
	public static byte[] downLoadLengthUnitsProfile(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, LENGTHUNITS_DOWNLOAD_URI);
	}
	
	public static byte[] downLoadManufacturerConstants(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, MANUFACTURER_DOWNLOAD_URI);
	}
	
	public static byte[] downLoadCategoryConstants(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, CATEGORY_DOWNLOAD_URI);
	}
	
	public static byte[] downLoadContainerConstants(Account account,
	        Context context) throws IOException, ResponseException{		
		return executeInGetWayOfSync(context, account, CONTAINER_DOWNLOAD_URI);
	}

	public static byte[] downLoadBrandConstants(Account account,
			Context context) throws IOException, ResponseException {
		// TODO Auto-generated method stub
		return executeInGetWayOfSync(context, account, BRAND_DOWNLOAD_URI);
	}
	
}
