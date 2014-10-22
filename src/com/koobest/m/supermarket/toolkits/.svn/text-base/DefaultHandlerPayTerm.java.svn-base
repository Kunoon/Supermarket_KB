package com.koobest.m.supermarket.toolkits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

import com.koobest.m.supermarket.database.NAME;
import com.koobest.m.supermarket.toolkits.DefaultHandlerPayment.CallBack;

public class DefaultHandlerPayTerm extends DefaultHandler{
	private final static String TAG = "DefaultHandlerPayment";
	private Stack<String> tags = new Stack<String>();
	private final List<Map<String, Object>> mResponse;
	private Map<String, Object> tempItem;
	private CallBack mCallBack;
	private int mCurrentPaytermID=-1,tempInt;
//	private String tempString=" ";
	public interface CallBack{
		void notifyCurrentSelectPosition(int positon);
	}
	/**
	 * 
	 * @param response
	 * @param currentPaytermID if no select Item set -1
	 * @param callback
	 */
	public DefaultHandlerPayTerm(List<Map<String, Object>> response,int currentPaytermID,CallBack callback) {
		mResponse = response;
		mCurrentPaytermID=currentPaytermID;
		mCallBack = callback;
//		tempString=
	}

	public void endDocument() throws SAXException {
		
	}

	public void startDocument() throws SAXException {
		tempItem=new HashMap<String, Object>(8);
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equals("payment_term")) {
			mResponse.add(tempItem);
			tempItem=new HashMap<String, Object>(8);
		} 
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("id")) {
			tempItem.put(NAME.PAYTERM_ID, tempInt=Integer.valueOf(new String(p0,p1,p2)));
			if(mCallBack!=null&&mCurrentPaytermID==tempInt){
				mCallBack.notifyCurrentSelectPosition(mResponse.size());
			}
		} else if (tag.equals("deposit")){
			tempItem.put(NAME.DEPOSIT, Integer.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("grace_period")){
			tempItem.put(NAME.GRACE_PERIOD, Integer.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("discount")){
			tempItem.put(NAME.DISCOUNT, Double.valueOf(new String(p0,p1,p2)));
		} else if (tag.equals("description")){
			tempItem.put(NAME.DESCRIPTION, new String(p0,p1,p2));
		}		
	}	
}
