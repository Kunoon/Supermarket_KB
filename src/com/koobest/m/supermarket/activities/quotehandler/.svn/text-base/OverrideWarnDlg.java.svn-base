package com.koobest.m.supermarket.activities.quotehandler;

import com.koobest.m.supermarket.activities.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.AlteredCharSequence;

public class OverrideWarnDlg extends AlertDialog{
//	private OnBtnClickListener mOnBtnClickListener=null;
	public static abstract class OnBtnClickListener{
		public abstract void onPositiveBtnClick();
		public boolean onNegativeBtnClick(){
			return false;
		}
	}

	protected OverrideWarnDlg(Context context) {
		super(context);
	}

	public static Builder builder(final Context context,final OnBtnClickListener l){
//		mOnBtnClickListener=l;
		return new Builder(context).setMessage(context.getString(R.string.quo_override_alert))
		     .setPositiveButton(R.string.gen_btn_yes, new OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if(l!=null){
						l.onPositiveBtnClick();
					}
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.gen_btn_no, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if(l!=null){
						if(l.onNegativeBtnClick()){
							return;
						}  
					}
					Intent intent= new Intent(context,EditQuote.class);
					context.startActivity(intent);
					dialog.dismiss();
				}
			}); 
	}
	
}
