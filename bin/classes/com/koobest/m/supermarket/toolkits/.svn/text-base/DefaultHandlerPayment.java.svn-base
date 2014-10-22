package com.koobest.m.supermarket.toolkits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.koobest.m.supermarket.database.NAME;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class DefaultHandlerPayment extends DefaultHandler{
	private final static String TAG = "DefaultHandlerPayment";
	private Stack<String> tags = new Stack<String>();
	private List<Map<String, Object>> mResponse;
	private Map<String, Object> tempItem;
	private CallBack mCallBack;
	private String mCurrentPaymentID,tempComment="";
	public interface CallBack{
		void notifyCurrentSelectPosition(int positon);
	}
	
	public DefaultHandlerPayment(List<Map<String, Object>> response,String currentPaymentID,CallBack callback) {
		mResponse = response;
		mCurrentPaymentID=currentPaymentID;
		mCallBack = callback;
	}

	public void endDocument() throws SAXException {
		
	}

	public void startDocument() throws SAXException {
		tempItem=new HashMap<String, Object>(4);
	}

	public void startElement(String p0, String p1, String p2,
			Attributes p3) throws SAXException {
		tags.push(p1);
	}

	public void endElement(String p0, String p1, String p2)
			throws SAXException {
		tags.pop();
		if (p1.equalsIgnoreCase("payment_method")) {
			tempItem.put(NAME.PAYMENT_COMMENT, tempComment);tempComment="";
			mResponse.add(tempItem);
			tempItem=new HashMap<String, Object>(4);
		} 
	}

	public void characters(char[] p0, int p1, int p2)
			throws SAXException {
		String tag = tags.peek();
		if (tag.equals("id")) {
			tempItem.put(NAME.PAYMENT_ID, new String(p0,p1,p2));
			if(mCallBack!=null&&mCurrentPaymentID!=null&&mCurrentPaymentID.equalsIgnoreCase(new String(p0,p1,p2))){
				mCallBack.notifyCurrentSelectPosition(mResponse.size());
			}
		} else if (tag.equals("name")){
			tempItem.put(NAME.PAYMENT_NAME, new String(p0,p1,p2));
		} else if (tag.equals("comment")){
			tempComment=tempComment+new String(p0,p1,p2);
		} 		
	}	
}
