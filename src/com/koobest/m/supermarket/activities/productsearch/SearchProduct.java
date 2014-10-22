package com.koobest.m.supermarket.activities.productsearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.DisplayProduct;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.activities.R.id;
import com.koobest.m.supermarket.activities.R.layout;
import com.koobest.m.supermarket.activities.R.string;
import com.koobest.m.supermarket.activities.utilities.BaseActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.ofcurrency.GetCurrencyInformation;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

public class SearchProduct extends BaseActivity {
	private static String TAG="SearchProduct";
	// Indicate barcode recognition failure
	private static final int RESULT_FAILED = RESULT_FIRST_USER + 1;
	// Request code to call barcode recognition activity
	private static final int REQUEST_CODE_BARCODE_RECOGNITION = 1;
	private static final String BARCODE_SCAN_ACTION = "com.koobest.barcode.BarcodeScan";
	private static final String BARCODE_SCAN_EXPECT_RESULT = "com.koobest.barcode.returnResult";
	private static final String BARCODE_SCAN_RESULT = "com.koobest.barcode.returnedBarcode";
	
	private static final int SHOW_PRODUCTLIST_DIALOG=0;
	protected static final int NETCONNECT_RESPONSE_MSG = 0;
    private EditText barcode_tv;
    private double mExchangeRateTobase=1;
	private String mCurrencySymbol=null;
	private boolean isLeftSymbol=true;
	
    private Dialog mDialog,productlistDialog;
    private Bundle mPage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_product_page);
		Log.e(TAG, "start");
		barcode_tv = (EditText) findViewById(R.id.bar_code);
        new PrepareBrandDataTask().execute();
		/**
		 * the Button of Scan on Click Event. turn to the Scan Activity which
		 * come from other application
		 */
		Log.e("scan null",String.valueOf(findViewById(R.id.btn_scan)==null));
		findViewById(R.id.btn_scan).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent newIntent;
				newIntent = new Intent(BARCODE_SCAN_ACTION);
				newIntent.putExtra(BARCODE_SCAN_EXPECT_RESULT, true);
				try{
					startActivityForResult(newIntent,
							REQUEST_CODE_BARCODE_RECOGNITION);
				}catch (ActivityNotFoundException e) {
					Toast.makeText(getBaseContext(), "action not be found", Toast.LENGTH_SHORT).show();
				}
				
			}
		});

		/**
		 * the Button of Search on Click Event. turn to Display product
		 * Activity, while we need take the bar code to
		 * */
		findViewById(R.id.search).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final String barcode = barcode_tv.getText().toString().trim();
				if (barcode.length() < 13) {
					Toast.makeText(getBaseContext(),
							getString(R.string.pro_sear_sim_wrongbarcode),
							Toast.LENGTH_SHORT).show();
				} else {
			        search(barcode);
				}
			}
		});


	}

	private void search(final String barcode) {
		if(searchInLocalDB(barcode)){
			return;
		}
		if(Constants.getIsLoginNeed()){
			showDialog(NEED_LOGIN_DIG);
            return;
		}
		showProgress();
		new SearchWithBarcodeTask(barcode).execute();
	}
	
	private void maybeGetCurrencyInform(){
		if(mCurrencySymbol==null){
			Bundle data = GetCurrencyInformation.getRateAndSymbol(getApplicationContext());
		    if(data!=null){
				isLeftSymbol=data.getBoolean(GetCurrencyInformation.IS_LEFT_SYMBOL,true);
				mCurrencySymbol=data.getString(GetCurrencyInformation.SYMBOL);
				mExchangeRateTobase=data.getDouble(GetCurrencyInformation.EXCHANGE_RATE_KEY, 1);
			}else{
				mCurrencySymbol=getString(R.string.default_currencysymbol);
			}
		}
	}
	/**
	 * search product in local database,while only one is correspond,
	 * Display Activity will be started
	 * @param barcode
	 * @return
	 */
	private boolean searchInLocalDB(String barcode){
		Cursor cursor = getContentResolver().query(PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI, 
				new String[]{PROVIDER_NAME.PRODUCT_NUM,PROVIDER_NAME.PRODUCT_IDS},
				PROVIDER_NAME.BARCODE+"="+barcode, null, null);
        if(cursor.moveToFirst()){
        	if(cursor.getInt(0)==1){
    			cursor.moveToFirst();
    			Log.e(TAG,"start display product activity");
    			final int product_id=Integer.valueOf(cursor.getString(1));
    			cursor.close();
    			Intent intent = new Intent();
    			intent.putExtra(PROVIDER_NAME.PRODUCT_ID, product_id);
    			intent.setClass(getBaseContext(),
    					DisplayProduct.class);
				SearchProduct.this.startActivity(intent);
//    			finish();
    			return true;
    		} else if(cursor.getInt(0)>1){
    			
    			//TODO show product list
    			//!-------´úÌæ need delete-------
//    			final int product_id=Integer.valueOf(cursor.getString(1));
//    			cursor.close();
//    			Intent intent = new Intent();
//    			intent.putExtra(PROVIDER_NAME.PRODUCT_ID, product_id);
//    			intent.setClass(getBaseContext(),
//    					DisplayProduct.class);
//				SearchProduct.this.startActivity(intent);
				//---------------------------
    			maybeGetCurrencyInform();
    			cursor.close();
    			Bundle data = new Bundle();
    			data.putString("barcode", barcode);
    			showDialog(SHOW_PRODUCTLIST_DIALOG, data);
    			return true;
    		}
        } 
		cursor.close();
        return false;
	}
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_BARCODE_RECOGNITION:
			if (resultCode == Activity.RESULT_OK) {
				final String barcode = data.getStringExtra(BARCODE_SCAN_RESULT)
						.trim();
				barcode_tv.setText(barcode);
				if (barcode.length() != 13) {
					Toast.makeText(getBaseContext(),
							getString(R.string.pro_sear_sim_wrongbarcode),
							Toast.LENGTH_SHORT).show();
				} else {
					new SearchWithBarcodeTask(barcode).execute();
				}
			} else if (resultCode == RESULT_FAILED) {
				Toast.makeText(this, getString(R.string.pro_sear_sim_barcodecatchfail),
						Toast.LENGTH_SHORT).show();
			}
			return;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
 
	
	private void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					SearchProduct.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	private void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}

	protected Dialog onCreateDialog(int id, Bundle args) {
        switch(id){
        case SHOW_PRODUCTLIST_DIALOG:
        	productlistDialog=createProductListDialog(args.getString("barcode"));
        	productlistDialog.setOnDismissListener(new OnDismissListener() {
				
				public void onDismiss(DialogInterface arg0) {
					removeDialog(SHOW_PRODUCTLIST_DIALOG);
				}
			});
        	return productlistDialog;
        }
        return super.onCreateDialog(id, args);
	}
	
	private Dialog createProductListDialog(String barcode) {
		DecimalFormat decimalFormat = new  DecimalFormat("######0.00");
		Cursor cursor = getContentResolver().query(PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI, 
				new String[]{PROVIDER_NAME.PRODUCT_NUM,PROVIDER_NAME.PRODUCT_IDS},
				PROVIDER_NAME.BARCODE+"="+barcode, null, null);
		if(cursor.getCount()==0){
			cursor.close();
			return null;
		}
		cursor.moveToFirst();
		int productNum=cursor.getInt(0);
		final String productId[]=cursor.getString(1).split(",");
		cursor.close();
		
		Log.e("currency ","count"+cursor.getCount());
		String projection[]=new String[]{PROVIDER_NAME.PRODUCT_NAME,PROVIDER_NAME.MANUFACTURER,PROVIDER_NAME.PRICE,PROVIDER_NAME.IMAGE},
		       selection=PROVIDER_NAME.PRODUCT_ID+"=?",
		       selectionArgs[] = new String[1];
		List<Map<String, Object>> adapterData=new ArrayList<Map<String, Object>>();
		Map<String, Object> item ;
		for(int i=0;i<productNum;i++){
			selectionArgs[0] = productId[i];
			cursor = getContentResolver().query(PROVIDER_NAME.PRODUCT_DESC_CONTENT_URI,
					projection, selection, selectionArgs, null);
			if(cursor.moveToFirst()){
				item = new HashMap<String, Object>(4);
				item.put("name", cursor.getString(0));
				item.put("summary", cursor.getString(1));
				if(isLeftSymbol){
					item.put("price", mCurrencySymbol+decimalFormat.format((cursor.getDouble(2)*mExchangeRateTobase)));
				}else{
					item.put("price", decimalFormat.format((cursor.getDouble(2)*mExchangeRateTobase))+mCurrencySymbol);
				}
				item.put("imgAddress", cursor.getString(3));
				adapterData.add(item);
			}
			cursor.close();
		}
		
        DialogInterface.OnClickListener listButClickListner=new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, final int selectedIndex) {
				Intent intent = new Intent();
    			intent.putExtra(PROVIDER_NAME.PRODUCT_ID, Integer.valueOf(productId[selectedIndex]));
    			intent.setClass(getBaseContext(),
    					DisplayProduct.class);
    			dialog.dismiss();
				SearchProduct.this.startActivity(intent);		
//				finish();
			}
		};
		
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), adapterData,
				R.layout.of_search_sub_list, new String[] { "name", "summary", "price",
				"imgAddress" }, new int[] { R.id.search_name,
						R.id.search_summary, R.id.search_price,
						R.id.search_image });
		adapter.setViewBinder(new MyImageViewBinder());
		return new AlertDialog.Builder(this).setTitle(getString(R.string.b1_dialog_tag)).
	    setAdapter(adapter,listButClickListner).create();
	}
	
	private class MyImageViewBinder implements ViewBinder {
		public boolean setViewValue(View view, Object data,
		String textRepresentation) {
			if( (view instanceof ImageView)&&(data instanceof String) ) {
			    try {
					((ImageView)view).setImageBitmap(BitmapFactory.decodeStream(openFileInput((String)data)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			    return true;
			}
		    return false;
		}
	}
	
	class SearchWithBarcodeTask extends NetworkWithSAXTask<Object, Integer>{
		private final String mBarcode;
		
		public SearchWithBarcodeTask(String barcode) {
			mBarcode=barcode;
		}
		@Override
		public Context createContext() {
			return SearchProduct.this;
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.
	        		createProductlistHandler(getApplicationContext(), Long.valueOf(mBarcode));
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downLoadProductList(String.valueOf(mBarcode));
		}
		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected Bundle doInBackground(Object... params) {
			Bundle result=super.doInBackground(params);
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY, false)){
				Cursor cursor = getContentResolver().query(PROVIDER_NAME.BARCODE_PRODUCT_CONTENT_URI, 
						new String[]{PROVIDER_NAME.PRODUCT_NUM},
						PROVIDER_NAME.BARCODE+"="+mBarcode, null, null);
				if(cursor.getCount()>1){
					maybeGetCurrencyInform();
				}
				cursor.close();
			}	
			return result;
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			hideProgress();
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
				searchInLocalDB(mBarcode);
			}else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==404){
				Toast.makeText(getBaseContext(),
						getString(R.string.pro_sear_sim_noproduct), 
						Toast.LENGTH_SHORT).show();
			}else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==401){
				//TODO:show dialog
			}
		}
	}
	
	private class PrepareBrandDataTask extends AsyncTask<Object, Object, Object>{
        SimpleAdapter mAdapter;
		@Override
		protected Object doInBackground(Object... params) {
			Cursor cursor=getContentResolver().query(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.BRAND_ID,SYNC_PROVIDER_NAME.NAME}, null, null,  SYNC_PROVIDER_NAME.NAME+" ASC");
			try{
				List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
				Map<String, Object> tempItem=new HashMap<String, Object>();
				tempItem.put(SYNC_PROVIDER_NAME.NAME, getString(R.string.gen_empty_selection));
				data.add(tempItem);
				if(cursor.getCount()>0){
					while(cursor.moveToNext()&&cursor.getInt(0)!=9999){
					    tempItem=new HashMap<String, Object>(4);
					    tempItem.put(SYNC_PROVIDER_NAME.BRAND_ID, cursor.getInt(0));
					    tempItem.put(SYNC_PROVIDER_NAME.NAME, cursor.getString(1));
					    data.add(tempItem);
					}
				}
				mAdapter = new SimpleAdapter(getBaseContext(), data, R.layout.my_spinner, new String[]{SYNC_PROVIDER_NAME.NAME}, new int[]{android.R.id.text1});
				mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				return null;
			}finally{
				cursor.close();
			}
		}
		
		@Override
		protected void onPostExecute(Object result) {
			((Spinner)findViewById(R.id.sp_simple_brand)).setAdapter(mAdapter);
			((Spinner)findViewById(R.id.sp_simple_brand)).setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(mPage==null){
						mPage=new Bundle();
					}
					mPage.putInt("page", 1);
					if(arg2>0){
						Map<String, Object> item = (Map<String, Object>) ((Spinner)findViewById(R.id.sp_simple_brand)).getSelectedItem();
						new SearchWithBrandTask((Integer)item.get(SYNC_PROVIDER_NAME.BRAND_ID)).execute();
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					Log.e("asdf","asdf");
					if(mPage==null){
						mPage=new Bundle();
						mPage.putInt("page", 1);
					}
					int selectItemId=((Spinner)findViewById(R.id.sp_simple_brand)).getSelectedItemPosition();
					if(selectItemId>0){
						Map<String, Object> item = (Map<String, Object>) ((Spinner)findViewById(R.id.sp_simple_brand)).getItemAtPosition(selectItemId);
						new SearchWithBrandTask((Integer)item.get(SYNC_PROVIDER_NAME.BRAND_ID)).execute();
					}
				}
			});
			findViewById(R.id.brand_search).setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int selectItemId=((Spinner)findViewById(R.id.sp_simple_brand)).getSelectedItemPosition();
					if(selectItemId>0){
						Map<String, Object> item = (Map<String, Object>) ((Spinner)findViewById(R.id.sp_simple_brand)).getItemAtPosition(selectItemId);
						new SearchWithBrandTask((Integer)item.get(SYNC_PROVIDER_NAME.BRAND_ID)).execute();
					}else{
						Toast.makeText(getBaseContext(), getString(R.string.pro_sear_sim_nobrand), Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	} 
	
	private class SearchWithBrandTask extends BaseSearchTask{
		private final int mBrandId;

		public SearchWithBrandTask(int brandId) {
			super(getParent(), mPage);
			mBrandId=brandId;
		}

		@Override
		protected BaseSearchTask createNewSearchTask() {
			return new SearchWithBrandTask(mBrandId);
		}

		@Override
		protected byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downLoadProductListByBrand(String.valueOf(mBrandId),mPage.getInt("page"));
		}
	}
}
