package com.koobest.m.supermarket.activities.dlg.forupdateneed;

import java.io.IOException;


import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.dlg.forupdateneed.DialogFactory.OnUpdateStatuListener;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.sync.toolkits.DefaultHandlerFactory;

class DownloadTaskFactory {
	private DownloadTaskFactory(){};
	
	private static abstract class AbstractTask extends  NetworkWithSAXTask<Object, Object>{

		final Context mContext;
        private final OnUpdateStatuListener mOnUpdateStatuListener;
		public AbstractTask(Context context,DialogFactory.OnUpdateStatuListener l) {
			mContext=context;
			mOnUpdateStatuListener=l;
		}
		@Override
		public Context createContext() {
			return mContext;
		}

		@Override
		protected void onPreExecute() {
			if(mOnUpdateStatuListener!=null){
				mOnUpdateStatuListener.onStart();
			}
		}
		
        @Override
        protected void onPostExecute(Bundle result) {
        	if(mOnUpdateStatuListener!=null){
        		if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY, false)){
                    mOnUpdateStatuListener.onFinish(Activity.RESULT_OK);
                    return;
        		}
        		mOnUpdateStatuListener.onFinish(Activity.RESULT_CANCELED);	
        	}
        }	
	}
	
	static class DownloadCurrencyTask extends AbstractTask{
		public DownloadCurrencyTask(Context context,DialogFactory.OnUpdateStatuListener l) {
			super(context, l);
		}
		
		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
        			DefaultHandlerFactory.PARSE_CURRENCYLIST_XML,mContext.getApplicationContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadCurrencyList();
		}	
	}
	
	static class DownloadCustomerTask extends AbstractTask{
		public DownloadCustomerTask(Context context,DialogFactory.OnUpdateStatuListener l) {
	        super(context, l);
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
        			DefaultHandlerFactory.PARSE_CUSTOMERPROFILE_XML,mContext.getApplicationContext(),xmlBuffer);
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadCustomerProfile();
		}		
	}
	
	static class DownloadLengthTask extends AbstractTask{
		public DownloadLengthTask(Context context,DialogFactory.OnUpdateStatuListener l) {
	        super(context, l);
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
        			DefaultHandlerFactory.PARSE_LENGHT_UNITLIST_XML,mContext.getApplicationContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadLengthUnitList();
		}		
	}
	
	static class DownloadWeightTask extends AbstractTask{
		public DownloadWeightTask(Context context,DialogFactory.OnUpdateStatuListener l) {
	        super(context, l);
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
        			DefaultHandlerFactory.PARSE_WEIGHT_UNITLIST_XML,mContext.getApplicationContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadWeightUnitList();
		}		
	}
}
