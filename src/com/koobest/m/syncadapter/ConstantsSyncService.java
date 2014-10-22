package com.koobest.m.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConstantsSyncService extends Service{
	private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter asSyncAdapter = null;
	@Override
	public IBinder onBind(Intent arg0) {
		return  asSyncAdapter.getSyncAdapterBinder();
	}
	
	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
	        if (asSyncAdapter == null) {
	            asSyncAdapter = new SyncAdapter(this,true);
	        }
	    }
	} 
}
