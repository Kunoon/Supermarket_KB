package com.koobest.m.supermarket.activities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.dlg.forupdateneed.DialogFactory;
import com.koobest.m.supermarket.activities.dlg.forupdateneed.DialogFactory.OnUpdateStatuListener;
import com.koobest.m.supermarket.activities.quotehandler.EditQuote;
import com.koobest.m.supermarket.activities.quotehandler.OverrideWarnDlg;
import com.koobest.m.supermarket.activities.quotehandler.OverrideWarnDlg.OnBtnClickListener;
import com.koobest.m.supermarket.activities.utilities.BaseActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.database.SAVE_PATH;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.DefaultHandlerOrderDetail;
import com.koobest.m.supermarket.toolkits.aboutprice.UpdateProductPriceTask;
import com.koobest.m.supermarket.toolkits.aboutprice.UpdateProductPriceTask.OnTaskStautChangeListener;
import com.koobest.m.toolkits.parsexml.ParseXml;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class QuoteConfirm extends BaseActivity{
	private static String TAG="QuoteConfirm";
	
	private final static int NETWORK_FAIL_DIALOG = 1;
	private final static int DETAIL_FAIL_DIALOG = 4;
	private static final int NEEDDOWN_CURRENCY_DIALOG=5;
	private static final int NEEDDOWN_CUSTOMER_DIALOG=6;
	private static final int NEEDDOWN_LENGTH_DIALOG=7;
	private static final int NEEDDOWN_WEIGHT_DIALOG=8;

	protected static final int UPDATE_PRICE_FAIL_DLG = 0;
	private String mXmlAddress;
	private int mOrder_id;
	private ProgressDialog mDialog=null;
	private Button bt_update;
	private Button bt_confirm;
	private WebView tv_detail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setProgress(802);
		setContentView(R.layout.quote_confirm);
		
		tv_detail = (WebView) findViewById(R.id.detail);
		bt_confirm = (Button) findViewById(R.id.confirm_order);
		bt_update = (Button) findViewById(R.id.update_quote);
		tv_detail.setWebViewClient(new WebViewClient());
        mOrder_id=getIntent().getIntExtra(NAME.ORDER_ID, -1);
        if(mOrder_id==-1){
        	finish();
        }
        
        new DisplayDetailTask(mOrder_id, getBaseContext(),tv_detail).execute();
        
        Cursor cursor = getContentResolver().query(PROVIDER_NAME.ORDER_CONTENT_URI,
        		new String[]{PROVIDER_NAME.ORDER_COMMENT,PROVIDER_NAME.STATUS_ID},
        		PROVIDER_NAME.ORDER_ID+"="+mOrder_id, 
        		null, null);
        if(cursor.moveToFirst()){
//        	((TextView)findViewById(R.id.name_quoteconfirm)).setText(getString(R.string.g_tag)+(cursor.getString(0)==null?"":cursor.getString(0)));
        	if(cursor.getInt(1)==102){
        		bt_confirm.setVisibility(View.VISIBLE);
        		bt_update.setVisibility(View.VISIBLE);
        		bt_update.setEnabled(false);
        		bt_confirm.setEnabled(false);
        		bt_update.setOnClickListener(new OnClickListener() {

        			public void onClick(View v) {
        				parseDetail();
        			}
        		});
        		
        		bt_confirm.setOnClickListener(new OnClickListener() {

        			public void onClick(View v) {
        				new ConfirmQuoteTask(mOrder_id).execute();
        			}
        		});
        	}else{
        		findViewById(R.id.as_temp_cq).setVisibility(View.VISIBLE);
        		findViewById(R.id.as_temp_cq).setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						parseDetail();
					}
				});
        	}
        }
        cursor.close();
		
		/**
		 * the Button of Back to Main onClick Event. finished this activity and
		 * go back to the SupermarketMain Activity
		 * */
        findViewById(R.id.gen_btn_main).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SupermarketMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Log.i(TAG,"flag:"+intent.getFlags());
				startActivity(intent);
			}
		});
		findViewById(R.id.gen_btn_back).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
	}	
	
	@Override
	protected void onStart() {
		findViewById(R.id.gen_background).setBackgroundDrawable(SupermarketMain.getBackGround(getApplicationContext()));
		super.onStart();
	}
	
	private void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					QuoteConfirm.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	private void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
	void overrideQuote(){
		SQLiteDatabase mDatabase= new DatabaseHelper(getApplicationContext()).getReadableDatabase();
		try{
			Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_QUOTES, 
					new String[]{PROVIDER_NAME.QUOTE_ID},
					PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null, null, null);
			for(int quoteId;cursor.moveToNext();){
				quoteId=cursor.getInt(0);
				mDatabase.delete(NAME.TABLE_QUOTE_PRODUCT, NAME.QUOTE_ID+"="+quoteId, null);
			}
			cursor.close();
			mDatabase.delete(NAME.TABLE_QUOTES, PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null);
			parseDetailToTableQuote();
		}finally{
			mDatabase.close();
		}
	}
	
	private void parseDetail(){
		Cursor cursor = getContentResolver().query(PROVIDER_NAME.QUOTE_CONTENT_URI,
				new String[]{PROVIDER_NAME.QUOTE_ID},
				PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null);
		if(cursor.getCount()==0){
			cursor.close();
			parseDetailToTableQuote();
//				Toast.makeText(getBaseContext(), "parse error", Toast.LENGTH_SHORT);
//				return;
//			Intent intent = new Intent();
//			intent.setClass(getApplicationContext(),EditQuote.class);
//			intent.putExtra("order_id", mOrder_id);
//			startActivity(intent);
		} else{
			cursor.close();
			OverrideWarnDlg.builder(QuoteConfirm.this, new OnBtnClickListener() {
				
				@Override
				public void onPositiveBtnClick() {
					overrideQuote();
				}
			}).create().show();
		}
	}

	private void parseDetailToTableQuote(){
		showProgress();
		Cursor cursor = getContentResolver().query(
				PROVIDER_NAME.ORDER_CONTENT_URI,
				new String[] { PROVIDER_NAME.ORDER_XML },
				PROVIDER_NAME.ORDER_ID + "=" + mOrder_id, null, null);
		cursor.moveToFirst();
		InputStream xml_is = null;
		String xml_address = cursor.getString(0);
		cursor.close();
		Log.e("xml_address update quote", xml_address);
		try {
			xml_is = getContentResolver().openInputStream(
					Uri.withAppendedPath(PROVIDER_NAME.ORDER_CONTENT_URI,
							xml_address)

			);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if(xml_is==null){
			Toast.makeText(getBaseContext(), "parse error", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			byte[] b = new byte[xml_is.available()];
			xml_is.read(b);
			if(bt_update.isShown()){
				ParseXml.handleXmlInSAX(b, new DefaultHandlerOrderDetail(getApplicationContext(),false));
			}else{
				ParseXml.handleXmlInSAX(b, new DefaultHandlerOrderDetail(getApplicationContext(),true));
			}
			new UpdateProductPriceTask(this, new OnTaskStautChangeListener() {
				public void onStart() {
					// TODO Auto-generated method stub
				}
				public void onFinished(Bundle result) {
					hideProgress();
					if(result.getBoolean(UpdateProductPriceTask.TASK_RESULT_KEY, true)){
						Intent intent=new Intent(getBaseContext(), EditQuote.class);
						if(bt_update.isShown()){
							intent.putExtra("order_id", mOrder_id);
						}else{
							intent.putExtra("order_id", -1);
						}
						startActivity(intent);
						finish();
					}else{
						showDialog(UPDATE_PRICE_FAIL_DLG);
					}
				}
			}).execute();
			return;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
//			if(e.getMessage().equalsIgnoreCase("no customer infor needed in local")){
//				showDialog(NEEDDOWN_CUSTOMER_DIALOG);
//			}else 
			if(e.getMessage().equalsIgnoreCase("no currency you need")){
				showDialog(NEEDDOWN_CURRENCY_DIALOG);return;
			}else if(e.getMessage().equalsIgnoreCase("the weight unit need could not be found in local")){
				showDialog(NEEDDOWN_WEIGHT_DIALOG);return;
			}else if(e.getMessage().equalsIgnoreCase("the length unit need could not be found in local")){
				showDialog(NEEDDOWN_LENGTH_DIALOG);return;
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		Toast.makeText(getBaseContext(), "parse error", Toast.LENGTH_SHORT).show();
	}
	

	protected Dialog onCreateDialog(int id, Bundle args) {		
		switch (id) {
		case DETAIL_FAIL_DIALOG:
			return new AlertDialog.Builder(this).setMessage(getString(R.string.g_dig_showfail))
			.setPositiveButton(getString(R.string.gen_btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).create();
		case NETWORK_FAIL_DIALOG:
			final int code=args.getInt(ResponseException.RESPONSECODE_KEY);
	        switch(code){
	        case 404:	
	        	return new AlertDialog.Builder(this).
				setMessage(getString(R.string.g_dig_quotenotfind)).
				setPositiveButton(getString(R.string.gen_btn_ok),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which){
						dialog.dismiss();
                        finish();
					}
				}).create();	
	        }
	        
		case UPDATE_PRICE_FAIL_DLG:
			return new AlertDialog.Builder(this).
			setMessage(Html.fromHtml(getString(R.string.gen_dlg_price_update_fail))).
			setPositiveButton(getString(R.string.gen_btn_ok), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent=new Intent(getBaseContext(), EditQuote.class);
					intent.putExtra("order_id", mOrder_id);
					startActivity(intent);
					finish();
				}
			}).create();
		}
		
		OnUpdateStatuListener l= new OnUpdateStatuListener() {
			public void onStart() {
				if(Constants.getCustomerId()==-1){
					return;
				}
				showProgress();
			}
			public void onFinish(int result) {
				hideProgress();
				if(result==Activity.RESULT_OK){
					parseDetailToTableQuote();
				}else if(result==Activity.RESULT_CANCELED){
					Toast.makeText(getBaseContext(), "update failure", Toast.LENGTH_SHORT).show();
				}
			}
		};
		switch(id){
		case NEEDDOWN_CURRENCY_DIALOG:
        	return DialogFactory.createCurrencyAlertDlg(QuoteConfirm.this, l);
		case NEEDDOWN_CUSTOMER_DIALOG:
        	return DialogFactory.createCustomerAlertDlg(QuoteConfirm.this, l);
		case NEEDDOWN_LENGTH_DIALOG:
        	return DialogFactory.createLengthAlertDlg(QuoteConfirm.this, l);
		case NEEDDOWN_WEIGHT_DIALOG:
        	return DialogFactory.createWeightAlertDlg(QuoteConfirm.this, l);
		}
		return super.onCreateDialog(id, args);
	}
	
	class DisplayDetailTask extends AsyncTask<Object, Integer, Bundle>{
        
		private int mOrder_id,mStatusId;
		private Context mContext;
        private WeakReference<WebView> mWebViewRef;
        private byte[] mOrderDetail;
		public DisplayDetailTask(int order_id, final Context context,WebView wv) {
			mWebViewRef=new WeakReference<WebView>(wv);
			mOrder_id = order_id;
			Log.e("detail order_id", String.valueOf(order_id));
			mContext = context;
		}
		
		@Override
		protected void onPreExecute() {
			showProgress();
		}
		@Override
		protected Bundle doInBackground(Object... arg0) {
			Cursor cursor = mContext.getContentResolver().query(
					PROVIDER_NAME.ORDER_CONTENT_URI,
					new String[] { PROVIDER_NAME.ORDER_XML,NAME.STATUS_ID },
					PROVIDER_NAME.ORDER_ID + "=" + mOrder_id, null,
					null);
			cursor.moveToFirst();
			mXmlAddress = cursor.getString(0);
			mStatusId = cursor.getInt(1);
			cursor.close();
			Log.e(TAG,"xml_address:"+mXmlAddress);
			try {
				InputStream xml_is = mContext.getContentResolver().openInputStream(Uri.withAppendedPath(
						PROVIDER_NAME.ORDER_CONTENT_URI, mXmlAddress)
				);
				mOrderDetail = new byte[xml_is.available()];
				xml_is.read(mOrderDetail);
				Matcher matcher = Pattern.compile("<order_status_id>([^<]*+)</order_status_id>").matcher(new String(mOrderDetail));
				matcher.find();
			    if(mStatusId!=Integer.valueOf(matcher.group(1))){
			    	File file = new File(SAVE_PATH.FILE_PATH, mXmlAddress);
			    	if(file.exists()){
			    		file.delete();
			    	}
			    	mOrderDetail=null;
			    	throw new FileNotFoundException();
			    }
				Log.e(TAG,"file has been found!");
			} catch (IOException e) {
				try {
					if(Constants.getIsLoginNeed()){
						Bundle result=new Bundle();
						result.putBoolean("show_loginneeddialog", true);
						return result;
					}
					Bundle data= downloadDetail();
					if(!data.getBoolean("down_success", false)){
						return data;
					}
					OutputStream outputStream = mContext.openFileOutput(
							mXmlAddress, Activity.MODE_WORLD_WRITEABLE);
					outputStream.write(mOrderDetail);
					outputStream.close();
//					xml_is = mContext.getContentResolver().openInputStream(Uri
//							.withAppendedPath(
//									PROVIDER_NAME.ORDER_CONTENT_URI,
//									mXmlAddress));
				} catch (IOException e1) {
					e1.printStackTrace();
				}			
			}

			if (mOrderDetail != null) {
				try {
					parseXml(new ByteArrayInputStream(mOrderDetail));
					mOrderDetail=null;//release memory
					Bundle result = new Bundle();
					result.putBoolean("result", true);
					return result;
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Bundle result = new Bundle();
			result.putBoolean("is_failed", true);//	msg.what = FAIL_TO_DISPLAY_DETAIL;
			return result;
		}
		
		private Bundle downloadDetail() throws IOException{
			try {
				mOrderDetail=NetworkUtilities.downLoadOrderDetail(mOrder_id);
			} catch (final ResponseException e1) {
				e1.printStackTrace();
				if(e1.getResponseCode()==401){
                	//login again to require cooikes
                	if(NetworkUtilities.login(QuoteConfirm.this, Constants.getUsername(),true)==false){
                		Bundle result=new Bundle();
                		result.putBoolean("down_success", false);
                		return result;
                	}
                	//if relogin successful access server again
                	try {
						mOrderDetail= NetworkUtilities.downLoadOrderDetail(mOrder_id);
					} catch (final ResponseException e11) {
						Bundle result=new Bundle();
						result.putBoolean("down_success", false);
		                result.putInt(ResponseException.RESPONSECODE_KEY, e11.getResponseCode());
						e11.printStackTrace();
		                return result;		
					} 
                } else{
                	Bundle result=new Bundle();
                	result.putBoolean("down_success", false);
	                result.putInt(ResponseException.RESPONSECODE_KEY, e1.getResponseCode());  
	                e1.printStackTrace();
	                return result;
                }		
			}
			Bundle data = new Bundle();
			data.putBoolean("down_success", true);
			return data;
		}
		
		private void parseXml(InputStream xml_is)throws NotFoundException, 
		         TransformerException, IOException{
			mContext.openFileOutput(Constants.ORDER_HTML_FILE_DIR,
					MODE_WORLD_WRITEABLE);
			int resourceCode;
			if(Locale.getDefault().getLanguage().equalsIgnoreCase("zh")){
				resourceCode=R.raw.zh_order_xsl;
			} else{
				resourceCode=R.raw.order_xsl;
			}
			ParseXml.turnXmlToHtmlByXSL(xml_is, mContext
					.openFileOutput(Constants.ORDER_HTML_FILE_DIR,
							MODE_WORLD_WRITEABLE), mContext
					.getResources().openRawResource(resourceCode),null);
		}
		@Override
		protected void onPostExecute(Bundle result) {
			hideProgress();
			if(result.getBoolean("result",false)){
				if(bt_update.isShown()){
					bt_update.setEnabled(true);
					bt_confirm.setEnabled(true);
				}
				mWebViewRef.get().loadUrl("file:///data/data/com.koobest.m.supermarket.activities/files/"
						+ Constants.ORDER_HTML_FILE_DIR);
 			}else{
				if(result.getInt(ResponseException.RESPONSECODE_KEY,-1)!=-1){
					showDialog(NETWORK_FAIL_DIALOG, result);
				} else if(result.getBoolean("is_failed", false)){
					showDialog(DETAIL_FAIL_DIALOG);
				} else if(result.getBoolean("show_loginneeddialog",false)){
					showDialog(NEED_LOGIN_DIG);
				}
			}
		}
	}
	
	private class ConfirmQuoteTask extends NetworkWithSAXTask<Object, Object>{
		private final int mOrderId;
		public ConfirmQuoteTask(int orderId) {
			mOrderId=orderId;
		}
		
		@Override
		protected void onPreExecute() {
			bt_confirm.setEnabled(false);
    		bt_update.setEnabled(false);
			showProgress();
		}

		@Override
		protected Context createContext() {
			return QuoteConfirm.this;
		}

		@Override
		protected DefaultHandler createHandler(byte[] xmlBuffer) {
			FileOutputStream outputStream;
			try {
				outputStream = openFileOutput(mXmlAddress,
						Activity.MODE_WORLD_WRITEABLE);
				outputStream.write(xmlBuffer);
				outputStream.close();
				getOrderSimpleInfor(new ByteArrayInputStream(xmlBuffer));
				return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw new IllegalArgumentException();
		}
		/**
		 * update new order status to database
		 * @param source
		 */
		private void getOrderSimpleInfor(InputStream source) {
			XmlPullParser parser= Xml.newPullParser();
			try {
				parser.setInput(source, "UTF-8");
				int eventType = parser.getEventType();
				String tempNode="";
				ContentValues values = new ContentValues();
				while(eventType!=XmlPullParser.END_DOCUMENT){
					tempNode=parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if(tempNode.equalsIgnoreCase("order_status_id")){
							values.put(NAME.STATUS_ID, Integer.valueOf(parser.nextText()));
						}else if(tempNode.equalsIgnoreCase("order_status")){
							values.put(NAME.STATUS, parser.nextText());
						}
						break;
//					case XmlPullParser.END_TAG:
//						break;
					default:
						break;
					}
					eventType=parser.next();
				}
				source.close();
				if(values.size()<=0){
					throw new IllegalArgumentException();
				}
			    SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getWritableDatabase();
			    try{
			    	Cursor cursor = database.query(NAME.TABLE_QUOTES, new String[]{NAME.QUOTE_ID}, NAME.ORDER_ID+"="+mOrderId, null, null, null, null);
			    	if(cursor.moveToFirst()){
				    	database.delete(NAME.TABLE_QUOTE_PRODUCT, NAME.QUOTE_ID+"="+cursor.getInt(0), null);
				    	database.delete(NAME.TABLE_QUOTE_TO_PAYMENT, NAME.QUOTE_ID+"="+cursor.getInt(0), null);
			    	}
			    	cursor.close();
			    	database.delete(NAME.TABLE_QUOTES, NAME.ORDER_ID+"="+mOrderId, null);
			    	database.update(NAME.TABLE_ORDERS, values, NAME.ORDER_ID+"="+mOrderId, null);
			    }finally{
			    	database.close();
			    }
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			}
		}

		@Override
		protected byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.uploadConfirmOfQuote(String.valueOf(mOrderId));
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY, false)){
				new DisplayDetailTask(mOrder_id, getBaseContext(),tv_detail).execute();
				bt_confirm.setVisibility(View.GONE);
        		bt_update.setVisibility(View.GONE);
				return;
			}
			hideProgress();
			if(result.getBoolean(NetworkWithSAXTask.CONNECT_TIME_OUT, false)){
				Toast.makeText(getBaseContext(), "time out", Toast.LENGTH_SHORT).show();//TODO string
			}else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)!=-1){
				if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==400){
					Toast.makeText(getBaseContext(), "Bad request. Maybe this quote statu has change in other way", Toast.LENGTH_SHORT).show();//TODO string
				}
			}else{
				Toast.makeText(getBaseContext(), "unknown wrong", Toast.LENGTH_SHORT).show();//TODO string
			}
			bt_confirm.setEnabled(true);
    		bt_update.setEnabled(true);
		}
	}

	//As a template to create a new quote
}
