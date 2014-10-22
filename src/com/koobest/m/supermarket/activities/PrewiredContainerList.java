package com.koobest.m.supermarket.activities;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.utilities.BaseActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.contentprovider.PROVIDER_NAME;
import com.koobest.m.supermarket.database.DatabaseHelper;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.supermarket.network.NetworkWithSAXTask;
import com.koobest.m.supermarket.toolkits.DefaultHandlerFactory;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class PrewiredContainerList extends BaseActivity{
	private String LAST_SEARCH_PORT=null;
	private String LAST_SEARCH_ZONE=null;
	private String LAST_SEARCH_BRAND=null;
	private WeakReference<Spinner> sp_port;
	private WeakReference<Spinner> sp_zone;
	private WeakReference<Spinner> sp_brand;
	private WeakReference<ListView> mListViewRef;
	private Dialog mDialog;
	private int mSpinnerLayout = R.layout.my_spinner;
	private OnItemSelectedListener sp_listener=new OnItemSelectedListener() {

		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			switch(arg0.getId()){
			case R.id.sp_port:
				LAST_SEARCH_PORT=((ArrayAdapter<String>)arg0.getAdapter()).getItem(arg2);
				//System.out.println("port click port:"+LAST_SEARCH_PORT);
				break;
			case R.id.sp_zone:
				LAST_SEARCH_ZONE=((ArrayAdapter<String>)arg0.getAdapter()).getItem(arg2);
				break;
			case R.id.sp_brand:
				LAST_SEARCH_BRAND=((ArrayAdapter<String>)arg0.getAdapter()).getItem(arg2);
				break;
			}
			new PrepareDataTask(false).execute();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v=LayoutInflater.from(this).inflate(R.layout.prewired_container_list, null);
		setContentView(v);
		new PrepareDataTask(true).execute();
		sp_port=new WeakReference<Spinner>((Spinner) v.findViewById(R.id.sp_port));
		sp_zone=new WeakReference<Spinner>((Spinner) v.findViewById(R.id.sp_zone));
		sp_brand=new WeakReference<Spinner>((Spinner) v.findViewById(R.id.sp_brand));
		mListViewRef=new WeakReference<ListView>((ListView) v.findViewById(R.id.lv_serachresult));
		v.findViewById(R.id.refresh).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(Constants.getIsLoginNeed()){
					showDialog(NEED_LOGIN_DIG);
		            return;
				}
				new RefreshTask().execute();
			}
		});
		v.findViewById(R.id.gen_btn_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
        findViewById(R.id.gen_btn_main).setOnClickListener(new OnClickListener() {
			
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
	
	private void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					PrewiredContainerList.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	private void hideProgress(){
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
	
	private void refreshListViewData(List<Map<String, Object>> data){
		if(data!=null&&data.size()!=0){
			SimpleAdapter adapter =new SimpleAdapter(getBaseContext(),
					data, R.layout.of_prewired_container_list_item, 
					new String[]{"name","container_name","end_date"},
					new int[]{R.id.name,R.id.container_name,R.id.end_date});
			mListViewRef.get().setAdapter(adapter);
			mListViewRef.get().setOnItemClickListener(new OnItemClickListener() {

				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Object id= ((HashMap<String, Object>)arg0.getAdapter().getItem(arg2)).get("id");
					if(id!=null){
						SQLiteDatabase database=new DatabaseHelper(getBaseContext()).getReadableDatabase();
						Cursor cursor=database.query(PROVIDER_NAME.TABLE_TEMP_ORDERS, 
								new String[]{PROVIDER_NAME.ORDER_ID}, 
								PROVIDER_NAME.ORDER_ID+"="+(Integer)id+" and "+PROVIDER_NAME.XML+" IS NOT NULL",
								null,null, null, null);
						if(cursor.moveToFirst()){
							//System.out.println("not null");
							cursor.close();
							database.close();
							//TODO:turn to template oreder detail page
							Intent intent = new Intent(getBaseContext(), PrewiredContainerDetail.class);
							intent.putExtra(PrewiredContainerDetail.TEMP_ORDER_ID_KEY, (Integer)id);
							startActivity(intent);
//							finish();
						}else{
							cursor.close();
							database.close();
							if(Constants.getIsLoginNeed()){
								showDialog(NEED_LOGIN_DIG);
					            return;
							}
							new DownLoadTempOrderDetail((Integer)id).execute();
						}
						
					}
				}
			});
		} else{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.test_list_item,
					new String[] {"no template orders"});//TODO:string
			mListViewRef.get().setAdapter(adapter);
		}
	}
		
	private class PrepareDataTask extends AsyncTask<Object, Bundle, Bundle>{
		private List<String> mTemplateOrderIds=null;
        private List<Map<String, Object>> mListViewData;
    	private SQLiteDatabase mDatabase;
    	private boolean isInitSpinnerData;
    	public PrepareDataTask(boolean isInit) {
    		isInitSpinnerData=isInit;
		}

		protected Bundle doInBackground(Object... params) {
			try{
				mDatabase= new DatabaseHelper(getApplicationContext()).getReadableDatabase();
				initSpinnerData();
				return initListViewData();
			}finally{
				mDatabase.close();
			}
		}
		
		private void initSpinnerData(){
			Bundle progress=new Bundle();
			Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS,
					new String[]{PROVIDER_NAME.VALUE,PROVIDER_NAME.NUMBER,PROVIDER_NAME.TEMP_IDS},
					PROVIDER_NAME.NAME+"=\"port\"", null, null, null, null);
			String data[]=new String[cursor.getCount()+1];
			data[0]=getString(R.string.gen_empty_selection);
			for(int i=1;cursor.moveToNext();i++){
				data[i]=cursor.getString(0);
				if(LAST_SEARCH_PORT!=null&&data[i].equalsIgnoreCase(LAST_SEARCH_PORT)){
					progress.putInt("select_item", i);
					String[] templateOrderIds=cursor.getString(2).split(",");
					mTemplateOrderIds=new ArrayList<String>(templateOrderIds.length);
					for(String each:templateOrderIds){
						mTemplateOrderIds.add(each);
					}
				}
			}
			cursor.close();
			progress.putBoolean("post_finished", true);
			progress.putStringArray("data", data);
			publishProgress(progress);

			progress=new Bundle();
			cursor=mDatabase.query(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS,
					new String[]{PROVIDER_NAME.VALUE,PROVIDER_NAME.NUMBER,PROVIDER_NAME.TEMP_IDS},
					PROVIDER_NAME.NAME+"=\"zone\"", null, null, null, null);
			data=new String[cursor.getCount()+1];
			data[0]=getString(R.string.gen_empty_selection);
			for(int i=1;cursor.moveToNext();i++){
				data[i]=cursor.getString(0);
				if(LAST_SEARCH_ZONE!=null&&data[i].equalsIgnoreCase(LAST_SEARCH_ZONE)){
					progress.putInt("select_item", i);
					if(mTemplateOrderIds==null||mTemplateOrderIds.size()==0){
						String[] templateOrderIds=cursor.getString(2).split(",");
						mTemplateOrderIds=new ArrayList<String>(templateOrderIds.length);
						for(String each:templateOrderIds){
							mTemplateOrderIds.add(each);
						}
					} else{
						String tempIds=cursor.getString(2);
						for(int j=mTemplateOrderIds.size()-1;j>=0;j--){
							if(!tempIds.contains(mTemplateOrderIds.get(j))){
								mTemplateOrderIds.remove(j);
							}
						}
					}
				} 
			}
			cursor.close();
			progress.putBoolean("zone_finished", true);
			progress.putStringArray("data", data);
			publishProgress(progress);

			progress=new Bundle();
			cursor=mDatabase.query(PROVIDER_NAME.TABLE_TEMP_ORDER_FACETS,
					new String[]{PROVIDER_NAME.VALUE,PROVIDER_NAME.NUMBER,PROVIDER_NAME.TEMP_IDS},
					PROVIDER_NAME.NAME+"=\"brand\"", null, null, null, null);
			data=new String[cursor.getCount()+1];
			data[0]=getString(R.string.gen_empty_selection);
			for(int i=1;cursor.moveToNext();i++){
				data[i]=cursor.getString(0);
				if(LAST_SEARCH_BRAND!=null&&data[i].equalsIgnoreCase(LAST_SEARCH_BRAND)){
					progress.putInt("select_item", i);
					if(mTemplateOrderIds==null||mTemplateOrderIds.size()==0){
						String[] templateOrderIds=cursor.getString(2).split(",");
						mTemplateOrderIds=new ArrayList<String>(templateOrderIds.length);
						for(String each:templateOrderIds){
							mTemplateOrderIds.add(each);
						}
					} else{
						String tempIds=cursor.getString(2);
						for(int j=mTemplateOrderIds.size()-1;j>=0;j--){
							if(!tempIds.contains(mTemplateOrderIds.get(j))){
								mTemplateOrderIds.remove(j);
							}
						}
					}
				} 
			}
			cursor.close();
			progress.putBoolean("brand_finished", true);
			progress.putStringArray("data", data);
			publishProgress(progress);
		}
		
		private Bundle initListViewData(){
			if(mTemplateOrderIds==null||mTemplateOrderIds.size()==0){
				Cursor cursor=mDatabase.query(PROVIDER_NAME.TABLE_TEMP_ORDERS,
						new String[]{PROVIDER_NAME.ORDER_ID,PROVIDER_NAME.NAME,PROVIDER_NAME.CONTAINER_ID,PROVIDER_NAME.END_DATE}, 
						null, null, null, null, null);
				if(cursor.getCount()>0){
					mListViewData=new ArrayList<Map<String,Object>>(cursor.getCount());
					HashMap<String, Object> item;
					Cursor c;
					String columns[]=new String[]{SYNC_PROVIDER_NAME.NAME},
					       selection=SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID+"=?";
					while(cursor.moveToNext()){
						item=new HashMap<String, Object>(3);
						item.put("id",cursor.getInt(0));
						item.put("name", cursor.getString(1));
						c=getContentResolver().query(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI, columns, selection,
								new String[]{String.valueOf(cursor.getInt(2))},null);
						if(c.moveToFirst()){
							item.put("container_name", c.getString(0));
						}
						c.close();
						item.put("end_date", cursor.getString(3));
						mListViewData.add(item);
					}
				}
				cursor.close();
			}else{
				mListViewData=new ArrayList<Map<String,Object>>(mTemplateOrderIds.size());
				HashMap<String, Object> item;
				String columns[]=new String[]{SYNC_PROVIDER_NAME.NAME},
			        selection=SYNC_PROVIDER_NAME.CONTAINER_CLASS_ID+"=?";
				Cursor cursor,c;
				for(String each:mTemplateOrderIds){
					cursor=mDatabase.query(PROVIDER_NAME.TABLE_TEMP_ORDERS,
							new String[]{PROVIDER_NAME.NAME,PROVIDER_NAME.CONTAINER_ID,PROVIDER_NAME.END_DATE}, 
							PROVIDER_NAME.ORDER_ID+"="+Integer.valueOf(each), null, null, null, null);
					if(cursor.moveToFirst()){
						item=new HashMap<String, Object>(3);
						item.put("id", Integer.valueOf(each));
						item.put("name", cursor.getString(0));
						c=getContentResolver().query(SYNC_PROVIDER_NAME.CONTAINER_CONTENT_URI, columns, selection,
								new String[]{String.valueOf(cursor.getInt(1))},null);
						if(c.moveToFirst()){
							item.put("container_name", c.getString(0));
						}
						c.close();
						item.put("end_date", cursor.getString(2));
						mListViewData.add(item);
					}
					cursor.close();
				}
			}
			Bundle result = new Bundle();
			result.putBoolean("result", true);
			return result;
		}
		
		@Override
		protected void onProgressUpdate(Bundle... values) {
			if(isInitSpinnerData){
				int selectItem=values[0].getInt("select_item", 0);
				if(values[0].getBoolean("post_finished",false)){
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(
							getBaseContext(), mSpinnerLayout, values[0].getStringArray("data"));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_port.get().setAdapter(adapter);
					sp_port.get().setSelection(selectItem);
					sp_port.get().setOnItemSelectedListener(sp_listener);
				} else if(values[0].getBoolean("zone_finished",false)){
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(
							getBaseContext(), mSpinnerLayout, values[0].getStringArray("data"));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_zone.get().setAdapter(adapter);
					sp_zone.get().setSelection(selectItem);
					sp_zone.get().setOnItemSelectedListener(sp_listener);
				} else if(values[0].getBoolean("brand_finished",false)){
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(
							getBaseContext(), mSpinnerLayout, values[0].getStringArray("data"));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_brand.get().setAdapter(adapter);
					sp_brand.get().setSelection(selectItem);
					sp_brand.get().setOnItemSelectedListener(sp_listener);
				}
			}
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean("result",false)&&mListViewData!=null&&mListViewData.size()!=0){
				refreshListViewData(mListViewData);
			}
			hideProgress();
		}
	}
	
	private class RefreshTask extends NetworkWithSAXTask<Object, Object>{

		@Override
		protected void onPreExecute() {
			showProgress();
		}
		@Override
		public Context createContext() {
			return PrewiredContainerList.this;
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
					DefaultHandlerFactory.PARSE_ORDER_TEMPLATE_LIST_XML, getApplicationContext());
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadOrderTemplateList();
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY,false)){
				LAST_SEARCH_BRAND=null;
				LAST_SEARCH_PORT=null;
				LAST_SEARCH_ZONE=null;
				new PrepareDataTask(true).execute();
				return;
			}
			
			hideProgress();
            if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==404){
				
				Toast.makeText(getBaseContext(),
						getString(R.string.temp_list_noproduct), 
						Toast.LENGTH_SHORT).show();
			}else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==401){
				//TODO:show dialog
			}
		}
	}

	private class DownLoadTempOrderDetail extends NetworkWithSAXTask<Object, Object>{
        private final int mOrderId;
		public DownLoadTempOrderDetail(int orderId) {
			mOrderId=orderId;
		}
		@Override
		protected void onPreExecute() {
			showProgress();
		}
		@Override
		public Context createContext() {
			return PrewiredContainerList.this;
		}

		@Override
		public DefaultHandler createHandler(byte[] xmlBuffer) {
			return DefaultHandlerFactory.createHandler(
					DefaultHandlerFactory.PARSE_ORDER_TEMPLATE_DETAIL_XML, getApplicationContext(), xmlBuffer);
		}

		@Override
		public byte[] upOrDownloadFromNet() throws IOException,
				ResponseException {
			return NetworkUtilities.downloadOrderTemplateDetail(mOrderId);
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			hideProgress();
			if(result.getBoolean(NetworkWithSAXTask.TASK_RESULT_KEY, false)){
				Intent intent = new Intent(getBaseContext(), PrewiredContainerDetail.class);
				intent.putExtra(PrewiredContainerDetail.TEMP_ORDER_ID_KEY, mOrderId);
				startActivity(intent);
//				finish();
			} else if(result.getInt(NetworkWithSAXTask.RESPONSE_EXCEPTIONCODE_KEY, -1)==404){
				Toast.makeText(getBaseContext(), "not found detail information", Toast.LENGTH_SHORT).show();//TODO:string
			} else if(result.getBoolean(NetworkWithSAXTask.HAVE_NOT_LOGIN_KEY, false)){
				showDialog(NEED_LOGIN_DIG);
			}else if(result.getBoolean(NetworkWithSAXTask.CONNECT_TIME_OUT,false)){
				Toast.makeText(getBaseContext(), getString(R.string.gen_dlg_net_timeout), Toast.LENGTH_SHORT).show();
			}
		}		
	}
}
