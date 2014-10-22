package com.koobest.m.supermarket.account.setting;

import java.util.List;

import android.accounts.Account;
import android.app.ListActivity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.ContentProviderOperation.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.AdapterView.OnItemClickListener;

import com.koobest.m.supermarket.activities.R;

public class GeneralSettingActivity extends ListActivity{
	private final static String TAG="GeneralSettingActivity";
	private Account mAccount;
	private String mAuthority;
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//setContentView(R.layout.account_setting);
    	Intent intent=getIntent();
    	mAccount=(Account)intent.getParcelableExtra("account");
    	mAuthority=intent.getStringExtra(getString(R.string.authority_key));
    	Bundle bundle=intent.getExtras();
    	for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Log.e("my"+key,String.valueOf(value));
        }
    	List<PeriodicSync> list=ContentResolver.getPeriodicSyncs(mAccount, mAuthority);
        int checkedIndex=-1;
		for(PeriodicSync p:list){
			 switch((int)(p.period)/43200){
			 case 1:checkedIndex=0;break;
			 case 2:checkedIndex=1;break;
			 case 4:checkedIndex=2;break;
			 case 8:checkedIndex=3;break;
			 case 14:checkedIndex=4;break;
			 };		
		}
		MyAdapter adapter=new MyAdapter(this,checkedIndex);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				long period=0;
				switch(arg2){
				case 0:period=2;break;
				case 1:period=4;break;
				case 2:period=7;break;
				case 3:period=15;break;
				}
				//if(period!=0)
				//	startSync(mAccount, mAuthority, new Bundle(), period);
				finish();
			}
		});    
    }
	
	class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private final String[] period;
		private final int checkedIndex;
        public MyAdapter(Context context, int checkedIndex) {
			period=new String[]{"two days","four days","a week","half month"};
			this.checkedIndex=checkedIndex;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		public int getCount() {
			return 4;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			CheckedTextView tv=(CheckedTextView) mInflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
			//android.R.drawable.btn_dialogstyle.drawable.btn_dropdown
			
			switch(position){
			case 0:tv.setText(period[0]);break;
			case 1:tv.setText(period[1]);break;
			case 2:tv.setText(period[2]);break;
			case 3:tv.setText(period[3]);break;
			}
			if(position==checkedIndex)
				tv.setChecked(true);
			return tv;
		}
		
	}
}
