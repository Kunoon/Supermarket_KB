/**
 * At the first the activity will display the products of table order
 * in our local database.
 * Then we can choose the different order whose information 
 * we want to see .the other we can do something in it(the order we choose) 
 * with the Buttons in the activity 
 * */
package com.koobest.m.supermarket.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.utilities.BaseTabActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;

public class MyQuotesAndOrders extends BaseTabActivity {
	private static String TAG="MyQuotesAndOrders";
	private static final int UNKNOW_WRONG = 6;
//	public final static int REQUEST_CODE_KEY=11233;
	private final static int NETWORK_FAIL_DIALOG = 1;
	public static final int CONNECT_FAIL_DIALOG = 0;
//	private static int[] order_id;
	private static ListView readyOrderList;
	private static ListView requestOrderList;
	private SimpleAdapter readyAdapter;
	private SimpleAdapter requestAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG,"start");
		super.onCreate(savedInstanceState);
		setProgress(802);
		setContentView(R.layout.my_quote_and_order_page);
		
		final TabHost tabhost = getTabHost();
//		tabhost.setup();
//		tabhost.addTab(tabhost.newTabSpec("requestOrder").setIndicator(composeLayout(getResources().getString(R.string.ord_list_tab_untreated))).setContent(R.id.orders_tab_content_1));
//		tabhost.addTab(tabhost.newTabSpec("readyOrder").setIndicator(composeLayout(getResources().getString(R.string.ord_list_tab_history))).setContent(R.id.orders_tab_content_2));
//		tabhost.setPadding(0, -30, 0, 0); 
		tabhost.addTab(tabhost.newTabSpec("requestOrder").setIndicator(composeLayout(getString(R.string.ord_list_tab_untreated))).setContent(R.id.orders_tab_content_1));
		tabhost.addTab(tabhost.newTabSpec("readyOrder").setIndicator(composeLayout(getString(R.string.ord_list_tab_history))).setContent(R.id.orders_tab_content_2));
		
		readyOrderList = (ListView) findViewById(R.id.orders_of_ready);
		requestOrderList = (ListView)findViewById(R.id.orders_of_request);
		if(savedInstanceState!=null&&savedInstanceState.getBoolean("needShowProgress",false)){
			showProgress();
		}
    
		

		/**
		 * the Button of Refresh my quotes and order on Click Event. get the
		 * confirmed order status of this customer from web server
		 */
		
		findViewById(R.id.refresh).setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if(Constants.getIsLoginNeed()){
						showDialog(NEED_LOGIN_DIG);
						return;
					}
					new RefreshQuoteTask().execute();
				}
		});

		/**
		 * the Button of Back to Main onClick Event. finished this activity and
		 * go back to the SupermarketMain Activity
		 * */
		findViewById(R.id.gen_btn_main).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), SupermarketMain.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		findViewById(R.id.gen_btn_back).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
	}
	
    @Override
    protected void onStart() {
    	findViewById(R.id.gen_background).setBackgroundDrawable(SupermarketMain.getBackGround(getApplicationContext()));
    	/**
		 * down load the custom's orders information from table order in local
		 * database, then display it in this activity
		 * */
		new PrepareDataTask().execute();
    	super.onStart();
    }

	/** the following are used to provider display information of this activity */
//	private int[] getArrayOrder_id(int count) {
//		order_id = new int[count];
//		return order_id;
//	}

    private void refreshListViewData(List<Map<String, Object>> data){
    	if (data==null||data.isEmpty()) {
			ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.test_list_item,
					new String[] {getString(R.string.g_noorders_tag)});
			readyOrderList.setAdapter(array_adapter);
			requestOrderList.setAdapter(array_adapter);
		} else {
			final List<Map<String, Object>> readyList = new ArrayList<Map<String,Object>>();
			final List<Map<String, Object>> requestList = new ArrayList<Map<String,Object>>();
			Iterator<Map<String, Object>> iterator = data.iterator();
			while (iterator.hasNext())
			{
				Map<String, Object> tmp = iterator.next();
				System.out.println(tmp.get("order_id")+": "+tmp.get("statu"));
				if (tmp.get("status_id").equals(102))
					readyList.add(tmp);
				else //if (tmp.get("status_id").equals(101))
					requestList.add(tmp);
			}
			readyAdapter = new SimpleAdapter(getBaseContext(), readyList,
					R.layout.of_my_quote_and_order_page,
					new String[] { "describe", "order_id", "statu",
							"data" }, new int[] { R.id.rb_describe,
							R.id.tv_order_id, R.id.tv_Status,
							R.id.tv_Date });
			readyOrderList.setAdapter(readyAdapter);
			requestAdapter = new SimpleAdapter(getBaseContext(), requestList,
					R.layout.of_my_quote_and_order_page,
					new String[] { "describe", "order_id", "statu",
							"data" }, new int[] { R.id.rb_describe,
							R.id.tv_order_id, R.id.tv_Status,
							R.id.tv_Date });
			requestOrderList.setAdapter(requestAdapter);
			
			readyOrderList.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					view.requestFocus();
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), QuoteConfirm.class);
					intent.putExtra(NAME.ORDER_ID, Integer.parseInt((String)readyList.get(position).get("order_id")));
			        startActivity(intent);
				}
			});
			requestOrderList.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					view.requestFocus();
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), QuoteConfirm.class);
					intent.putExtra(NAME.ORDER_ID, Integer.parseInt((String)requestList.get(position).get("order_id")));
			        startActivity(intent);
				}
			});
			
			readyOrderList.setItemsCanFocus(true);
			readyOrderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			requestOrderList.setItemsCanFocus(true);
			requestOrderList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
    }
	

	private void prepareData(List<Map<String, Object>> responseData) {
		Cursor cursor = getContentResolver().query(
				PROVIDER_NAME.ORDER_CONTENT_URI,
				new String[] { PROVIDER_NAME.ORDER_ID,
						PROVIDER_NAME.ORDER_COMMENT, PROVIDER_NAME.STATUS,
						PROVIDER_NAME.ORDER_DATE, PROVIDER_NAME.STATUS_ID},
				PROVIDER_NAME.CUSTOMER_ID + "=" + Constants.getCustomerId(), null,
				"order_id desc");

//		int[] order_id = getArrayOrder_id(cursor.getCount());
		if (cursor.moveToFirst()) {
			//data = new ArrayList<Map<String, Object>>();
			Map<String, Object> item;
			int j = 0;
			do {
				item = new HashMap<String, Object>();
//				order_id[j] = cursor.getInt(0);
				item.put("describe", cursor.getString(1));
				item.put("order_id", String.valueOf(cursor.getInt(0)));
				item.put("statu", cursor.getString(2));
				item.put("data", cursor.getString(3));
				item.put("status_id", cursor.getInt(4));
				responseData.add(item);
			} while (cursor.moveToNext());
			cursor.close();
		} else {
			cursor.close();
		}
	}
	
	private void showProgress(){
		Dialog dialog=(Dialog) readyOrderList.getTag();
		if(dialog==null){
			dialog=ProgressDialog.show(
					MyQuotesAndOrders.this, null,
					getString(R.string.gen_progress_wait), true, true);
			readyOrderList.setTag(dialog);
		}
	}
	
	private void hideProgress(){
		Dialog dialog=(Dialog) readyOrderList.getTag();
		if(dialog!=null&&dialog.isShowing()){
			dialog.dismiss();
		}
		readyOrderList.setTag(null);
	}
	
	private void showDialogOfMine(int id, Bundle args){
		Context context=readyOrderList.getContext();
		Dialog dialog;
		switch(id){
		case CONNECT_FAIL_DIALOG:
				dialog=new AlertDialog.Builder(context).setMessage(getString(R.string.f_dig_connectwrong)).
				setPositiveButton(getString(R.string.gen_btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).create();
				dialog.show();
			break;
		case UNKNOW_WRONG:
				dialog=new AlertDialog.Builder(context).setMessage(getString(R.string.f_dig_unknowwrong)).
				setPositiveButton(getString(R.string.gen_btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				}).create();
				dialog.show();
			break;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(readyOrderList.getTag()!=null){
			outState.putBoolean("needShowProgress",true);
			hideProgress();
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id){
		case UNKNOW_WRONG:
			return new AlertDialog.Builder(this).setMessage(getString(R.string.f_dig_unknowwrong)).
			setPositiveButton(getString(R.string.gen_btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			}).create();
		}
		return super.onCreateDialog(id, args);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG,"intent");
//		super.onNewIntent(intent);
	}
	
	private class PrepareDataTask extends AsyncTask<Object, Object, Boolean>{
		
		private ArrayList<Map<String, Object>> mData=null;

		@Override
		protected Boolean doInBackground(Object... arg0) {
			mData = new ArrayList<Map<String,Object>>();
			prepareData(mData);
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result&&mData!=null){
				refreshListViewData(mData);
			} else{
				refreshListViewData(null);
			}
		}
	}
	
	private class RefreshQuoteTask extends NetworkWithSAXTask<Object, Integer>{
        private ArrayList<Map<String, Object>> mData=null;
        @Override
        public Context createContext() {
			return MyQuotesAndOrders.this;
		}

		@Override
		public DefaultHandler createHandler(byte[] xml_buffer) {
			return DefaultHandlerFactory.createHandler(DefaultHandlerFactory.PARSE_ORDERLIST_XML, getBaseContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException, ResponseException {
			return NetworkUtilities.downLoadOrderList();
		}
		
		protected void onPreExecute() {
			showProgress();
		}

		protected Bundle doInBackground(Object... params) {
			Bundle result=super.doInBackground(params);	
			if(!result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
				return result;
			}
			mData = new ArrayList<Map<String,Object>>();
			prepareData(mData);
			if(mData.isEmpty()){
				result.putBoolean(NetworkWithSAXTask.TASK_RESULT_KEY, false);
				result.putInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, 404);
			}
			return result;					
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			Log.e("refresh","complete");
			hideProgress();
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
				refreshListViewData(mData);
			}else{
				/*if(result.getInt(ResponseException.RESPONSECODE_KEY,-1)!=-1){
					showDialog(NETWORK_FAIL_DIALOG, result);
				} else*/
				if(result.getBoolean("connectwrong", false)){
					showDialogOfMine(CONNECT_FAIL_DIALOG, result);
					return;
				} else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==404){
					Toast.makeText(getBaseContext(), getString(R.string.ord_list_noord_found), Toast.LENGTH_SHORT).show();
					return;
				}
//				else if(result.getBoolean("unknowwrong", false)){
					showDialogOfMine(UNKNOW_WRONG,null);
//				}
			}
		}
	}
}
