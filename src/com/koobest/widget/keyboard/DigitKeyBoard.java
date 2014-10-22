package com.koobest.widget.keyboard;

import com.koobest.m.supermarket.activities.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DigitKeyBoard extends LinearLayout{
    private final static String TAG="DigitKeyBoard";
    public final static int KEY_0=0;
    public final static int KEY_1=1;
    public final static int KEY_2=2;
    public final static int KEY_3=3;
    public final static int KEY_4=4;
    public final static int KEY_5=5;
    public final static int KEY_6=6;
    public final static int KEY_7=7;
    public final static int KEY_8=8;
    public final static int KEY_9=9;
    public final static int KEY_ADD=10;
    public final static int KEY_DOWN=11;
    public final static int KEY_POINT=12;
    public final static int KEY_CLOSE=13;
    public final static int KEY_DEL=14;
    public final static int TYPE_FLOAT=15;
    public final static int TYPE_INTEGER=16;
    private EditText mEditView=null;
    private int mEditTextLength=5;
	private OnKeyDownListener mOnKeyDownListener = null;
	private OnKeyCloseDownListener mOnKeyCloseDownListener=null;
	private int mInputType=TYPE_INTEGER;
	public interface OnKeyDownListener{
		void onKey(View v,int key_code);
	}
	
	public interface OnKeyCloseDownListener{
		void onKey();
	}
	
	public DigitKeyBoard(Context context) {
		super(context);
		setOrientation(VERTICAL);
		initKeyBoardView(context);
		Log.e(TAG, "start");
	}
	
	public DigitKeyBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundDrawable(getResources().getDrawable(R.drawable.keyboard_bg));
		setOrientation(VERTICAL);
		initKeyBoardView(context);
		setFocusable(false);
		Log.e(TAG, "start");
	}
	
	private void initKeyBoardView(Context context){
		LayoutParams params=new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);	
		
		LinearLayout layout = new LinearLayout(context);
		initTheFirstLine(context, layout);
		addView(layout,params);
		
		layout = new LinearLayout(context);
        initTheSecondLine(context, layout);
        addView(layout,params);
        
//        layout = new LinearLayout(context);
//        initTheThirdLine(context, layout);
//        addView(layout,params);
	}

	private void initTheFirstLine(Context context,LinearLayout layout){
		LayoutParams params = new LayoutParams(50,ViewGroup.LayoutParams.WRAP_CONTENT); 
		params.weight=1;
		addButton(layout, context, "1", 1, params);
		addButton(layout, context, "2", 2, params);
		addButton(layout, context, "3", 3, params);
		addButton(layout, context, "4", 4, params);
		addButton(layout, context, "5", 5, params);
		addButton(layout, context, "Close", KEY_CLOSE, params);
	}
	
	private void initTheSecondLine(Context context,LinearLayout layout){
		LayoutParams params = new LayoutParams(50,ViewGroup.LayoutParams.WRAP_CONTENT); 
		params.weight=1;
		addButton(layout, context, "6", 6, params);
		addButton(layout, context, "7", 7, params);
		addButton(layout, context, "8", 8, params);
		addButton(layout, context, "9", 9, params);
		addButton(layout, context, "0", 0, params);
		addButton(layout, context, "Del", KEY_DEL, params);
	}
	
	private Button addButton(LinearLayout layout,Context context,String text,int id,LayoutParams params){
		Button btn = new Button(context);
		btn.setId(id);
		if(text.equalsIgnoreCase("Close"))
		{
			btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.keyboard_close_style));
		}
		else
		{
			btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.keyboard_style));
			btn.setText(text);
		}
		btn.setTextColor(R.color.keyboard_color);
		btn.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) {
				if(v.getId()==KEY_CLOSE){
					//closeKeyBoard();
					if(mOnKeyCloseDownListener!=null){
						System.out.println("digit key on down1");
						mOnKeyCloseDownListener.onKey();
					}
				}
//					switch(v.getId()){
//					case 0:
//					case 1:
//					case 2:
//					case 3:
//					case 4:
//					case 5:
//					case 6:
//					case 7:
//					case 8:
//					case 9:
//					case KEY_ADD:
//					case KEY_DOWN:
//					case KEY_DEL:
//					case KEY_POINT:
//					}
				notifyKeyClickedToEditView(v.getId());
				System.out.println("digit key on down");
				if(mOnKeyDownListener!=null){
					System.out.println("digit key on down1");
					mOnKeyDownListener.onKey(v,v.getId());
				}
				
			}
		});
		layout.addView(btn, params);
		return btn;
	}
	
//	private void addButton(LinearLayout layout,Context context,String text,int id,LayoutParams params){
//		Button btn = new Button(context);
//		btn.setText(text);
//		btn.setId(id);
//		btn.setOnClickListener(new View.OnClickListener() 
//		{
//			public void onClick(View v) {
//				if(v.getId()==KEY_CLOSE){
//					//closeKeyBoard();
//					if(mOnKeyCloseDownListener!=null){
//						System.out.println("digit key on down1");
//						mOnKeyCloseDownListener.onKey();
//					}
//				}
////					switch(v.getId()){
////					case 0:
////					case 1:
////					case 2:
////					case 3:
////					case 4:
////					case 5:
////					case 6:
////					case 7:
////					case 8:
////					case 9:
////					case KEY_ADD:
////					case KEY_DOWN:
////					case KEY_DEL:
////					case KEY_POINT:
////					}
//				notifyKeyClickedToEditView(v.getId());
//				System.out.println("digit key on down");
//				if(mOnKeyDownListener!=null){
//					System.out.println("digit key on down1");
//					mOnKeyDownListener.onKey(v,v.getId());
//				}
//				
//			}
//		});
//		layout.addView(btn, params);
//	}
	
	private void notifyKeyClickedToEditView(int keyCode){
		if(mEditView!=null){
			if(!mEditView.isFocused()){
				mEditView.requestFocus();
			}
			if((keyCode<=9&&keyCode>=0)){
				if((mEditView.length()==mEditTextLength&&mInputType==TYPE_INTEGER)
						||(keyCode==0&&mEditView.getText().length()==0)){
					return;
				}
				int index=mEditView.getSelectionStart();
				StringBuffer buffer=new StringBuffer(mEditView.getText().toString());
				buffer.insert(mEditView.getSelectionStart(), keyCode);
				mEditView.setText(buffer.toString());
//				setQtyChangeEvent(Integer.valueOf(buffer.toString()));
				mEditView.setSelection(index+1);
			}else if(keyCode==DigitKeyBoard.KEY_DEL){
				if(mEditView.length()>0){
					int index=mEditView.getSelectionStart();
					if(index>0){
						StringBuffer buffer=new StringBuffer(mEditView.getText().toString());
						buffer.deleteCharAt(index-1);
						if(buffer.length()==0){
//							setQtyChangeEvent(0);
						}else{
//							setQtyChangeEvent(Integer.valueOf(buffer.toString()));
						}
						mEditView.setText(buffer);
						mEditView.setSelection(index-1);
					}else if(mEditView.getText().toString().length()>0) {
						StringBuffer buffer=new StringBuffer(mEditView.getText().toString());
						buffer.deleteCharAt(0);
						if(buffer.length()==0){
//							setQtyChangeEvent(0);
						}else{
//							setQtyChangeEvent(Integer.valueOf(buffer.toString()));
						}
						mEditView.setText(buffer);
						mEditView.setSelection(0);
					}						
				}	
			}else if(keyCode==DigitKeyBoard.KEY_ADD){
				if(mEditView.length()==0){
					mEditView.setText("1");
//					setQtyChangeEvent(1);
				}else{
					int qty=Integer.valueOf(mEditView.getText().toString())+1;
					if(qty>9999){
						return;
					}
					mEditView.setText(String.valueOf(qty));
//					setQtyChangeEvent(qty);
				}
				mEditView.setSelection(mEditView.length());
			}else if(keyCode==DigitKeyBoard.KEY_DOWN){
				if(mEditView.length()>0&&Float.valueOf(mEditView.getText().toString())>1){
					int qty=Integer.valueOf(mEditView.getText().toString())-1;
					mEditView.setText(String.valueOf(qty));
//					setQtyChangeEvent(qty);
				}
				mEditView.setSelection(mEditView.length());
			}else if(keyCode==DigitKeyBoard.KEY_POINT){
				if(mInputType==TYPE_FLOAT){
					StringBuffer buffer=new StringBuffer(mEditView.getText().toString());
					if(buffer.toString().contains(".")){
						return;
					}
					int index=mEditView.getSelectionStart();
					buffer.insert(mEditView.getSelectionStart(), ".");
					mEditView.setText(buffer.toString());
//					setQtyChangeEvent(Integer.valueOf(buffer.toString()));
					mEditView.setSelection(index+1);
				}
				//TODO
			}
		}
	}
	
	void closeKeyBoard(){
		if(mEditView==null){
			return;
		}
		if(mEditView.getText().toString().trim().length()==0){
			mEditView.setText("0");
		}
//		else{
//			mEditView.setText(String.valueOf(Integer.valueOf(productView.qty.getText().toString().trim())));
//		}
//		((LinearLayout)mKeyBoard.getParent()).removeView(mKeyBoard);
//		if(isResetId){
//            mKeyBoard.setId(-1);
//		}
		mEditView.clearFocus();
		//productView.qty.setFocusable(false);
	}
	
	public void setOnKeyDownListener(OnKeyDownListener l){
		mOnKeyDownListener=l;
	}
	
	public void setOnKeyCloseDownListener(OnKeyCloseDownListener l){
		mOnKeyCloseDownListener=l;
	}
	
	public void setEditView(EditText et,int type){
		mInputType=type;
		mEditView=et;
	}
}
