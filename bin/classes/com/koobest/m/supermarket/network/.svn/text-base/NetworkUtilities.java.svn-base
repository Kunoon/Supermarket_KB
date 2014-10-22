package com.koobest.m.supermarket.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.koobest.m.authenticate.constant.AuthenticatorConstants;
import com.koobest.m.network.toolkits.CommonNetworkUtilities;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;

public class NetworkUtilities extends CommonNetworkUtilities{
	private static final String TAG="NetworkUtilities";
	private static final String DOWNLOAD_PRODUCT_URL = HOST+"?route=xkb/m/product/product&product_id=${product_id}";
	private static final String DOWNLOAD_PRODUCTLIST_BY_BARCODE_URL = HOST+"?route=xkb/m/product/search/bybarcode&barcode=${barcode}";
	private static final String DOWNLOAD_PRODUCTLIST_BY_BRAND_URL = HOST+"?route=xkb/m/product/search/bybrand&brand_id=%s&page=%s";
	private static final String DOWNLOAD_PRODUCTLIST_BY_ADVANCED_URL = HOST+"?route=xkb/m/product/search" +
			"&keyword=%s&category_id=%s&manufacturer_id=%s&brand_id=%s&page=%s&sort=%s&order=%s";
	private static final String SUBMIT_QUOTE_URL = HOST+"?route=xkb/m/checkout/ordersubmit/newquote";
	private static final String DOWNLOAD_PAYMENT_URL = HOST+"?route=xkb/m/order/payment_method";
	private static final String DOWNLOAD_PAYMENT_TERM_URL = HOST+"?route=xkb/m/order/payment_term";
	private static final String DOWNLOAD_ORDERLIST_URL = HOST+"?route=xkb/m/account/history";
	//the format of product list is like "p1;p2;p3" p1 p2 p3 are product ids 
	private static final String DOWNLOAD_PRICELIST_URL = HOST+"?route=xkb/m/product/price&product_info=${productlist}";
	private static final String DOWNLOAD_ORDER_DETAIL_URL = HOST+"?route=xkb/m/account/invoice&order_id=%s";
	private static final String CONFIRM_ORDER_URL = HOST+"?route=xkb/m/checkout/ordersubmit/convertquotetoorder";
	//private static final String LOGIN_URL = "https://test.trjcn.com/opencart_interface.php?t=user&f=login&user_id=001&password=123456";
	private static final String CURRENCY_DOWNLOAD_URI = HOST+"?route=xkb/m/common/localization/getcurrencies";

    private static final String ORDER_TEMPLATE_LIST_URL=HOST+"?route=xkb/m/order/order_template/getall";
    private static final String ORDER_TEMPLATE_DETAIL_URL=HOST+"?route=xkb/m/order/order_template&order_template_id=${order_template_id}";
    
    /**
     * if the account you want to confirm is failure.The login activity will start, 
     * while you should take your activity to the first param of this method
     * @param context used to start the authenticate activity,it should not be null;
     *        if you don't want start auth activity you only need 
     *        to send a context could not turn to a activity
     * @param username used to confirm at the first.If the confirm is failure the 
     *                 authenticate acitvity will be start
     * @return if the cofirm without authenticate activity is success,true will be return
     * @throws UnknownHostException can,t access the server at this time
     */
	public static boolean login(final Context context,String username,boolean needStartLoginPage) throws UnknownHostException{
		setHttpClientToNull();
		maybeCreateHttpClient();
		if(context!=null&&username!=null){
			try {
				Bundle options=new Bundle();
				options.putBoolean(AuthenticatorConstants.GET_AUTHTOKEN_WIRHOUT_NETCONFIRM, true);
				String authToken=Constants.getAuthtoken()!=null?
						Constants.getAuthtoken():getAuthToken(context ,
								new Account(username,context.getString(R.string.AccountType)),options);
				if(connectAccountToServer(context,username, authToken)){
					Constants.setAuthtoken(authToken);
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ResponseException e) {
				e.printStackTrace();
			}
		}
        if(context!=null&&(context instanceof Activity)&&needStartLoginPage){
			Intent intent=new Intent("koobest.intent.action.AUTHENTICATE");
        	((Activity)context).startActivityForResult(intent, 0);
		}
		return false;
	}
	
	public static byte[] downLoadProduct(String product_id) throws IOException,ResponseException{
		Log.e(TAG,"start download Product");
		return executeInGetWay(DOWNLOAD_PRODUCT_URL
				.replace("${product_id}", product_id));
		
	}
	
	public static byte[] downLoadProductList(String barcode) throws IOException,ResponseException{
		return executeInGetWay(DOWNLOAD_PRODUCTLIST_BY_BARCODE_URL
				.replace("${barcode}", barcode));
	}
	
	public static byte[] downLoadProductListByBrand(String brandId,int page) throws IOException,ResponseException{
		return executeInGetWay(String.format(DOWNLOAD_PRODUCTLIST_BY_BRAND_URL,
				brandId,String.valueOf(page)));
	}
	
	public static byte[] downLoadProductList(String keyword,String categoryId,String manufacturerId,String brandId,String page) 
	          throws IOException,ResponseException{
		return executeInGetWay(String.format(DOWNLOAD_PRODUCTLIST_BY_ADVANCED_URL
				, URLEncoder.encode(keyword,"utf-8"),categoryId,manufacturerId,brandId,page,"",""));
	}
	
	public static byte[] downLoadOrderList() throws IOException, ResponseException{
		return executeInGetWay(DOWNLOAD_ORDERLIST_URL);
	}
	
	public static byte[] downLoadOrderDetail(int order_id) throws IOException, ResponseException{
		return executeInGetWay(String.format(DOWNLOAD_ORDER_DETAIL_URL,order_id));
	}
	
	public static byte[] downLoadPayment() throws IOException, ResponseException{
		return executeInGetWay(DOWNLOAD_PAYMENT_URL);
	}
	
	public static byte[] downLoadPayTerm() throws IOException, ResponseException{
		return executeInGetWay(DOWNLOAD_PAYMENT_TERM_URL);
	}
	
	public static byte[] uploadQuote(HttpEntity entity) throws IOException, ResponseException{
		HttpPost request = new HttpPost(SUBMIT_QUOTE_URL);
		request.setEntity(entity);
		maybeCreateHttpClient();
		/*if(mHttpClient==null){
			Log.e(TAG,"mHttpClient==null in uploadQuote method");
			return null;
		}*/
		HttpResponse response = mHttpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toByteArray(response.getEntity());
		}else{
			ResponseException exception=new ResponseException();
			exception.setResponseCode(response.getStatusLine().getStatusCode());
			Log.e(TAG,"upload quote resonse code"+response.getStatusLine().getStatusCode());
			throw exception;
		}	
	}
	
	public static byte[] uploadConfirmOfQuote(String orderId) throws ResponseException, ClientProtocolException, IOException{
		List<NameValuePair> responseParams = new ArrayList<NameValuePair>();
		responseParams.add(new BasicNameValuePair("order_id", orderId));
		try {
			HttpEntity entity = new UrlEncodedFormEntity(responseParams,"UTF-8");
			HttpPost request = new HttpPost(CONFIRM_ORDER_URL);
			request.setEntity(entity);
			maybeCreateHttpClient();
			/*if(mHttpClient==null){
				Log.e(TAG,"mHttpClient==null in uploadQuote method");
				return null;
			}*/
			HttpResponse response = mHttpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toByteArray(response.getEntity());
			}else{
				ResponseException exception=new ResponseException();
				exception.setResponseCode(response.getStatusLine().getStatusCode());
				Log.e(TAG,"upload quote resonse code"+response.getStatusLine().getStatusCode());
				throw exception;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	public static byte[] downloadImage(String url) throws IOException, ResponseException{
		return executeInGetWay(url.replace(" ", "%20"));
	}
	
	public static byte[] downloadCurrencyList()  throws IOException, ResponseException{
		return executeInGetWay(CURRENCY_DOWNLOAD_URI);
	}
	
	public static byte[] downloadCustomerProfile()  throws IOException, ResponseException{
		return executeInGetWay(CUSTOMER_DOWNLOAD_URI);
	}
	
	public static byte[] downloadLengthUnitList()  throws IOException, ResponseException{
		return executeInGetWay(LENGTHUNITS_DOWNLOAD_URI);
	}
	
	public static byte[] downloadWeightUnitList()  throws IOException, ResponseException{
		return executeInGetWay(WEIGHTUNITS_DOWNLOAD_URI);
	}
	
	public static byte[] downloadOrderTemplateList()  throws IOException, ResponseException{
		return executeInGetWay(ORDER_TEMPLATE_LIST_URL);
	}
	
	public static byte[] downloadOrderTemplateDetail(int id)  throws IOException, ResponseException{
		return executeInGetWay(ORDER_TEMPLATE_DETAIL_URL.replace("${order_template_id}", String.valueOf(id)));
	}
	
	public static byte[] downLoadPriceList(String product_list) throws UnknownHostException, IOException, ResponseException {
		return executeInGetWay(DOWNLOAD_PRICELIST_URL.replace("${productlist}", product_list));
	}
	
    //the following are used by syncAdapter
	public static byte[] downLoadOrderList(Account account, Context context,
			Date mLastUpdated) throws UnknownHostException, IOException, ResponseException {
		return executeInGetWayOfSync(context, account, DOWNLOAD_ORDERLIST_URL);
	}
	
	public static byte[] downLoadPriceList(Account account, Context context,
			String product_list) throws UnknownHostException, IOException, ResponseException {
		return executeInGetWayOfSync(context, account, DOWNLOAD_PRICELIST_URL.replace("${productlist}", product_list));
	}
}
