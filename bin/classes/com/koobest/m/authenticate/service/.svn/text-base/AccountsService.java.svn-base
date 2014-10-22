package com.koobest.m.authenticate.service;

import com.koobest.m.authenticate.authenticator.AccountAuthenticator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;

public class AccountsService extends Service {
	private static final String TAG="AccountAuthenticator";
	private AccountAuthenticator mAccountAuthenticator;
	
    @Override
    public void onCreate() {
    	super.onCreate();
    }
	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		if (intent.getAction().equals(
				android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
			ret = getAuthenticator().getIBinder();
		return ret;
	}
    
	private AccountAuthenticator getAuthenticator() {
		if (mAccountAuthenticator == null)
			mAccountAuthenticator = new AccountAuthenticator(this);
		return mAccountAuthenticator;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
