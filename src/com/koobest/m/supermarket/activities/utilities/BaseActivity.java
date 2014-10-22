package com.koobest.m.supermarket.activities.utilities;

import java.io.IOException;
import java.net.UnknownHostException;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends Activity{
	public final static int NEED_LOGIN_DIG=100;
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id){
		case NEED_LOGIN_DIG:
			return new AlertDialog.Builder(this).setMessage(getString(R.string.gen_loginneed_alert))
			.setNegativeButton("No", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();					
				}
			}).setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					//TODO to optimize
					new LoginTask(BaseActivity.this.getParent()==null?
							BaseActivity.this:BaseActivity.this.getParent()).execute(); 
					dialog.dismiss();
				}
			}).create();
		}
		return null;
	}
	
	
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("BaseActivity","onResult:"+resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED){			
			Constants.setIsLoginNeed(true);
			//confirm_button.setEnabled(false);
		}else if(resultCode==RESULT_OK){
			Constants.setIsLoginNeed(false);
			String username=data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			String authToken=data.getStringExtra(AccountManager.KEY_AUTHTOKEN);
			Log.e("mUserName", username);
    		Log.e("mAuthToken", authToken);
    		if(authToken!=null&&username!=null){
//    			try {
        			Constants.setAuthtoken(authToken);
//    				NetworkUtilities.connectAccountToServer(getApplicationContext(),username, authToken);
//    			} catch (UnknownHostException e) {
//    			} catch (IOException e) {
//    			} catch (ResponseException e) {
//    			} 
    		}
		}
	}
}
