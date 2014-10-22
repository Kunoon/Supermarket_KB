package com.koobest.m.supermarket.toolkits.aboutprice;

import java.io.IOException;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.DefaultHandlerProductsPrice;

public class UpdateProductPriceTask extends NetworkWithSAXTask<Object, Object>{
	private final Context mContext;
	private int mProductIds[];
	private OnTaskStautChangeListener mOnTaskStautChangeListener=null;
	public interface OnTaskStautChangeListener{
		void onStart();
		void onFinished(Bundle result);
	}
	
    /**
     * need an activity context
     * @param context an activity context
     * @param productIds[] if productIds is null price 
     *                     of all products in local will be update
     */
	public UpdateProductPriceTask(Context context,int productIds[],OnTaskStautChangeListener l) {
		mContext=context;
		mProductIds=productIds;
		mOnTaskStautChangeListener=l;
	}
	
	public UpdateProductPriceTask(Context context,OnTaskStautChangeListener l) {
		this(context,null,l);
	}
	
	@Override
	public Context createContext() {
		return mContext;
	}

	@Override
	protected DefaultHandler createHandler(byte[] xmlBuffer) {
		return new DefaultHandlerProductsPrice(mContext,mProductIds);
	}

	@Override
	protected byte[] upOrDownloadFromNet() throws IOException,
			ResponseException {
		StringBuffer product_list=new StringBuffer();
		if(mProductIds!=null){
			for(int productId:mProductIds){
				product_list.append(";"+productId);		
			}
		}else {
			Cursor cursor=mContext.getContentResolver().query(
					PROVIDER_NAME.PRODUCT_CONTENT_URI,
					new String[]{PROVIDER_NAME.PRODUCT_ID},
					null,null,null);
			if(cursor.getCount()==0){
				cursor.close();
				throw new IOException("no product");
			}
			mProductIds=new int[cursor.getCount()];
			while(cursor.moveToNext()){
				product_list.append(";"+cursor.getInt(0));
				mProductIds[cursor.getPosition()]=cursor.getInt(0);
			}
			cursor.close();
		}
		return NetworkUtilities.downLoadPriceList(
	    		product_list.deleteCharAt(0).toString());
	}
	
	@Override
	protected void onPreExecute() {
		if(mOnTaskStautChangeListener!=null){
			mOnTaskStautChangeListener.onStart();
		}
	}

	@Override
	protected void onPostExecute(Bundle result) {
		if(mOnTaskStautChangeListener!=null){
			mOnTaskStautChangeListener.onFinished(result);
		}
	}
}
