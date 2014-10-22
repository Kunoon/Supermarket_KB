package com.koobest.m.supermarket.activities.productsearch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.DisplayProduct;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.ofcurrency.CurrencyNote;

public abstract class BaseSearchTask extends NetworkWithSAXTask<Object, Object>{
	private View mDialogView;
	private final Activity mActivity;
    private ListAdapter mAdapter;
    private Bundle mParseResult;
    private ImageLoadManager mImageManager;
    private Bundle mPage;
	public BaseSearchTask(Activity activity,Bundle page) {
		mActivity=activity.getParent()!=null?activity.getParent():activity;
		mPage=page;
	}
	
	protected abstract BaseSearchTask  createNewSearchTask();

	
	@Override
	protected void onPreExecute() {
		
		((SearchProductTab)mActivity).showProgress();
	}
	
	public Context createContext() {
		return mActivity;
	}

	@Override
	public DefaultHandler createHandler(byte[] xml_buffer) {
		mParseResult=new Bundle();
		return DefaultHandlerFactory.
        createProductlistHandler(mActivity.getApplicationContext(), mParseResult);
	}
	
	@Override
	protected Bundle doInBackground(Object... params) {
		Bundle result = super.doInBackground(params);
		if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
			prepareDialogData(mParseResult);
		}
		return result;
	}
	
    private void prepareDialogData(Bundle args){
    	CurrencyNote currencyNote = ((SearchProductTab)mActivity).maybeCreateCurrencyNote();
    	final int pageLimitNum=args.getInt("page_limit",10),//the max number products each page could have
        productTotalNum=args.getInt("number"),
        pageIndex=args.getInt("page_index");
		int productPageNum=((productPageNum=productTotalNum-(pageIndex-1)*pageLimitNum)<pageLimitNum)?(productPageNum):pageLimitNum;
		Log.e("currency ","total count:"+productTotalNum+",page index:"+pageIndex+",product page num:"+productPageNum+",pageLimitNum:"+pageLimitNum);
		
		String name[]=args.getStringArray("name"),
		       manufacturer[]=args.getStringArray("manufacturer"),
		       image[]=args.getStringArray("image");
		double price[]=args.getDoubleArray("price");
		args.remove("name");
		args.remove("manufacturer");
		args.remove("image");
		args.remove("price");
		List<Map<String, Object>> adapterData=new ArrayList<Map<String, Object>>();
		Map<String, Object> item ;
		for(int i=0;i<productPageNum;i++){
			item = new HashMap<String, Object>(4);
			item.put("name", name[i]);
			item.put("summary", manufacturer[i]);
			if(currencyNote.isLeftSymbol){
				item.put("price", currencyNote.currencySymbol+(currencyNote.decimalFormat.format(price[i]*currencyNote.exchangeRateTobase)));
			}else{
				item.put("price", (currencyNote.decimalFormat.format(price[i]*currencyNote.exchangeRateTobase))+currencyNote.currencySymbol);
			}
//			try {
			item.put("imgAddress", image[i]);
//				item.put("imgAddress", NetworkUtilities.downloadImage(image[i]));
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ResponseException e) {
//				e.printStackTrace();
//			}
			adapterData.add(item);	
		}
		
		mAdapter = new SimpleAdapter(mActivity.getApplicationContext(), adapterData,
				R.layout.of_search_sub_list, new String[] { "name", "summary", "price",
				"imgAddress" }, new int[] { R.id.search_name,
						R.id.search_summary, R.id.search_price,
						R.id.search_image });
    }
    
    @Override
	protected void onPostExecute(Bundle result) {
		if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
			int count=mParseResult.getInt("number");
			Log.e("product", "count:"+count);
			if(count==1){
				Intent intent = new Intent();
    			intent.putExtra(PROVIDER_NAME.PRODUCT_ID, mParseResult.getIntArray("product_id")[0]);
    			intent.setClass(mActivity,
    					DisplayProduct.class);
    			((SearchProductTab)mActivity).hideProgress();
				mActivity.startActivity(intent);
				
			}else{
				if(!mActivity.isFinishing()){
					initViewData();
					mActivity.showDialog(SearchProductTab.SHOW_PRODUCTLIST_DIALOG, null);
				}
			}
			return;
		} 
		
		((SearchProductTab)mActivity).hideProgress();
		if(mActivity.isFinishing()){
			return;
		}
		if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==404){
			Toast.makeText(mActivity,
					mActivity.getString(R.string.pro_sear_adv_noproducts), 
					Toast.LENGTH_SHORT).show();
		} else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==401){
			//TODO:show dialog
			//showDialog(id, args)
		} else if(result.getBoolean(NetworkWithSAXTask.CONNECT_TIME_OUT, false)){
			Toast.makeText(mActivity, mActivity.getString(R.string.gen_dlg_net_timeout), Toast.LENGTH_SHORT).show();
		}
	}
    
    private void initViewData(){
    	mDialogView=((SearchProductTab)mActivity).maybeCreateListView();
    	mImageManager=new ImageLoadManager(27);
    	((SimpleAdapter)mAdapter).setViewBinder(new MyImageViewBinder());
    	final int productId[]=mParseResult.getIntArray("product_id");
//    	Log.e(TAG,""+(mDialogView==null));
    	((ListView)mDialogView.findViewById(R.id.lv_productlist)).setAdapter(mAdapter);
		((ListView)mDialogView.findViewById(R.id.lv_productlist)).setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
    			intent.putExtra(PROVIDER_NAME.PRODUCT_ID, Integer.valueOf(productId[arg2]));
    			intent.setClass(mActivity,DisplayProduct.class);
//    			AdvancedSearchProduct.this.dismissDialog(SHOW_PRODUCTLIST_DIALOG);
				mActivity.startActivity(intent);		
//				finish();
			}
		});
		final int pageLimitNum=mParseResult.getInt("page_limit",10),//the max number products each page could have
        productTotalNum=mParseResult.getInt("number"),
        pageIndex=mParseResult.getInt("page_index");
		
		if(productTotalNum<=pageLimitNum){
			((Button)mDialogView.findViewById(R.id.btn_next)).setVisibility(View.GONE);
			((Button)mDialogView.findViewById(R.id.btn_pre)).setVisibility(View.GONE);
		}else{
			((Button)mDialogView.findViewById(R.id.btn_next)).setVisibility(View.VISIBLE);
			((Button)mDialogView.findViewById(R.id.btn_pre)).setVisibility(View.VISIBLE);
		}
		
		if(pageIndex<(productTotalNum-1)/pageLimitNum+1){
			
			((Button)mDialogView.findViewById(R.id.btn_next)).setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					mPage.putInt("page", mPage.getInt("page")+1);
					createNewSearchTask().execute();
				}
			});
			((Button)mDialogView.findViewById(R.id.btn_next)).setEnabled(true);
		} else{
			((Button)mDialogView.findViewById(R.id.btn_next)).setEnabled(false);
		}
		Log.w("page_index","page:"+pageIndex);
		if(pageIndex>1){
			((Button)mDialogView.findViewById(R.id.btn_pre)).setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					mPage.putInt("page", mPage.getInt("page")-1);
					createNewSearchTask().execute();
				}
			});
			((Button)mDialogView.findViewById(R.id.btn_pre)).setEnabled(true);
		} else{
			((Button)mDialogView.findViewById(R.id.btn_pre)).setEnabled(false);
		}
    }
    
    private class MyImageViewBinder implements ViewBinder {
		public boolean setViewValue(View view, Object data,
		String textRepresentation) {
			if( (view instanceof ImageView)&&(data instanceof String) ) {
				view.setTag(data);
				Drawable drawable = mImageManager.loadDrawable((String)data, new Callback() {  
					public void onImageLoaded(Drawable drawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) mDialogView.findViewWithTag(imageUrl);  
	                    if (imageViewByTag != null) {  
	                        imageViewByTag.setImageDrawable(drawable);  
	                    } 
					}  
	            }); 
				if(drawable!=null){
					((ImageView)view).setImageDrawable(drawable);  
				}else{
					((ImageView)view).setImageDrawable(null); //TODO set a default image
				}
			    return true;
			}
		    return false;
		}
	}	
    
    private class ImageLoadManager{
    	
    	private HashMap<String, SoftReference<Drawable>> imageCache;
    	private Handler mHandler = new Handler();	    	
    	public ImageLoadManager(int capacity) {
			imageCache = new HashMap<String, SoftReference<Drawable>>(capacity);
		}
    	public Drawable loadDrawable(final String imageUrl, final Callback callback) {
            if (imageCache.containsKey(imageUrl)) {
                SoftReference<Drawable> softReference = imageCache.get(imageUrl);
                Drawable drawable = softReference.get();
                if (drawable != null) {
                    return drawable;
                }
            }
            new Thread() {
                @Override
                public void run() {
					try {
						final Drawable drawable = new BitmapDrawable(
								new ByteArrayInputStream((byte[])NetworkUtilities.downloadImage(imageUrl)));
//                    	loadImageFromUrl(imageUrl);
	                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
//	                    Message message = handler.obtainMessage(0, drawable);
//	                    handler.sendMessage(message);
	                    mHandler.post(new Runnable() {
							
							public void run() {
								callback.onImageLoaded(drawable, imageUrl);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ResponseException e) {
						e.printStackTrace();
					}
                }
            }.start();
            return null;
        }
    } 
    
    private interface Callback{
		void onImageLoaded(Drawable drawable,String imageUrl);
	}
}
