package com.koobest.m.supermarket.activities.setting;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		((ListPreference) findPreference("period_currency")).setSummary(((ListPreference) findPreference("period_currency")).getEntry());
		((ListPreference) findPreference("period_orderlist")).setSummary(((ListPreference) findPreference("period_orderlist")).getEntry());
		((ListPreference) findPreference("cache_pro_detail")).setSummary(((ListPreference) findPreference("cache_pro_detail")).getEntry());
		SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(this);		
        prefs.registerOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equalsIgnoreCase("period_currency")){
			 int peroid=Integer.valueOf(((ListPreference) findPreference(key)).getValue())*3600;
			 ContentResolver.addPeriodicSync(new Account(Constants.getUsername(), getString(R.string.AccountType)), 
						getString(R.string.authority_currency), new Bundle(), peroid);
//			 ((ListPreference) findPreference(key)).setSummary(((ListPreference) findPreference(key)).getEntry());
		}else if(key.equalsIgnoreCase("period_orderlist")){
			 int peroid=Integer.valueOf(((ListPreference) findPreference(key)).getValue())*3600;
			 ContentResolver.addPeriodicSync(new Account(Constants.getUsername(), getString(R.string.AccountType)), 
						getString(R.string.authority_orders), new Bundle(), peroid);
//			 ((ListPreference) findPreference(key)).setSummary(((ListPreference) findPreference(key)).getEntry());
		}else if(key.equalsIgnoreCase("cache_pro_detail")){
			 int peroid=Integer.valueOf(((ListPreference) findPreference(key)).getValue());
			 setProductCachePeriod(this,peroid);
		}
		((ListPreference) findPreference(key)).setSummary(((ListPreference) findPreference(key)).getEntry());
	}
	
	//period in day
	public static void setProductCachePeriod(Context context,int peroid){
		AlarmManager am=(AlarmManager) context.getSystemService(ALARM_SERVICE);
		Intent intent=new Intent(context,SettingBroadcastReceiver.class);
		intent.putExtra("peroid", peroid);
		PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 24*3600*1000, pi);//unit is mill
	}
}
