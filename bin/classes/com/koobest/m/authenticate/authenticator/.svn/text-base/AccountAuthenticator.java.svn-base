package com.koobest.m.authenticate.authenticator;

import java.io.IOException;
import java.net.UnknownHostException;

import com.koobest.m.authenticate.constant.AuthenticatorConstants;
import com.koobest.m.authenticate.toolkit.AuthTokenBuilder;
import com.koobest.m.authenticate.toolkit.ConfirmAccountByServer;
import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

	private Context mContext;
	private static final String TAG = "AccountAuthenticator";

	public AccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {

		Bundle bundle = new Bundle();
		// here can set KEY_ACCOUNT_NAME and KEY_ACCOUNT_TYPE to bundle
		Intent intent = new Intent("koobest.intent.action.AUTHENTICATE");
		//intent.setAction("koobest.intent.action.AUTHENTICATE");
		
		Log.e(TAG+"addAccount", "intent");
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		AccountManager am = AccountManager.get(mContext);
		String username = null;
		if(account!=null){
			username = account.name;
			String password = am.getPassword(account);
			if (username != null && password != null){
				AuthTokenBuilder builder = new AuthTokenBuilder();
				builder.makeAuthToken(username, password);
				String authToken = builder.getAuthToken();
//				if(options!=null&&options.getBoolean(AuthenticatorConstants.GET_AUTHTOKEN_WIRHOUT_NETCONFIRM)==true){
//					Log.e(TAG,"withoutConfirm");
					final Bundle result = new Bundle();
					result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
					result.putString(AccountManager.KEY_ACCOUNT_TYPE, mContext
							.getString(R.string.AccountType));
					result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
					return result;
//				}
//				try {
//					if (ConfirmAccountByServer.confirm(context,username, authToken)) {
//						final Bundle result = new Bundle();
//						result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
//						result.putString(AccountManager.KEY_ACCOUNT_TYPE, mContext
//								.getString(R.string.AccountType));
//						result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
//						return result;
//					}
//				} catch (UnknownHostException e) {
//					Log.e(TAG,"getAuthtokenMethod "+e.getMessage());
//					e.printStackTrace();
//				} catch (IOException e) {
//					Log.e(TAG,"getAuthtokenMethod "+e.getMessage());
//					e.printStackTrace();
//				} catch (ResponseException e) {
//					Log.e(TAG,"getAuthtokenMethod "+String.valueOf(e.getResponseCode()));
//					e.printStackTrace();
//				}
			}
		}		
			
		//final Intent intent = new Intent(mContext,
			//	MyAccountAuthenticatorActivity.class);
		Intent intent = new Intent("koobest.intent.action.AUTHENTICATE");
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
		//intent.putExtra(AuthenticatorConstants.Key_AuthToken_Type, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}
}
