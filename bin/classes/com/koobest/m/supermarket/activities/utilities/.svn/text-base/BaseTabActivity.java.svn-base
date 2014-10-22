package com.koobest.m.supermarket.activities.utilities;

import java.io.IOException;
import java.net.UnknownHostException;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class BaseTabActivity extends TabActivity{
	
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
					new LoginTask(BaseTabActivity.this.getParent()==null?
							BaseTabActivity.this:BaseTabActivity.this.getParent()).execute(); 
					dialog.dismiss();
				}
			}).create();
		}
		return null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initTabStyle(getTabHost());
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
	
	protected View composeLayout(String s)
	{
		int marquee = 10;
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setText(s);
		tv.setSingleLine(true);
		tv.setFocusable(false);
		tv.setEllipsize(TruncateAt.MARQUEE);
		tv.setMarqueeRepeatLimit(marquee);
		tv.setTextSize(17);
		tv.setTextColor(Color.BLACK);
		return tv;
	}
	
	private void initTabStyle(final TabHost tabHost){
		final TabWidget tabWidget = tabHost.getTabWidget();
//      int height = 70;
		
		for(int i = 0; i < tabWidget.getChildCount(); i++)
		{
//			tabWidget.getChildAt(i).getLayoutParams().height = height; 
			View view = tabWidget.getChildAt(i);
			if(tabHost.getCurrentTab() ==i)
			{
				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_focus));
			}
			else
			{
				view.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_normal));
			}
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener()
	      {
	      	public void onTabChanged(String tabId) 
	      	{
	      		for(int i = 0; i < tabWidget.getChildCount(); i++){
						View v = tabWidget.getChildAt(i);
						if(tabHost.getCurrentTab() == i)
						{
							v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_focus));
						}
						else
						{
							v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_normal));
						}
				}
	      		BaseTabActivity.this.onTabChange(tabId);
	      	}
	      });
      
	}

    protected void onTabChange(String tabId){};
}
