package com.koobest.m.supermarket.activities.productsearch;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.activities.utilities.BaseActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.network.NetworkUtilities;
import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AdvancedSearchProduct extends BaseActivity{
	private static final String TAG = "AdvancedSearchProduct";
//	private static final int SHOW_PRODUCTLIST_DIALOG=0;
	private static String mSearchKeyWord="";
	private static int mManufacturerSelectedId=0;
	private static int mCategorySelectedId=0;
	private static int mBrandSelectedId=0;
	private static Bundle mPage;
	private Spinner sp_manufacturer;
	private Spinner sp_brand;
	private EditText et_name;
	private Spinner sp_category;
//	private Dialog mDialog=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_search_product_page);
	    et_name=(EditText)findViewById(R.id.edit_name);
	    sp_manufacturer=(Spinner)findViewById(R.id.sp_manufaturer);
	    sp_category=(Spinner)findViewById(R.id.sp_category);
	    sp_brand=(Spinner) findViewById(R.id.sp_brand);
		new InitSpinnerDataTask(this, sp_manufacturer,
				sp_category,sp_brand).execute();
		et_name.setText(mSearchKeyWord);
	    findViewById(R.id.Adv_Search).setOnClickListener(new OnClickListener(){
	        public void onClick(View v){
	        	if(et_name.getText().toString().trim().length()==0){
					Toast.makeText(AdvancedSearchProduct.this, 
							getString(R.string.pro_sear_adv_nokeyword), Toast.LENGTH_SHORT).show();
					return;
				}
	        	if(Constants.getIsLoginNeed()){
	        		showDialog(NEED_LOGIN_DIG);
	        		return;
	        	}
	        	if(mPage==null){
	        		setPageIndex(1);
	        	}
	        	new SearchTask(getParent()).execute();
		    }
		});
	    
	    findViewById(R.id.reset).setOnClickListener(new OnClickListener(){
	        public void onClick(View v){
	        	et_name.setText(null);
	        	sp_brand.setSelection(0);
	        	sp_category.setSelection(0);
	        	sp_manufacturer.setSelection(0);
	        	mSearchKeyWord="";
	        	mManufacturerSelectedId=0;
	        	mCategorySelectedId=0;
	        	mBrandSelectedId=0;
	        	setPageIndex(1);
		    }
		});
	}
	
	class InitSpinnerDataTask extends AsyncTask<Object,Integer,Bundle>{
		private WeakReference<Spinner> sp_manufacturer,sp_category,sp_brand;
		private Context mContext;
        public InitSpinnerDataTask(Context context,Spinner manufacturer,Spinner category,Spinner brand) {
        	mContext = context;
        	sp_category=new WeakReference<Spinner>(category);
        	sp_manufacturer=new WeakReference<Spinner>(manufacturer);
        	sp_brand=new WeakReference<Spinner>(brand);
		}
		@Override
		protected Bundle doInBackground(Object... arg0) {
			Cursor cursor=getContentResolver().query(SYNC_PROVIDER_NAME.MANUFACTURER_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.NAME}, null, null, null);
			Bundle result = new Bundle();
			if(cursor.getCount()>0){
				String data[] = new String[cursor.getCount()+1];
				int i=1;
				data[0]=getString(R.string.gen_empty_selection);
				while(cursor.moveToNext()){
					data[i++]=cursor.getString(0);
				}
				result.putBoolean("manuDownSuccess",true);
				result.putStringArray("manuList", data);
			}
			cursor.close();
			cursor=getContentResolver().query(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.NAME}, null, null, null);
			if(cursor.getCount()>0){
				String data[] = new String[cursor.getCount()+1];
				int i=1;
				data[0]=getString(R.string.gen_empty_selection);
				while(cursor.moveToNext()){
					data[i++]=cursor.getString(0);
				}
				result.putBoolean("categoryDownSuccess",true);
				result.putStringArray("categoryList", data);
			}
			cursor.close();
			cursor=getContentResolver().query(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI,
					new String[]{SYNC_PROVIDER_NAME.NAME}, null, null, null);
			if(cursor.getCount()>0){
				String data[] = new String[cursor.getCount()+1];
				int i=1;
				data[0]=getString(R.string.gen_empty_selection);
				while(cursor.moveToNext()){
					data[i++]=cursor.getString(0);
				}
				result.putBoolean("brandDownSuccess",true);
				result.putStringArray("brandList", data);
			}
			cursor.close();
			return result;
		}
		
		@Override
		protected void onPostExecute(Bundle result) {
			if(result.getBoolean("manuDownSuccess",false)){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(mContext,
						R.layout.my_spinner, result.getStringArray("manuList"));
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_manufacturer.get().setAdapter(adapter);
				sp_manufacturer.get().setSelection(mManufacturerSelectedId);
			}
			
			if(result.getBoolean("categoryDownSuccess",false)){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(mContext,
						R.layout.my_spinner, result.getStringArray("categoryList"));
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_category.get().setAdapter(adapter);
				sp_category.get().setSelection(mCategorySelectedId);
			}
			
			if(result.getBoolean("brandDownSuccess",false)){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(mContext,
						R.layout.my_spinner, result.getStringArray("brandList"));
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_brand.get().setAdapter(adapter);
				sp_brand.get().setSelection(mCategorySelectedId);
			}
		}
	}
	
	private void setSearchKeyWord(String word){
		mSearchKeyWord=word;
	}
	
	private void setManufacturerSelectedId(int position){
		mManufacturerSelectedId=position;
	}
	
	private void setBrandSelectedId(int position){
		mBrandSelectedId=position;
	}
	
	private void setCategorySelectedId(int position){
		mCategorySelectedId=position;
	}
	
	private void setPageIndex(int page){
		if(mPage==null){
			mPage=new Bundle();
		}
		mPage.putInt("page", page);
	}
	
	private String getSearchKeyWord(){
		return mSearchKeyWord;
	}
	
	private int getManufacturerSelectedIndex(){
		return mManufacturerSelectedId;
	}
	
	private int getBrandSelectedIndex(){
		return mBrandSelectedId;
	}
	
	private int getCategorySelectedIndex(){
		return mCategorySelectedId;
	}
//	
	private int getPage(){
		return mPage.getInt("page");
	}
	
	class SearchTask extends BaseSearchTask{
        public SearchTask(Activity activity) {
			super(activity, mPage);
		}

        @Override
		protected BaseSearchTask createNewSearchTask() {
			return new SearchTask(getParent());
		} 

		@Override
		public byte[] upOrDownloadFromNet() throws IOException, ResponseException {
			String categoryId="",manufacturerId="",brandId="";
			int selectedIndex;Cursor cursor;
			if((selectedIndex=sp_category.getSelectedItemPosition())!=0){
				cursor=getContentResolver().query(SYNC_PROVIDER_NAME.PRODUCT_CATEGORY_CONTENT_URI,
						new String[]{SYNC_PROVIDER_NAME.CATEGORY_ID}, null, null, null);
				if(cursor.moveToPosition(selectedIndex-1)){
					categoryId=String.valueOf(cursor.getInt(0));
				}
				cursor.close();
			}
			if(getCategorySelectedIndex()!=selectedIndex){
				setCategorySelectedId(selectedIndex);
				setPageIndex(1);
			}
			
			if((selectedIndex=sp_manufacturer.getSelectedItemPosition())!=0){
				cursor=getContentResolver().query(SYNC_PROVIDER_NAME.MANUFACTURER_CONTENT_URI,
						new String[]{SYNC_PROVIDER_NAME.MANUFACTURER_ID}, null, null, null);
				if(cursor.moveToPosition(selectedIndex-1)){
					manufacturerId=String.valueOf(cursor.getInt(0));
				}
				cursor.close();
			}
			if(getManufacturerSelectedIndex()!=selectedIndex){
				setManufacturerSelectedId(selectedIndex);
				setPageIndex(1);
			}	
			
			if((selectedIndex=sp_brand.getSelectedItemPosition())!=0){
				cursor=getContentResolver().query(SYNC_PROVIDER_NAME.BRAND_CONTENT_URI,
						new String[]{SYNC_PROVIDER_NAME.BRAND_ID}, null, null, null);
				if(cursor.moveToPosition(selectedIndex-1)){
					brandId=String.valueOf(cursor.getInt(0));
				}
				cursor.close();
			}
			if(getBrandSelectedIndex()!=selectedIndex){
				setBrandSelectedId(selectedIndex);
				setPageIndex(1);
			}
			
			if(!getSearchKeyWord().equalsIgnoreCase(et_name.getText().toString().trim())){
				setSearchKeyWord(et_name.getText().toString().trim());
				setPageIndex(1);
			}
			return NetworkUtilities.downLoadProductList(
					getSearchKeyWord(),categoryId,manufacturerId,brandId,String.valueOf(getPage()));
		}
		
	}
}
