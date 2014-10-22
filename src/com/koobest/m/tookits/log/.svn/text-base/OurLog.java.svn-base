package com.koobest.m.tookits.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.text.format.Time;

public class OurLog {
   public static void writeCurrentTimeToFile(String note){
    	//<--
    	try {
		    File file = new File("/mnt/sdcard", "minefilw.txt");
        	if(!file.exists()){
        		file.createNewFile();
        	}	
        	FileOutputStream output = new FileOutputStream(file,true);
        	Time t = new Time(); // or Time t=new Time("GMT+8");
			t.setToNow();
			String time = String.valueOf(t.year) + "-"
			+ String.valueOf(t.month + 1) + "-"
			+ String.valueOf(t.monthDay) + " "
			+ String.valueOf(t.hour) + ":"
			+ String.valueOf(t.minute) + ":"
			+ String.valueOf(t.second)+" "+note+"\n";
	        output.write(time.getBytes());	
    	} catch (IOException e1) {
			e1.printStackTrace();
		}
	    //-->  
    }
}
