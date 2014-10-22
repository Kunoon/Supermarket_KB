package com.koobest.m.authenticate.activities;

import java.io.IOException;
import java.net.UnknownHostException;

import com.koobest.m.authenticate.toolkit.AuthTokenBuilder;
import com.koobest.m.authenticate.toolkit.ConfirmAccountByServer;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.activities.SupermarketMain;
import com.koobest.m.supermarket.constant.Constants;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticatorActivity extends
		AccountAuthenticatorActivity {
	private static final String TAG="MyAccountAuthenticatorActivity";
	private EditText username_ev = null;
	private EditText password_ev = null;
	//private Account[] accounts=null;
	private String mUsername = null;
	private String mPassword = null;
	private String mAuthToken = null;
	private AccountManager mAccountManager=null;
	private ProgressDialog mDialog=null;
	private static boolean isHasAcount=false;
	private Handler mHandler;
	private Bitmap mBackGround;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG,"start");
		View root = LayoutInflater.from(this).inflate(R.layout.login,null, true);
		setContentView(root);
		mHandler = new Handler();
		mAccountManager = AccountManager.get(getBaseContext());
		username_ev = (EditText) findViewById(R.id.lg_et_username);
		password_ev = (EditText) findViewById(R.id.lg_et_password);
		//Intent intent = getIntent();
		//mUsername = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		//mAuthTokenType = intent.getStringExtra(Constants.Key_AuthToken_Type);
		Account[] accounts=mAccountManager.getAccountsByType(getString(R.string.AccountType));
		Log.e(TAG,"accounts size:"+accounts.length);
		if(accounts.length>0){
			isHasAcount=true;
			mUsername=accounts[0].name;
			mPassword=mAccountManager.getPassword(accounts[0]);
			username_ev.setText(mUsername);
			username_ev.setEnabled(false);
			findViewById(R.id.lg_tv_alert).setVisibility(View.VISIBLE);
			password_ev.setText(mPassword);
		}else{
			getAccountFromPreference();
		} 
		                                     
		root.findViewById(R.id.lg_btn_login).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(username_ev.getText().length() == 0){
					Toast.makeText(getBaseContext(), getString(R.string.lg_lackname),
							Toast.LENGTH_SHORT).show();
					return;
				} else if(password_ev.getText().length() == 0){
					Toast.makeText(getBaseContext(), getString(R.string.lg_lackpassword),
							Toast.LENGTH_SHORT).show();
					return;
				}
				new AuthTask().execute();
			}
		});
		findViewById(R.id.gen_btn_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onBackPressed();
			}
		});
		if(SupermarketMain.getBackGround(this)!=null){
			findViewById(R.id.gen_background).setBackgroundDrawable(SupermarketMain.getBackGround(this));
		}else{
			try{
	    		mBackGround=BitmapFactory.decodeResource(getResources(), R.drawable.bg_img, null);
	    	}catch (OutOfMemoryError e) {
	    		BitmapFactory.Options options=new BitmapFactory.Options();
	        	options.inSampleSize=2;
	        	mBackGround=BitmapFactory.decodeResource(getResources(), R.drawable.bg_img, options);
			}
	    	findViewById(R.id.gen_background).setBackgroundDrawable(new BitmapDrawable(mBackGround));
		}
	}
	
	private void getAccountFromPreference() {
	    SharedPreferences preference=getPreferences(MODE_PRIVATE);
	    if(preference.getString("username", "").length()!=0){
	    	isHasAcount=false;
	    	mUsername=preference.getString("username", "");
	    	mPassword=preference.getString("password", "");
	    	username_ev.setText(mUsername);
			findViewById(R.id.lg_tv_alert).setVisibility(View.VISIBLE);
			password_ev.setText(mPassword);
	    }
	}
	
	@Override
    protected void onDestroy() {
		if(mBackGround!=null){
			mBackGround.recycle();
		}
    	super.onDestroy();
    }
	
    private void performAuthSuccessResult(){
    	//final Account account= new Account(mUsername,
    		//	"com.koobest.account");
    	final Account account= new Account(mUsername,
    			getString(R.string.AccountType));
    	Log.e(TAG, "AccountType:"+account.type);
		if (!isHasAcount){
			mAccountManager.addAccountExplicitly(account, mPassword, null);
			startSync(account, new Bundle());
		} else{
			mAccountManager.setPassword(account, mPassword);
			mAccountManager.setAuthToken(account, getString(R.string.AuthTokenType),
					mAuthToken);
		}
		{
			SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
			editor.putString("username", mUsername);
			editor.putString("password", mPassword);
			editor.commit();
		}
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
				getString(R.string.AccountType));
		//if (mAuthTokenType != null
			//	&& mAuthTokenType
				//		.equals(getString(R.string.AuthTokenType))) {
			intent.putExtra(AccountManager.KEY_AUTHTOKEN,mAuthToken);
		//}
		setAccountAuthenticatorResult(intent.getExtras());
		hideProgress();
		setResult(RESULT_OK, intent);						
		finish();	
    }
	
	private void startSync(Account account,Bundle extra){
		
		ContentResolver.setIsSyncable(account,
				getString(R.string.authority_currency), 1);
		ContentResolver.setIsSyncable(account,
				getString(R.string.authority_price), 1);
		ContentResolver.setIsSyncable(account,
				getString(R.string.authority_constants), 1);
		ContentResolver.setIsSyncable(account,
				getString(R.string.authority_orders), 1);
		ContentResolver.setIsSyncable(account,
				getString(R.string.authority_customer), 1);

		SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		ContentResolver.addPeriodicSync(account, 
				getString(R.string.authority_currency), new Bundle(extra), Integer.valueOf(preferences.getString(getString(R.string.period_currency), "12"))*3600);//unit from string hour
		ContentResolver.addPeriodicSync(account, 
				getString(R.string.authority_price), new Bundle(extra), 24*3600);
		ContentResolver.addPeriodicSync(account, 
				getString(R.string.authority_constants), new Bundle(extra), 604800);
		ContentResolver.addPeriodicSync(account, 
				getString(R.string.authority_orders), new Bundle(extra), Integer.valueOf(preferences.getString(getString(R.string.period_orderlist), "2"))*3600);//unit from string hour
		ContentResolver.addPeriodicSync(account, 
				getString(R.string.authority_customer), new Bundle(extra), 24*3600);
		
		
		ContentResolver.setSyncAutomatically(account,
				getString(R.string.authority_currency), true);
		ContentResolver.setSyncAutomatically(account,
				getString(R.string.authority_price), true);
		ContentResolver.setSyncAutomatically(account,
				getString(R.string.authority_constants), true);
		ContentResolver.setSyncAutomatically(account,
				getString(R.string.authority_orders), true);
		ContentResolver.setSyncAutomatically(account,
				getString(R.string.authority_customer), true);
		
		
		//will set it syncable after customerprofile has been synced at first time
		//ContentResolver.setIsSyncable(account,
			//	getString(R.string.authority_orders), 0);
		Log.e(TAG,"MasterSyncAutomatically() "+String.valueOf(ContentResolver.getMasterSyncAutomatically()));	
	}

	private void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					AuthenticatorActivity.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	private void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		if (KeyEvent.KEYCODE_BACK == keyCode && event.getRepeatCount() == 0) {
//			Intent intent=new Intent();
//			setResult(RESULT_CANCELED,intent);
//			finish();
//		}
//		return true;
//	}
	
	@Override
	public void onBackPressed() {
		if(isHasAcount){
//			Process.killProcess(Process.myPid());
//			System.exit(0);
			Constants.setUsername(mUsername);
		}else{
			Constants.setUsername(null);
		}
		Intent intent=new Intent();
		setResult(RESULT_CANCELED,intent);
		finish();
	}
	
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			mUsername=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Account account=new Account(mUsername, getBaseContext().getString(R.string.AccountType));	
			mPassword=mAccountManager.getPassword(account);
			if(mPassword==null){
				return;
			}
			username_ev.setText(mUsername);
			password_ev.setText(mPassword);  
		}
	}*/
	
	class AuthTask extends AsyncTask<Object, Integer, Bundle>{
		
		@Override
		protected void onPreExecute() {
			showProgress();
		}
		
		protected Bundle doInBackground(Object... arg0) {
			mUsername = username_ev.getText().toString().trim();
			mPassword = password_ev.getText().toString();
			mAuthToken = getAuthtoken(mUsername, mPassword);
			Bundle result =new Bundle();
			
			result.putBoolean("result",true);
			return result;
//			try {
//				if(ConfirmAccountByServer.confirm(AuthenticatorActivity.this,mUsername, mAuthToken)){
//					result.putBoolean("result",true);
//					return result;
//				} else {
//					result.putBoolean("result",false);
//					result.putBoolean("confirm_failure",true);
//					return result;
//				}
//			} catch (UnknownHostException e) {
//				result.putBoolean("result",false);
//				result.putBoolean("connect_failure",true);
//				e.printStackTrace();
//				return result;
//			} catch (IOException e) {
//				result.putBoolean("result",false);
//				result.putBoolean("connect_failure",true);
//				e.printStackTrace();
//				return result;
//			} catch (final ResponseException e) {
//				e.printStackTrace();
//				result.putBoolean("result",false);
//				result.putBoolean("confirm_failure",true);
//				return result;
//			}	
		}
		
		private String getAuthtoken(String username,String password){
			AuthTokenBuilder builder = new AuthTokenBuilder();
			builder.makeAuthToken(mUsername, mPassword);
			return builder.getAuthToken();
		}
		
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean("result",false)){
				performAuthSuccessResult();
				return;
			} else {
				hideProgress();
				if(result.getBoolean("confirm_failure",false)){
					Toast.makeText(getBaseContext(),
							getString(R.string.lg_invalidaccount),
							Toast.LENGTH_SHORT).show();
				} else if(result.getBoolean("connect_failure",false)){
					Toast.makeText(getBaseContext(),
							getString(R.string.lg_connectfail),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	}	
	
	public void notifyStartLoadCustomer(){
		mHandler.post(new Runnable() {
			
			public void run() {
				if(mDialog!=null){
					((ProgressDialog)mDialog).setMessage(getString(R.string.load_statu_customer));
				}
			}
		});
	}
	
	public void notifyStartLoadCurrency() {
		mHandler.post(new Runnable() {
			
			public void run() {
				if(mDialog!=null){
					((ProgressDialog)mDialog).setMessage(getString(R.string.load_statu_currency));
				}
			}
		});
	}

	public void notifyStartLoadPayment() {
		mHandler.post(new Runnable() {
			
			public void run() {
				if(mDialog!=null){
					((ProgressDialog)mDialog).setMessage(getString(R.string.load_statu_payment));
				}
			}
		});
	}

	public void notifyStartLoadPayterm() {
		mHandler.post(new Runnable() {
			
			public void run() {
				if(mDialog!=null){
					((ProgressDialog)mDialog).setMessage(getString(R.string.load_statu_pay_term));
				}
			}
		});
	}
}
