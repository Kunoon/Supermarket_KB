package com.koobest.m.supermarket.activities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.quotehandler.EditQuote;
import com.koobest.m.supermarket.activities.quotehandler.OverrideWarnDlg;
import com.koobest.m.supermarket.activities.quotehandler.OverrideWarnDlg.OnBtnClickListener;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.supermarket.toolkits.ofcurrency.GetCurrencyInformation;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.toolkits.parsexml.ParseXml;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.test.MoreAsserts;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class PrewiredContainerDetail extends Activity{
	private final static String TAG="PrewiredContainerDetail";
	public final static String TEMP_ORDER_ID_KEY="key_id";
	private int mTempOrderId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View root=LayoutInflater.from(this).inflate(R.layout.prewired_container_detail, null);		
		setContentView(root);
		Intent intent=getIntent();
		mTempOrderId=intent.getIntExtra(TEMP_ORDER_ID_KEY, -1);
		if(mTempOrderId==-1){
			Log.e(TAG,"Invalid id");
			return;
		}
		new PrepareDataTask(mTempOrderId).execute();
		root.findViewById(R.id.order).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SQLiteDatabase mDatabase= new DatabaseHelper(getApplicationContext()).getReadableDatabase();
				try{
					Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_QUOTES, 
							new String[]{PROVIDER_NAME.QUOTE_ID},
							PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null, null, null);
					if(cursor.getCount()==0){
						cursor.close();
						parseToQuote(mDatabase);
						Intent intent=new Intent(getBaseContext(), EditQuote.class);
						startActivity(intent);
					}else{	
						cursor.close();
						OverrideWarnDlg.builder(PrewiredContainerDetail.this, new OnBtnClickListener() {
							
							@Override
							public void onPositiveBtnClick() {
								SQLiteDatabase mDatabase= new DatabaseHelper(getApplicationContext()).getReadableDatabase();
								try{
									Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_QUOTES, 
											new String[]{PROVIDER_NAME.QUOTE_ID},
											PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null, null, null, null);
									for(int quoteId;cursor.moveToNext();){
										quoteId=cursor.getInt(0);
										mDatabase.delete(NAME.TABLE_QUOTE_PRODUCT, NAME.QUOTE_ID+"="+quoteId, null);
									}
									cursor.close();
									mDatabase.delete(NAME.TABLE_QUOTES, PROVIDER_NAME.CUSTOMER_ID+"="+Constants.getCustomerId(), null);
									parseToQuote(mDatabase);
									Intent intent=new Intent(getBaseContext(), EditQuote.class);
									startActivity(intent);
								}finally{
									mDatabase.close();
								}
								
							}
						}).create().show();
						
					}
				}finally{
					mDatabase.close();
				}
//				Intent intent=new Intent(getBaseContext(), EditQuote.class);
//				startActivity(intent);
//				finish();
			}
		});
		root.findViewById(R.id.gen_btn_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		root.findViewById(R.id.gen_btn_main).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SupermarketMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onStart() {
		findViewById(R.id.gen_background).setBackgroundDrawable(SupermarketMain.getBackGround(getApplicationContext()));
		super.onStart();
	}
    
	private void parseToQuote(SQLiteDatabase database){
		Cursor cursor=database.query(PROVIDER_NAME.TABLE_TEMP_ORDERS, 
				new String[]{PROVIDER_NAME.XML}, PROVIDER_NAME.ORDER_ID+"="+mTempOrderId, null, null, null, null);
		//new File("/data/data/com.koobest.m.supermarket.activities/files", cursor.getString(0));
		try {
			if(cursor.moveToFirst()){
				InputStream is=openFileInput(cursor.getString(0));
				byte[] buffer=new byte[is.available()];
				is.read(buffer);
				ParseXml.handleXmlInSAX(buffer, 
						DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_ORDER_TO_QUOTE, getApplicationContext()));
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			cursor.close();
		}
	}
	
	private void initListViewData(List<Map<String, Object>> data){
		final ListView view = (ListView) findViewById(R.id.pred_product_list);
		if(data!=null&&data.size()!=0){
			SimpleAdapter adapter=new SimpleAdapter(getBaseContext(),
					data, R.layout.of_prewired_container_detaile,
					new String[]{"name","qty","price","total_price","image"}, 
                    new int[]{R.id.pred_procuct_name,R.id.pred_qty,R.id.pred_price,R.id.pred_tprice,R.id.pred_img});
			adapter.setViewBinder(new ViewBinder() {
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if( (view instanceof ImageView)&&(data instanceof byte[]) ) {
						((ImageView)view).setImageBitmap(BitmapFactory.decodeStream(
								new ByteArrayInputStream((byte[])data)));	
					    return true;
					}
					return false;
				}
			});
			view.setAdapter(adapter);
			view.setOnItemClickListener(new OnItemClickListener() {

				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO£º turn to DisplayProductDetail page				
					int productId=(Integer)((HashMap<String, Object>)arg0.getAdapter().getItem(arg2)).get("product_id");
					Intent intent = new Intent(getBaseContext(),DisplayProduct.class);
					intent.putExtra(Constants.CLASS_NAME_KEY, TAG);
					intent.putExtra(DisplayProduct.KEY_POROCUT_ID, productId);
					intent.putExtra(TEMP_ORDER_ID_KEY, mTempOrderId);
					startActivity(intent);
//					finish();
				}
			});
		}else{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.test_list_item,
					new String[] {"no products"});//TODO:string
			view.setAdapter(adapter);
		}
	}
	
	private void initGeneralData(Bundle result){
		((TextView)findViewById(R.id.pred_container_name)).setText(result.getString("order_name"));
		((TextView)findViewById(R.id.pred_container_type)).setText(result.getString("container"));//result.getString("container"));
		((TextView)findViewById(R.id.pred_port)).setText(result.getString("port"));
		((TextView)findViewById(R.id.pred_zone)).setText(result.getString("zone"));
		((TextView)findViewById(R.id.pred_brand)).setText(result.getString("brand"));
	}
	
	private class PrepareDataTask extends AsyncTask<Object, Bundle, Bundle>{
		private SQLiteDatabase mDataBase;
		private List<Map<String, Object>> mListViewData;
        private final int mOrderId;
        private double mExchangeRateTobase=1;
    	private String mCurrencySymbol=null;
    	private boolean isLeftSymbol=true;
    	public DecimalFormat mDecimalFormat;
		public PrepareDataTask(int orderId) {
			mOrderId=orderId;
			mDecimalFormat=new DecimalFormat("######0.00");
		}
		@Override
		protected Bundle doInBackground(Object... params) {
			try{
				mDataBase=new DatabaseHelper(getApplicationContext()).getReadableDatabase();
				Bundle data = GetCurrencyInformation.getRateAndSymbol(getApplicationContext());
			    if(data!=null){
					isLeftSymbol=data.getBoolean(GetCurrencyInformation.IS_LEFT_SYMBOL,true);
					mCurrencySymbol=data.getString(GetCurrencyInformation.SYMBOL);
					mExchangeRateTobase=data.getDouble(GetCurrencyInformation.EXCHANGE_RATE_KEY, 1);
				}else{
					mCurrencySymbol=getString(R.string.default_currencysymbol);
				}
			    prepareProductListData();
				return prepareGeneralData();
			}finally{
				mDataBase.close();
			}
		}		
		
		private void prepareProductListData() {
			Cursor cursor = mDataBase.query(PROVIDER_NAME.TABLE_TEMP_ORDER_PRODUCTS,
					new String[]{PROVIDER_NAME.NAME,PROVIDER_NAME.PRICE,PROVIDER_NAME.QTY,PROVIDER_NAME.QTY_UNIT,PROVIDER_NAME.IMAGE,PROVIDER_NAME.PRODUCT_ID},
					PROVIDER_NAME.ORDER_ID+"="+mOrderId, null, null, null, null);
			if(cursor.getCount()>0){
				mListViewData=new ArrayList<Map<String,Object>>(cursor.getCount());
				HashMap<String, Object> item;
				int qty;
				double price;
				for(;cursor.moveToNext();){
					item=new HashMap<String, Object>();
					item.put("name", cursor.getString(0));
					qty=cursor.getInt(2);
					item.put("qty", qty+cursor.getString(3));
					price=cursor.getDouble(1);
					if(isLeftSymbol){
						item.put("price", mCurrencySymbol+mDecimalFormat.format(mExchangeRateTobase*price));
						item.put("total_price", mCurrencySymbol+mDecimalFormat.format(mExchangeRateTobase*price*qty));
					}else{
						item.put("price", mDecimalFormat.format(mExchangeRateTobase*price)+mCurrencySymbol);
						item.put("total_price", mDecimalFormat.format(mExchangeRateTobase*price*qty)+mCurrencySymbol);
					}
					item.put("image", cursor.getBlob(4));
					item.put("product_id", cursor.getInt(5));
					mListViewData.add(item);
				}
			}
			cursor.close();
			Bundle progress=new Bundle();
			progress.putBoolean("is_listview_data_prepared", true);
			publishProgress(progress);
		}

		@Override
		protected void onProgressUpdate(Bundle... values) {
			if(values[0].getBoolean("is_listview_data_prepared", false)){
				initListViewData(mListViewData);
			}
		}
		
		private Bundle prepareGeneralData() {
			//TODO:
			Cursor cursor = mDataBase.query(PROVIDER_NAME.TABLE_TEMP_ORDER_DETAIL_FACETS, 
					new String[]{PROVIDER_NAME.NAME,PROVIDER_NAME.VALUE},
					PROVIDER_NAME.ORDER_ID+"="+mOrderId, null, null, null, null);
			StringBuilder port_builder=new StringBuilder("");
			StringBuilder zone_builder=new StringBuilder("");
			StringBuilder brand_builder=new StringBuilder("");
			while(cursor.moveToNext()){
				if(cursor.getString(0).equalsIgnoreCase("port")){
					if(port_builder.length()!=0){
						port_builder.append(",");
					}
					port_builder.append(cursor.getString(1));
				}else if(cursor.getString(0).equalsIgnoreCase("zone")){
					if(zone_builder.length()!=0){
						zone_builder.append(",");
					}
					zone_builder.append(cursor.getString(1));
				}else if(cursor.getString(0).equalsIgnoreCase("brand")){
					if(brand_builder.length()!=0){
						brand_builder.append(",");
					}
					brand_builder.append(cursor.getString(1));
				}
			}
			cursor.close();
			Bundle result = new Bundle();
			result.putString("port", port_builder.toString());
			result.putString("zone", zone_builder.toString());
			result.putString("brand", brand_builder.toString());
			cursor=mDataBase.query(PROVIDER_NAME.TABLE_TEMP_ORDERS,
					new String[]{PROVIDER_NAME.CONTAINER_ID,PROVIDER_NAME.NAME}, 
					PROVIDER_NAME.ORDER_ID+"="+mOrderId, null, null, null, null);
			if(cursor.moveToFirst()){
				int container_id=cursor.getInt(0);
				result.putString("order_name", cursor.getString(1));
				cursor.close();
				cursor=getContentResolver().query(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI,
						new String[]{SYNC_PROVIDER_NAME.NAME},
						SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID+"="+container_id, null, null);
				if(cursor.moveToFirst()){
					System.out.println(cursor.getString(0));
					result.putString("container", cursor.getString(0));
				}
			}
			cursor.close();
			result.putBoolean("result", true);
			return result;
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean("result", false)){
				initGeneralData(result);
			}
		}
	}
}
