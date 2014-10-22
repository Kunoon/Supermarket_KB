package com.koobest.m.supermarket.activities.utilities;

import java.net.UnknownHostException;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * For activities relogin
 * @author 001
 *
 */
public class LoginTask extends AsyncTask<Object, Object, Bundle>{
    private Context mContext;
    private Dialog mDialog = null;
    public LoginTask(Context context) {
		mContext = context; 
	}
	
	@Override
	protected void onPreExecute() {
		if(mContext instanceof Activity){
			mDialog=ProgressDialog.show(
					mContext, null,
					mContext.getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	@Override
	protected Bundle doInBackground(Object... params) {
		try {
			if(NetworkUtilities.login(mContext, Constants.getUsername(),true)){
				Constants.setIsLoginNeed(false);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Bundle result) {
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
}
