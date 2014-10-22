package com.koobest.m.supermarket.network;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.ParseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.koobest.m.network.toolkits.ResponseException;
import com.koobest.m.supermarket.constant.Constants;
import com.koobest.m.toolkits.parsexml.ParseXml;

public abstract class NetworkWithSAXTask<Params, Progress> extends AsyncTask<Params, Progress, Bundle> {
	public final static String RESPONSE_EXCEPTIONCODE_KEY="response_exception";
    public final static String TASK_RESULT_KEY="result_key";
    public final static String HAVE_NOT_LOGIN_KEY="not_login_key";
    public final static String CONNECT_TIME_OUT="time_out";
	//public final WeakReference<Activity> mActivityRef;
	
    protected abstract byte[] upOrDownloadFromNet()throws IOException,ResponseException;
    
    /**
     * if you don't want to start auth activity after connect 
     * fail you should only create a context is not activity.
     * such as getApplicationContext().
     * @return
     */
    protected abstract Context createContext();
    
    private Context myContext(){
    	Context context=createContext();
    	if(context!=null&&(context instanceof Activity)){
    		while(((Activity)context).getParent()!=null){
        		context=((Activity)context).getParent();
        	}
    	}
    	return context;
    }
    /**
     * if you need not to parse the xml with SAX.You should only return null in this method
     * @param xml_buffer
     * @return
     */
    protected abstract DefaultHandler createHandler(byte[] xml_buffer);
    
	@Override
	protected Bundle doInBackground(Params... params) {
		if(NetworkUtilities.getHttpClient()==null){
			Bundle result = new Bundle();
			result.putBoolean(HAVE_NOT_LOGIN_KEY, true);
			return result;
		}
		try{
			byte[] buffer=null;
			try{
				buffer= upOrDownloadFromNet();
		    }catch (ResponseException e) {
		    	//Log.e(TAG,"respnse exception code:"+e.getResponseCode());
				e.printStackTrace();
	            if(e.getResponseCode()==401){
	            	//login again to require cooikes
	            	if(!NetworkUtilities.login(myContext(), Constants.getUsername(),true)){
	            		return new Bundle();
	            	}
	            	//if relogin successful access server again
	            	try {
						buffer= upOrDownloadFromNet();
					} catch (ResponseException e1) {
						Bundle bundle=new Bundle();
						bundle.putInt(RESPONSE_EXCEPTIONCODE_KEY, e1.getResponseCode());
		                return bundle;		
					}
	            }else{
	            	Bundle bundle=new Bundle();
					bundle.putInt(RESPONSE_EXCEPTIONCODE_KEY, e.getResponseCode());
	                return bundle;
				}
			}
		    
			if(buffer!=null){
				DefaultHandler handler = createHandler(buffer);
				if(handler==null){
					Bundle result=new Bundle();
					result.putBoolean(TASK_RESULT_KEY, true);
					return result;
				}
				try {
					ParseXml.handleXmlInSAX(buffer, handler);
					Bundle result=new Bundle();
					result.putBoolean(TASK_RESULT_KEY, true);
					return result;
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParseException e) {
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}								
			}	
		}catch (IOException e) {
			if(e instanceof ConnectTimeoutException){
				Bundle result=new Bundle();
				result.putBoolean(TASK_RESULT_KEY, false);
				result.putBoolean(CONNECT_TIME_OUT, true);
				return result;
			}
			e.printStackTrace();
		} 
		Bundle result=new Bundle();
		result.putBoolean(TASK_RESULT_KEY, false);
		return result;
	}
}
