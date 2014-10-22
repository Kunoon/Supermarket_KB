package com.koobest.m.supermarket.activities.setting;

import java.util.Date;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction()!=null&&intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			int period=settings.getInt(context.getString(R.string.set_cache_product), 7);
			Log.e("mysetting",""+period);
			AlarmManager am=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent1=new Intent(context,SettingBroadcastReceiver.class);
			intent1.putExtra("peroid", period);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 180*1000, pi);
			return;
		}
		Toast.makeText(context, "receiver:"+intent.getIntExtra("peroid", 7), Toast.LENGTH_SHORT).show();
		Log.i("current time", new Date(System.currentTimeMillis()).toString());
		deleteProducts(context, intent.getIntExtra("peroid", 7));
		deleteBarcodeSearchResults(context, intent.getIntExtra("peroid", 7));
	}
	
	private void deleteProducts(Context context,int differDate){
    	long differTime=differDate*24*60*60*1000;//millSecord
    	long currentTime=System.currentTimeMillis();
//    	context.getContentResolver().delete(PROVIDER_NAME.PRODUCT_CONTENT_URI,
//    			PROVIDER_NAME.LAST_ACCESS_DATE+"<="+(currentTime-differTime), null);
    	SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
    	try{
    		Cursor cursor = db.query(NAME.TABLE_PRODUCT, new String[]{NAME.PRODUCT_ID},
    				NAME.LAST_ACCESS_DATE+"<="+(currentTime-differTime)+" or "+NAME.LAST_ACCESS_DATE+" IS NULL", null, null, null, null);
    		for(Cursor c;cursor.moveToNext();){
    			c=db.query(NAME.TABLE_QUOTE_PRODUCT, null,
    					NAME.PRODUCT_ID+"="+cursor.getInt(0), null, null, null, null);
    			if(c.getCount()==0){
    				context.getContentResolver().delete(PROVIDER_NAME.PRODUCT_CONTENT_URI, NAME.PRODUCT_ID+"="+cursor.getInt(0), null);
    			}	
    			c.close();
    		}
    		cursor.close();
    	}finally{
    		db.close();
    	}
    }
	
	private void deleteBarcodeSearchResults(Context context,int differDate){
    	long differTime=differDate*24*60*60*1000;//millSecord
    	long currentTime=System.currentTimeMillis();
    	context.getContentResolver().delete(PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI,
    			PROVIDER_NAME.CREATE_DATE+"<="+(currentTime-differTime), null);
    }
}
