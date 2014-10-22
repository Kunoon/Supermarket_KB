package com.koobest.m.supermarket.activities.productsearch;


import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.activities.SupermarketMain;
import com.koobest.m.supermarket.activities.R.drawable;
import com.koobest.m.supermarket.activities.R.id;
import com.koobest.m.supermarket.activities.R.layout;
import com.koobest.m.supermarket.activities.R.string;
import com.koobest.m.supermarket.activities.quotehandler.EditQuote;
import com.koobest.m.supermarket.activities.utilities.BaseTabActivity;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.toolkits.ofcurrency.CurrencyNote;
import com.koobest.m.supermarket.toolkits.ofcurrency.GetCurrencyInformation;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;


public class SearchProductTab extends BaseTabActivity{
	private static final String TAG="SearchProductTab";
	static final int SHOW_PRODUCTLIST_DIALOG=0;
	private Dialog mDialog=null;
	private View mDialogView=null;//used to show product list after search
	private CurrencyNote mCurrencyNote=null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		View root=LayoutInflater.from(this).inflate(R.layout.pro_search_tab,
				null, true);
		setContentView(root);
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(getTabHost().newTabSpec("tab1").setIndicator(composeLayout(getString(R.string.pro_sear_sim)))
				.setContent(new Intent(this,SearchProduct.class))); 

		tabHost.addTab(getTabHost().newTabSpec("tab2").setIndicator(composeLayout(getString(R.string.pro_sear_adv)))
				.setContent(new Intent(this,AdvancedSearchProduct.class)));
		tabHost.addTab(getTabHost().newTabSpec("tab3").setIndicator(composeLayout(getString(R.string.pro_sear_local)))
				.setContent(new Intent(this,LocalSearchProduct.class)));
//		tabHost.setPadding(0, -30, 0, -30); 
		
		findViewById(R.id.gen_btn_main).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SupermarketMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
			}
		});
		findViewById(R.id.gen_btn_back).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onStart() {
		findViewById(android.R.id.tabcontent).setBackgroundDrawable(SupermarketMain.getBackGround(getApplicationContext()));
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onPause();
	}
	
	View maybeCreateListView(){
		if(mDialogView==null){
			LayoutInflater inflater=LayoutInflater.from(this);
			mDialogView=inflater.inflate(R.layout.productlist_dialog_page, null);
		}
		return mDialogView;
	}
	
	CurrencyNote maybeCreateCurrencyNote(){
		if(mCurrencyNote==null){
			Bundle data = GetCurrencyInformation.getRateAndSymbol(getApplicationContext());
	        mCurrencyNote=new CurrencyNote();
			if(data!=null){
				mCurrencyNote.isLeftSymbol=data.getBoolean(GetCurrencyInformation.IS_LEFT_SYMBOL,true);
				mCurrencyNote.currencySymbol=data.getString(GetCurrencyInformation.SYMBOL);
				mCurrencyNote.exchangeRateTobase=data.getDouble(GetCurrencyInformation.EXCHANGE_RATE_KEY, 1);
			}else{
				mCurrencyNote.currencySymbol=getString(R.string.default_currencysymbol);
			}
		}
		return mCurrencyNote;
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
        switch(id){
        case SHOW_PRODUCTLIST_DIALOG:

			Dialog dialog=new Dialog(this, R.style.TANCStyle);
			dialog.setContentView(mDialogView);
			dialog.setTitle(R.string.b1_dialog_tag);
//        	return new AlertDialog.Builder(this).setTitle(getString(R.string.b1_dialog_tag))
//        		.setView(mDialogView)
    		return dialog;
        }
        return super.onCreateDialog(id, args);
	}
    
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		System.out.println("prepare dialog");
		switch(id){
		case SHOW_PRODUCTLIST_DIALOG:
			System.out.println("prepare dialog1");
			hideProgress();
		}
	}
	
	void showProgress(){
		if(mDialog==null){
			mDialog=ProgressDialog.show(
					SearchProductTab.this, null,
					getString(R.string.gen_progress_wait), true, true);
		}
	}
	
	void hideProgress(){
		
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		mDialog=null;
	}
}