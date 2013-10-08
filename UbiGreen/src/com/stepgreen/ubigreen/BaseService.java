package com.stepgreen.ubigreen;
import android.app.Service;
import com.stepgreen.ubigreen.xmlpuller;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.res.Resources;
import java.util.Scanner;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;
import android.os.IBinder;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import com.stepgreen.ubigreen.*;
public class BaseService extends Service {
	private static Timer timer = new Timer();
	
	public static List<String> listactions=new ArrayList<String>();
	public static List<Long>   listtime=new ArrayList<Long>();
	@Override
      public IBinder onBind(Intent intent)
      {
         return null;
      }
      @Override
      public void onCreate()
      {
    	  super.onCreate();
//    	  getApplicationContext().bindService(new Intent(getApplicationContext(), DetectionActivity.class), null, Context.BIND_AUTO_CREATE);
    	  Log.e("UBIGREEN","Entering LoadDB");
    	  LoadDB();																												//Read the database File
    	  Log.e("UBIGREEN","Loaded DB "+listtime.size()+":"+listactions.size());
    	  for(int i=0;i<listtime.size();i++)
    	  {
    		  Calendar cala=Calendar.getInstance();
    		  long currenttime=cala.get(Calendar.HOUR_OF_DAY)*3600+cala.get(Calendar.MINUTE)*60+cala.get(Calendar.SECOND);
    		  long  startat;
    		  if(currenttime>listtime.get(i))																					//Get no of seconds to start in from db and current time
    			  startat=24*60*60-currenttime+listtime.get(i);
    		  else
    			  startat=listtime.get(i)-currenttime;
    			  
    		   Log.e("UBIGREEN","Scheduled to start activity in "+Long.toString(startat)+" secs" + " CUrrent time : " + Long.toString(currenttime));
    	
    		  timer.scheduleAtFixedRate(new toastprovider(), startat*1000, 24*60*60*1000);										//Schedule the even 
 //   		  Log.e("UBIGREEN","Process in "+Long.toString(startat));
    	  }
 //   	  Log.e("UBIGREEN","Processing done\n");
    	  Toast.makeText(this , "Service created",Toast.LENGTH_LONG).show();
       }
      public void LoadDB() {
    	  /*
    	  InputStream is = getResources().openRawResource(R.raw.actions);
    	    Scanner fil=new Scanner(is);
    	          int no=fil.nextInt();
    	          fil.nextLine();																							//Read line 
                  while((no--)>0)									
                  {
                	  
                	  String act=fil.nextLine();																			//Get the name of event and time
                	  long time=fil.nextLong();
                	//  String whateva=fil.next();
                	  fil.nextLine();
                	  
                      listactions.add(act);
                      listtime.add(time);
//                      Log.e("UBIGREEN","YAAAAAAY RAN 1 time |"+act);
                  }
                  try {
                     	
                  is.close();
    	    fil.close();
    	    } catch (Exception e) {
    	        e.printStackTrace();
//    	        Log.e("UBIGREEN","NOOOOOOOOOOOO ERROR Occured while parsing the database of actions!");
    	    }*/
//    	  Intent dialogIntent = new Intent(getBaseContext(), xmlpuller.class);
	//		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//	getApplication().startActivity(dialogIntent); 					//Retrieve Stepgreen Actions
    	  xmlpuller pul=new xmlpuller();
    	  pul.onCreate(null);
    	  Log.e("UBIGREEN","EXITED INTENT");
      }

      public void onDestroy()
      {
    	  super.onDestroy();
    	  Toast.makeText(this , "Service destroyed",Toast.LENGTH_LONG).show();
      	     	  
      }
      private class toastprovider extends TimerTask
      {
    	  public void run()
    	  {
    		  toastHandler.sendEmptyMessage(0);																	      //On timer Expiry Call Event
    	  }
      }
      public final Handler toastHandler = new Handler()
      {
          @Override
          public void handleMessage(Message msg)
          {
    		  Calendar cala=Calendar.getInstance();
    		  long curtime=cala.get(Calendar.HOUR_OF_DAY)*3600+cala.get(Calendar.MINUTE)*60+cala.get(Calendar.SECOND);//Get Current time and filter the messages from all
        	  for(int i=0;i<listtime.size();i++)
        	  {
        	      if((listtime.get(i)-curtime)<5&&(listtime.get(i)-curtime)>-5)
        	    	  Toast.makeText(getApplicationContext(), listactions.get(i), Toast.LENGTH_SHORT).show();         //Toast the Message
  //                Log.e("UBIGREEN","Currtime: "+Long.toString(curtime)+ " Listtime: "+Long.toString(listtime.get(i))  );      
        	  }
        	
              }
      }; 
}
