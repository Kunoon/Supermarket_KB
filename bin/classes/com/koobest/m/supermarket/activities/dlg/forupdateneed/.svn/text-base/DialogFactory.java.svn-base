package com.koobest.m.supermarket.activities.dlg.forupdateneed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.MonthDisplayHelper;

import com.koobest.m.supermarket.activities.R;
import com.koobest.m.supermarket.constant.Constants;

public class DialogFactory {
	 public interface OnUpdateStatuListener{
		 void onStart();
		 void onFinish(int result);
	 }
     public static Dialog createCurrencyAlertDlg(final Context context,final OnUpdateStatuListener l){
    	 return new AlertDialog.Builder(context).
			setMessage(context.getString(R.string.gen_dlg_nocurrency)).
			setPositiveButton(context.getString(R.string.gen_btn_ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					if(Constants.getCustomerId()==-1){
						if(l!=null){
							l.onStart();
						}
						return;
					}
					new DownloadTaskFactory.DownloadCurrencyTask(context,l).execute();
				}
			}).setNegativeButton(R.string.gen_btn_cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create();
     }
     
     public static Dialog createCustomerAlertDlg(final Context context,final OnUpdateStatuListener l){
    	 return new AlertDialog.Builder(context).
			setMessage(context.getString(R.string.gen_dlg_nocustomer)).
			setPositiveButton(context.getString(R.string.gen_btn_ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					if(Constants.getCustomerId()==-1){
						if(l!=null){
							l.onStart();
						}
						return;
					}
					new DownloadTaskFactory.DownloadCustomerTask(context,l).execute();
				}
			}).setNegativeButton(R.string.gen_btn_cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create();
     }
     
     public static Dialog createLengthAlertDlg(final Context context,final OnUpdateStatuListener l){
    	 return new AlertDialog.Builder(context).
			setMessage(context.getString(R.string.gen_dlg_nolengthunit)).
			setPositiveButton(context.getString(R.string.gen_btn_ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					if(Constants.getCustomerId()==-1){
						if(l!=null){
							l.onStart();
						}
						return;
					}
					new DownloadTaskFactory.DownloadLengthTask(context,l).execute();
				}
			}).setNegativeButton(R.string.gen_btn_cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create();
     }
     
     public static Dialog createWeightAlertDlg(final Context context,final OnUpdateStatuListener l){
    	 return new AlertDialog.Builder(context).
			setMessage(context.getString(R.string.gen_dlg_noweightunit)).
			setPositiveButton(context.getString(R.string.gen_btn_ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					if(Constants.getCustomerId()==-1){
						if(l!=null){
							l.onStart();
						}
						return;
					}
					new DownloadTaskFactory.DownloadWeightTask(context,l).execute();
				}
			}).setNegativeButton(R.string.gen_btn_cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
				}
			}).create();
     }
}
