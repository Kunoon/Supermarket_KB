package com.koobest.m.toolkits.transform.currency;

import javax.xml.transform.TransformerException;

import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

public class CurrencyTransform {
	/**
	 * turn currency from one to another Code 
	 * @param context
	 * @param currency 
	 * @param from 
	 * @param to
	 * @return
	 */
//    public static double[] transform(Context context,double[] currency,String from,String to){
//    	ContentResolver resolver=context.getContentResolver();
//    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
//    	String selection=SYNC_PROVIDER_NAME.CODE+"=?";
//    	Cursor cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//    			projection, selection, new String[]{from}, null);
//    	try{
//    		if(!cursor.moveToFirst()){
//        		return null;
//        	}
//        	double preValue=cursor.getDouble(0);
//        	cursor.close();
//        	cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//        			projection, selection, new String[]{to}, null);
//        	if(!cursor.moveToFirst()){
//        		return null;
//        	}
//        	double value=cursor.getDouble(0);
//        	cursor.close();
//        	int max = currency.length;
//        	double[] ret=new double[max];
//        	for(int i=0;i<max;i++){
//        		ret[i] = currency[i]*value/preValue;
//        	}
//        	return ret;
//    	}finally{
//    		if(!cursor.isClosed()){
//    			cursor.close();
//    		}
//    	}
//    }
//    
//    public static double transform(Context context,double currency,String from,String toCode){
//    	ContentResolver resolver=context.getContentResolver();
//    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
//    	String selection=SYNC_PROVIDER_NAME.CODE+"=?";
//    	Cursor cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//    			projection, selection, new String[]{from}, null);
//    	try{
//    		if(!cursor.moveToFirst()){
//        		return -1;
//        	}
//        	double preValue=cursor.getDouble(0);
//        	cursor.close();
//        	cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//        			projection, selection, new String[]{toCode}, null);
//        	if(!cursor.moveToFirst()){
//        		return -1;
//        	}
//        	double value=cursor.getDouble(0);
//        	cursor.close();
//        	return currency*value/preValue;	 
//    	}finally{
//    		if(!cursor.isClosed()){
//    			cursor.close();
//    		}
//    	}
//    }
    /**
     * turn currency from basic{value=1} Code to another Code 
     * @param context
     * @param currency
     * @param to
     * @return
     */
//    public static double[] transform(Context context,double[] currency,String toCode){
//    	ContentResolver resolver=context.getContentResolver();
//    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
//    	String selection=SYNC_PROVIDER_NAME.CODE+"=?";
//    	Cursor cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//    			projection, selection, new String[]{toCode}, null);
//    	try{
//    		if(!cursor.moveToFirst()){
//        		return null;
//        	}
//    		double value=cursor.getDouble(0);
//        	cursor.close();
//        	
//        	int max = currency.length;
//        	double[] ret=new double[max];
//        	for(int i=0;i<max;i++){
//        		ret[i] = currency[i]*value;
//        	}
//        	return ret;
//    	}finally{
//    		if(!cursor.isClosed()){
//    			cursor.close();
//    		}
//    	}
//    }
//    
//    
//    public static double transform(Context context,double currency,String to) throws TransformerException{
//    	ContentResolver resolver=context.getContentResolver();
//    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
//    	String selection=SYNC_PROVIDER_NAME.CODE+"=?";
//    	Cursor cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
//    			projection, selection, new String[]{to}, null);
//    	try{
//    		if(!cursor.moveToFirst()){
//        		throw new TransformerException("no currency you need");
//        	}
//        	return currency*(cursor.getDouble(0));	 
//    	}finally{
//    		if(!cursor.isClosed()){
//    			cursor.close();
//    		}
//    	}
//    }
    
    /**
     * turn currency from a CurrencySymbol stand for to basic{value=1} Code 
     * @param context
     * @param fromSymbol
     * @param currency
     * @return
     * @throws TransformerException 
     */
    public static double transform(Context context,double currency,char fromSymbol,boolean isLeftSymbol) throws TransformerException{
    	ContentResolver resolver=context.getContentResolver();
    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
    	String selection;
    	if(isLeftSymbol){
        	selection=SYNC_PROVIDER_NAME.SYMBOL_LEFT+"=\""+fromSymbol+"\"";
    	}else{
    		selection=SYNC_PROVIDER_NAME.SYMBOL_RIGHT+"=\""+fromSymbol+"\"";
    	}
    	Cursor cursor=resolver.query(SYNC_PROVIDER_NAME.CURRENCY_CONTENT_URI,
    			projection, selection, null, null);
    	try{
    		if(!cursor.moveToFirst()){
    			throw new TransformerException("no currency you need");
        	}
    		if(cursor.getDouble(0)==1.0){
    			return currency;
    		}
        	return currency/(cursor.getDouble(0));	 
    	}finally{
    		if(!cursor.isClosed()){
    			cursor.close();
    		}
    	}
    }
}
