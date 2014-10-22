package com.koobest.widget.keyboard;

import java.lang.ref.WeakReference;

import com.koobest.m.supermarket.activities.R;
import com.koobest.widget.keyboard.DigitKeyBoard.OnKeyCloseDownListener;
import com.koobest.widget.keyboard.DigitKeyBoard.OnKeyDownListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MarketProductBoard extends LinearLayout{
	private WeakReference<TextView> tv_MaxQty_Tag;
	private WeakReference<TextView> tv_MaxQty;
	private WeakReference<TextView> tv_MinQty;
	private WeakReference<TextView> tv_DiscountQty;
	private WeakReference<TextView> tv_DiscountPrice;
	private WeakReference<DigitKeyBoard> mKeyBoard;
	private final static float TEXT_SIZE=14; 
	private final static int TEXT_COLOR=Color.BLACK;
	public MarketProductBoard(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setOrientation(VERTICAL);
		initView(context);
	}
	
	public MarketProductBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOrientation(VERTICAL);
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		
		LinearLayout layout =new LinearLayout(context);
		layout.setOrientation(HORIZONTAL);
		
		LayoutParams params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tv=new TextView(context);
		tv.setText(context.getString(R.string.key_board_maxqty));
		tv.setTextColor(TEXT_COLOR);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		tv_MaxQty_Tag=new WeakReference<TextView>(tv);
		layout.addView(tv,params);
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.weight=1;
		tv=new TextView(context);
		tv_MaxQty=new WeakReference<TextView>(tv);
		tv.setTextColor(TEXT_COLOR);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		layout.addView(tv,params);
		
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tv=new TextView(context);
		tv.setText(context.getString(R.string.key_board_minqty));
		tv.setTextColor(TEXT_COLOR);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		layout.addView(tv,params);
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.weight=1;
		tv=new TextView(context);
		tv.setTextColor(TEXT_COLOR);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		tv_MinQty=new WeakReference<TextView>(tv);
		layout.addView(tv,params);
		
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.weight=2;
		tv=new TextView(context);
		tv.setTextColor(TEXT_COLOR);
		tv_DiscountQty=new WeakReference<TextView>(tv);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		layout.addView(tv,params);
		tv=new TextView(context);
		tv.setTextColor(TEXT_COLOR);
		tv_DiscountPrice=new WeakReference<TextView>(tv);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
		layout.addView(tv,params);
		
		params=new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(layout, params);
		DigitKeyBoard keyBoard = new DigitKeyBoard(context);
		mKeyBoard=new WeakReference<DigitKeyBoard>(keyBoard);
		addView(keyBoard,params);
		registCloseListener();
	}
	
	private void registCloseListener(){
		mKeyBoard.get().setOnKeyCloseDownListener(new OnKeyCloseDownListener() {
			
			public void onKey() {
				close();
			}
		});
	}
	
	public void setEditView(EditText et){
		mKeyBoard.get().setEditView(et,DigitKeyBoard.TYPE_INTEGER);
	}
	
	public void setEditView(EditText et,int type){
		mKeyBoard.get().setEditView(et,type);
	}
	
	public void setOnKeyDownListener(OnKeyDownListener l){
		mKeyBoard.get().setOnKeyDownListener(l);
	}
	
	public void setMaxQty(int qty){
		if(qty==Integer.MAX_VALUE){
			tv_MaxQty_Tag.get().setVisibility(View.GONE);
			tv_MaxQty.get().setVisibility(View.GONE);
		}else{
			tv_MaxQty.get().setVisibility(View.VISIBLE);
			tv_MaxQty_Tag.get().setVisibility(View.VISIBLE);
			tv_MaxQty.get().setText(String.valueOf(qty));
		}
	}
	
	public void setMinQty(int qty){
		tv_MinQty.get().setText(String.valueOf(qty));
	}
	
	/**
	 * 
	 * @param infor such as String "DiscountQty:30"
	 */
	public void setDiscountQtyInfor(String infor){
		tv_DiscountQty.get().setText(infor);
	}
	
	/**
	 * 
	 * @param infor such as String "DiscountPrice:$30"
	 */
	public void setDiscountPriceInfor(String infor){
		tv_DiscountPrice.get().setText(infor);
	}
	
	public void setDiscountInforViewVisibility(int visible){
		if(visible==View.VISIBLE){
			tv_DiscountQty.get().setVisibility(View.VISIBLE);
			tv_DiscountPrice.get().setVisibility(View.VISIBLE);
		} else{
			tv_DiscountQty.get().setVisibility(View.INVISIBLE);
			tv_DiscountPrice.get().setVisibility(View.INVISIBLE);
		}
	}
	
	public boolean isClosed(){
		return getParent()==null?true:false;
	}
	
	public void close(){
		if(getParent()!=null){
			mKeyBoard.get().closeKeyBoard();
			((ViewGroup)getParent()).removeView(this);
		}
	}
}
