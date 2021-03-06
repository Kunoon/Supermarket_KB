package com.koobest.m.supermarket.activities;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;

import com.koobest.m.authenticate.activities.AuthenticatorActivity;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.dlg.forupdateneed.DialogFactory;
import com.koobest.m.supermarket.activities.dlg.forupdateneed.DialogFactory.OnUpdateStatuListener;
import com.koobest.m.supermarket.activities.productsearch.SearchProductTab;
import com.koobest.m.supermarket.activities.quotehandler.EditQuote;
import com.koobest.m.supermarket.activities.setting.Setting;
import com.koobest.m.supermarket.application.MarketApplication;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import dalvik.system.VMRuntime;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SupermarketMain extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG="SupermarketMain";
	public static final String IS_LOGIN_NEED="isLoginNeed";
//	private Account mAccount=null;
	private ProgressDialog mDialog;
	private View exit_btn_layLayout;
	private int mSelectedCurrecnyId=-1,mCurrCurrencySelectIndex=-1;
	private static final int LOGIN_ALERT=0;
	private static final int DLG_FINISH=3;
	private static final int NEEDDOWN_CURRENCY_DIALOG=1;
	private static final int SHOW_CURRENCYLIST_DIALOG=2;
	private static Bitmap mBackGroundBitmap;
	private static Drawable mBackGround;//this will be used in all pages
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG,"onCreate");
		if(!((MarketApplication)getApplication()).hasLoding){
    		Process.killProcess(Process.myPid());
    		return;
    	}
		setContentView(R.layout.main);
		if(getIntent().getBooleanExtra(IS_LOGIN_NEED, true)){
			Intent intent=new Intent("koobest.intent.action.AUTHENTICATE");
        	SupermarketMain.this.startActivityForResult(intent, 0);
		}else{
			Constants.setIsLoginNeed(false);
		}
		
		SharedPreferences preferences = getSharedPreferences(getString(R.string.config_file_key),MODE_WORLD_READABLE);
		mSelectedCurrecnyId=preferences.getInt("currency_id", -1);
		if(mSelectedCurrecnyId==-1){
			Cursor cursor=getContentResolver().query(
					SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
					new String[] { SYNC_PROVIDER_NAME.CURRENCY_ID},
					SYNC_PROVIDER_NAME.VALUE+"=1", null,null);
			if(cursor.moveToFirst()){
				mSelectedCurrecnyId=cursor.getInt(0);
			}
			cursor.close();
		}
		
		/* Button On Click Event ,turn to Search Product Activity */
		findViewById(R.id.main_btn_search).setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				if(Constants.getCustomerId()==-1){
					showDialog(LOGIN_ALERT);
//					return;
				}
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), SearchProductTab.class);
				SupermarketMain.this.startActivity(intent);
			}
		});		
		/* Button On Click Event ,turn to My Favorites Activity */
		findViewById(R.id.main_btn_favorite).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(Constants.getCustomerId()==-1){
					showDialog(LOGIN_ALERT);
//					return;
				}
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), MyFavorites.class);
				SupermarketMain.this.startActivity(intent);
			}
		});	
		/* Button On Click Event ,turn to Prepare Quote Activity */
		findViewById(R.id.main_btn_quote).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(Constants.getCustomerId()==-1){
					showDialog(LOGIN_ALERT);
//					return;
				}
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), EditQuote.class);
				SupermarketMain.this.startActivity(intent);
			}
		});		
		/* Button On Click Event ,turn to My Quotes And Orders Activity */
		findViewById(R.id.main_btn_order).setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(Constants.getCustomerId()==-1){
					showDialog(LOGIN_ALERT);
//					return;
				}
				Intent intent = new Intent();
				intent.setClass(getBaseContext(),
						MyQuotesAndOrders.class);
				SupermarketMain.this.startActivity(intent);
			}
		});	
		findViewById(R.id.main_btn_template).setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(Constants.getCustomerId()==-1){
					showDialog(LOGIN_ALERT);
//					return;
				}
				Intent intent = new Intent();
				intent.setClass(getBaseContext(),
						PrewiredContainerList.class);
				SupermarketMain.this.startActivity(intent);
			}
		});
		
		
    	
		/* Button On Click Event ,Exit */
		findViewById(R.id.main_btn_exit).setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
			{
				onFinish();
			}
		});
		
		try{
    		mBackGroundBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bg_img, null);
    	}catch (OutOfMemoryError e) {
    		BitmapFactory.Options options=new BitmapFactory.Options();
        	options.inSampleSize=2;
        	mBackGroundBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bg_img, options);
        	Log.e("main","scale background");
		}
    	mBackGround=new BitmapDrawable(mBackGroundBitmap);
    	
    	
	}
 	
	protected void onStart() {
		findViewById(R.id.gen_background).setBackgroundDrawable(mBackGround);
		super.onStart();
	}
    
	@Override
	protected void onDestroy() {
	    mBackGround=null;
	    mBackGroundBitmap=null;
	    super.onDestroy();
	}    
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
		} catch (Exception ex) {
		}
	}
	
	 @Override
    public void onBackPressed() 
	{
    	onFinish();
    }
	 
    void onFinish()
    {
    	showDialog(DLG_FINISH);
    }
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
//			android.os.Process.killProcess(android.os.Process.myPid());
//		}
//		return false;
//	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){
			if(Constants.getUsername()==null){
				finish();
				return;
			}else{
				setCustomerId(Constants.getUsername());
			}
			Constants.setIsLoginNeed(true);
		}else if(resultCode==RESULT_OK){
			Constants.setIsLoginNeed(false);
			final String username=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			final String authToken=data.getStringExtra(AccountManager.KEY_AUTHTOKEN);
    		setCustomerId(username);
			Log.e("mUserName", username);
    		Log.e("mAuthToken", authToken!=null?authToken:"");
    		Constants.setUsername(username);		
    		//ContentResolver.requestSync(new Account(username,getString(R.string.AccountType)),
    	    	    //getString(R.string.authority_customer), new Bundle());
    		if(authToken!=null){
    			Constants.setAuthtoken(authToken);
//				new Thread(new Runnable() {
//					
//					public void run() {
//						try {
//							NetworkUtilities.connectAccountToServer(
//									getApplicationContext(),username, authToken);
//						} catch (UnknownHostException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						} catch (ResponseException e) {
//							e.printStackTrace();
//						}
//					}
//				}).start();    			
    		}
		}
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
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, 0,getString(R.string.main_menu_item1));
//		menu.add(0,Menu.FIRST+1,0,getString(R.string.main_menu_item2));
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST:
			showDialog(SHOW_CURRENCYLIST_DIALOG);
			break;
		case Menu.FIRST+1:
		    startActivity(new Intent(getBaseContext(),Setting.class));
		    break;
		}
		return true;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) 
	{
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DLG_FINISH:
			exit_btn_layLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.exit_dialog_btn_nomal));
			break;
		default:
			break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) 
	{
        switch(id){
        case DLG_FINISH:
        	View dialogView = LayoutInflater.from(this).inflate(R.layout.exit_dialog_page, null);
        	exit_btn_layLayout = dialogView.findViewById(R.id.exit_btn_layout);
    		final Dialog dialog=new Dialog(SupermarketMain.this, R.style.TANCStyle);
    		dialog.setContentView(dialogView);
    		dialog.setTitle(R.string.dialog_title_exit_finish);
    		
    		dialogView.findViewById(R.id.btn_no).setOnClickListener(new OnClickListener()
    		{
    			public void onClick(View v)
    			{
    				exit_btn_layLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.exit_dialog_btn_press_no));
    				dialog.dismiss();
    			}
    		});
    		dialogView.findViewById(R.id.btn_yes).setOnClickListener(new OnClickListener()
    		{
    			public void onClick(View v)
    			{
    				//!---delete---------
    		    	Runtime time=Runtime.getRuntime();	
    				Log.i("Memory","MEMORY1::Max:"+time.maxMemory()+";Total:"+time.totalMemory()+";Free:"+time.freeMemory());
    		        Log.i("Memory", "VM_HEAP1::heap:"+VMRuntime.getRuntime().getMinimumHeapSize()+";"+";Utilization:"+VMRuntime.getRuntime().getTargetHeapUtilization()+";Alloc:"+VMRuntime.getRuntime().getExternalBytesAllocated());
    		        //----------------
    				exit_btn_layLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.exit_dialog_btn_press_yes));
    				dialog.dismiss();
    				SupermarketMain.this.finish();
    			}
    		});
    		
    		return dialog;
        case LOGIN_ALERT:
        	return new AlertDialog.Builder(this).
			setMessage(getString(R.string.gen_dig_noaccount)).
			setPositiveButton("ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
				}
			}).create();	
        case NEEDDOWN_CURRENCY_DIALOG:
        	Log.e("currency ","show currency Dialog");
        	return DialogFactory.createCurrencyAlertDlg(SupermarketMain.this, new OnUpdateStatuListener() {
				
				public void onStart() {
					if(Constants.getCustomerId()==-1){
						showDialog(LOGIN_ALERT);
						return;
					}
					showProgress();
				}
				
				public void onFinish(int result) {
					hideProgress();
					if(result==Activity.RESULT_OK){
						showDialog(SHOW_CURRENCYLIST_DIALOG);
					}
				}
			});
        case SHOW_CURRENCYLIST_DIALOG:
        	return createCurrencyListDialog();
        }
        return null;
	}
	
	

	private Dialog createCurrencyListDialog() {
		Cursor cursor = getContentResolver().query(
				SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
				new String[] { SYNC_PROVIDER_NAME.CURRENCY_ID,
	                    SYNC_PROVIDER_NAME.TITLE},
						null, null,null);
		Log.e("currency ","count:"+cursor.getCount());
		if(cursor.getCount()==0){
			cursor.close();
			Log.e("currency ","will show currency Dialog");
			showDialog(NEEDDOWN_CURRENCY_DIALOG);
			return null;
		}
		Log.e("currency ","count"+cursor.getCount());
		String items[]=new String[cursor.getCount()];
		int currency_id[]=new int[cursor.getCount()],index=0;
		while(cursor.moveToNext()){
			items[index]=cursor.getString(1);
			if((currency_id[index]=cursor.getInt(0))==mSelectedCurrecnyId){
				mCurrCurrencySelectIndex=index;
			}
			index++;
		}
		cursor.close();
		DialogInterface.OnClickListener listButClickListner=new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, final int selectedIndex) {
				Log.e("selection", "index:"+selectedIndex);
				Cursor cursor = getContentResolver().query(
						SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
						new String[] { SYNC_PROVIDER_NAME.CODE,SYNC_PROVIDER_NAME.CURRENCY_ID},
								null, null,null);
				Log.e("currency cursor", "count:"+cursor.getCount());
				if(!cursor.moveToPosition(selectedIndex)){
					throw new IllegalArgumentException();
				}
				int currCurrencyId=cursor.getInt(1);
				SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.config_file_key),MODE_WORLD_WRITEABLE).edit();
			    editor.putString(getString(R.string.config_currencyCode_key), cursor.getString(0));
				editor.putInt(getString(R.string.config_currencyId_key),cursor.getInt(1));
				cursor.close();
				editor.commit();
				//if customer logined it before relogin
				if(!Constants.getIsLoginNeed()){
					//reLogin after currency is chosen again.tell server which currency this customer choice
					new AuthAfterResetCurrency(selectedIndex,currCurrencyId).execute();
//					try {
//						NetworkUtilities.connectAccountToServer(getApplicationContext(),
//								Constants.getUsername(), Constants.getAuthtoken());
//					} catch (UnknownHostException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (ResponseException e) {
//						e.printStackTrace();
//					}
				}
				//if customer dosen't login it before
				else{
					mCurrCurrencySelectIndex=selectedIndex;
					mSelectedCurrecnyId = currCurrencyId;
				}
				dialog.dismiss();
			}
		};
		return new AlertDialog.Builder(this).
	    setSingleChoiceItems(items, mCurrCurrencySelectIndex, listButClickListner).create();
	}
		
	private void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					SupermarketMain.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	private void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
	
//	protected void onNewIntent(Intent intent) {
//		Log.i(TAG,"intent");
////		super.onNewIntent(intent);
//	}
	
	class AuthAfterResetCurrency extends AsyncTask<Object, Object, Bundle>{
		private int mNewSelectItemIndex,mNewCurrencyId;
		
		public AuthAfterResetCurrency(int newSelectItemId,int newCurrencyId) {
			mNewSelectItemIndex = newSelectItemId;
			mNewCurrencyId = newCurrencyId;
		}
		@Override
		protected void onPreExecute() {
			showProgress();
		}
	
		@Override
		protected Bundle doInBackground(Object... params) {
			Bundle result = new Bundle();
			try {
				if(NetworkUtilities.connectAccountToServer(getApplicationContext(),
						Constants.getUsername(), Constants.getAuthtoken())){
					result.putBoolean("result", true);
					return result;
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				result.putBoolean("unknowHost", true);
			} catch (IOException e) {
				if(e instanceof ConnectTimeoutException){
					result.putBoolean("timeout", true);
				}
				e.printStackTrace();
			} catch (ResponseException e) {
				e.printStackTrace();
			}
			if(mCurrCurrencySelectIndex!=-1){
				Cursor cursor = getContentResolver().query(
						SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
						new String[] { SYNC_PROVIDER_NAME.CODE,SYNC_PROVIDER_NAME.CURRENCY_ID},
								null, null,null);
				if(!cursor.moveToPosition(mCurrCurrencySelectIndex)){
					throw new IllegalArgumentException();
				}
				SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.config_file_key),MODE_WORLD_WRITEABLE).edit();
			    editor.putString(getString(R.string.config_currencyCode_key), cursor.getString(0));
				editor.putInt(getString(R.string.config_currencyId_key),cursor.getInt(1));
				cursor.close();
				editor.commit();
			}else{
				SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.config_file_key),MODE_WORLD_WRITEABLE).edit();
			    editor.putString(getString(R.string.config_currencyCode_key), "");
				editor.putInt(getString(R.string.config_currencyId_key),-1);
				editor.commit();
			}
			return new Bundle();
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			hideProgress();
			if(result.getBoolean("result", false)){
				mCurrCurrencySelectIndex=mNewSelectItemIndex;
				mSelectedCurrecnyId = mNewCurrencyId;
			}else{
				if(!SupermarketMain.this.isFinishing()){
					if(result.getBoolean("unknowHost", false)){
						Toast.makeText(getBaseContext(), getString(R.string.gen_unknown_host_toast), Toast.LENGTH_SHORT).show();
					}else if(result.getBoolean("timeout", false)){
						Toast.makeText(getBaseContext(), getString(R.string.gen_dlg_net_timeout), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getBaseContext(), getString(R.string.gen_dig_auth_fail), Toast.LENGTH_SHORT).show();
					}
					removeDialog(SHOW_CURRENCYLIST_DIALOG);
					showDialog(SHOW_CURRENCYLIST_DIALOG);
				}
			}
		}
	}
	
	//for others
	public static Drawable getBackGround(Context context){
		if((context instanceof AuthenticatorActivity)){
			return mBackGround;
		}
		if(mBackGroundBitmap==null||mBackGroundBitmap.isRecycled()){
			try{
	    		mBackGroundBitmap=BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.bg_img, null);
	    	}catch (OutOfMemoryError e) {
	    		BitmapFactory.Options options=new BitmapFactory.Options();
	        	options.inSampleSize=2;
	        	mBackGroundBitmap=BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.bg_img, options);
	        	Log.e("main","scale background");
			}
	    	mBackGround=new BitmapDrawable(mBackGroundBitmap);
		}
		return mBackGround;
	}
}