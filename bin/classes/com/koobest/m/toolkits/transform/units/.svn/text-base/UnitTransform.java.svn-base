package com.koobest.m.toolkits.transform.units;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.koobest.m.sync.contentprovider.SYNC_PROVIDER_NAME;
import com.koobest.m.toolkits.transform.exceptions.TransformException;

public class UnitTransform {
	/**
	 * transform the weight to unit Kilogram from the unit of param weight_class_id stand for
	 * @param context
	 * @param weight
	 * @param weight_class_id
	 * @return
	 * @throws TransformException 
	 */
	public static double weightTransform(Context context,double weight,int weight_class_id) throws TransformException{
		ContentResolver resolver=context.getContentResolver();
    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
    	Cursor cursor;
    	cursor=resolver.query(SYNC_PROVIDER_NAME.WEIGHT_UNITS_CONTENT_URI,
    			projection, SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID+"=1",
    			null, null);
    	try{
    		if(!cursor.moveToFirst()){
    			throw new TransformException(
						"the weight unit need could not be found in local");
        	}
    		double targetValue=cursor.getDouble(0);
    		cursor.close();
    		cursor=resolver.query(SYNC_PROVIDER_NAME.WEIGHT_UNITS_CONTENT_URI,
    				projection, SYNC_PROVIDER_NAME.WEIGHT_CLASS_ID+"="
    				+weight_class_id, null, null);
    		if(!cursor.moveToFirst()){
    			throw new TransformException(
						"the weight unit need could not be found in local");
        	}
    		if(targetValue==cursor.getDouble(0)){
    			return weight;
    		}
        	return weight*targetValue/(cursor.getDouble(0));	 
    	}finally{
    		if(!cursor.isClosed()){
    			cursor.close();
    		}
    	}	
	}
	/**
	 * transform the volume to unit Cubic Centimeter from the cubic 
	 * unit of param length_class_id stand for
	 * @param context
	 * @param length
	 * @param lenght_class_id
	 * @return
	 * @throws TransformException 
	 */
	public static double volumeTransform(Context context,double volume,
			int lenght_class_id) throws TransformException{
		ContentResolver resolver=context.getContentResolver();
    	String[] projection=new String[]{SYNC_PROVIDER_NAME.VALUE};
    	Cursor cursor;
    	cursor=resolver.query(SYNC_PROVIDER_NAME.LENGTH_UNITS_CONTENT_URI,
    			projection, SYNC_PROVIDER_NAME.LENGHT_CLASS_ID+"=1",
    			null, null);
    	try{
    		if(!cursor.moveToFirst()){
    			throw new TransformException(
						"the length unit need could not be found in local");
        	}
    		double targetValue=cursor.getDouble(0);
    		cursor.close();
    		cursor=resolver.query(SYNC_PROVIDER_NAME.LENGTH_UNITS_CONTENT_URI,
    				projection, SYNC_PROVIDER_NAME.LENGHT_CLASS_ID+"="
    				+lenght_class_id, null, null);
    		if(!cursor.moveToFirst()){
    			throw new TransformException(
						"the length unit need could not be found in local");
        	}
    		double currentValue=cursor.getDouble(0);
    		cursor.close();
    		if(currentValue==targetValue){
    			return volume;
    		}
        	return volume*(targetValue*targetValue*targetValue)/(currentValue*currentValue*currentValue);	 
    	}finally{
    		if(!cursor.isClosed()){
    			cursor.close();
    		}
    	}	
	}
}
