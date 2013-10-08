package com.stepgreen.ubigreen;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.stepgreen.ubigreen.GLRenderer;
public class Coefficient_calculatorActivity extends Activity {
    /** Called when the activity is first created. */
	TextView gcoeff;
	String contentTxt;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {// Check the Charging status of the battery
          // TODO Auto-generated method stub
          int level = intent.getIntExtra("level", 0);
          contentTxt=(String.valueOf(level) + "%");
//          Log.e("BATTERY   ",contentTxt);
        }
      };
	public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.registerReceiver(this.mBatInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//Chacking the battery status

    }
    float credits = 15;		//Initially 15 credits
    int cnt = 0;
    float distance = 0;
    float wasted = 0;
    long time_pre = 0;
    long time_now = 0;
    float steps_now = 0;
    float steps_prev = 0;
/*    public void get(float x)
    {
    	int arr[] = new int [100];
    }*/
     public void Gcoefficient(float value, int flag)
    {
    	float change = value/10;
    	if(flag == 1)
    		credits = credits-change;	//If traveling by car
    	else
    		credits = credits + change;	//If traveling on foot/staircase
    	if(credits>25)					//Limit upperbound
    		credits=25;
    	if(credits<0)					//Limit Lower Bound
    		credits=0;
    	GLRenderer.GreenCoeff=(int)credits;		//Set Values in the code for the wallpaper to accordingly change the wallpaper 
    	if(flag==2)
    	{
    		GLRenderer.CurMotion="Stationary";
    		GLRenderer.StepCount=0;
    	}
    	else if(flag==1)
    	{
    		GLRenderer.CurMotion="Automobile";
    		GLRenderer.StepCount=(int)steps_now;
    	}
    	else if(flag==3){
    		GLRenderer.CurMotion="Staircase";
    	}
    	else{
    		GLRenderer.CurMotion="Walking";
    		GLRenderer.StepCount=(int)steps_now;    		
    	}
    		
//		Log.e("UBIGREEN","Updating Greencoeff to "+Float.toString(credits));
    }
    public float Calculate(String str,long parameter)
    {
    	if(str == "Walking")
    	{
    		steps_now = parameter;
    		cnt++;
    		distance = (float)(0.726 * (steps_now - steps_prev));  // computing the distance traveled in walking
    		
    		if(steps_prev==steps_now)
    			Gcoefficient(distance,2);
    		if(steps_now>steps_prev)
    			Gcoefficient(distance,0);
        			
    		steps_prev = steps_now;
    		
    	}
    	else if(str=="Staircase"){
    		steps_now = parameter;
    		cnt++;
    		distance = (float)(0.526 * (steps_now - steps_prev));  // computing the distance traveled on staircase
    		
    		if(steps_prev==steps_now)
    			Gcoefficient(distance,2);
    		if(steps_now>steps_prev)
    			Gcoefficient(distance,3);
    		steps_prev = steps_now;    		
    	}
    	else if(str == "Automobile")
    	{
    		cnt++;
    		time_now = parameter;
    		wasted = (float)(.5*(time_now-time_pre));
    		time_pre = time_now;
    		Gcoefficient(wasted,1);
    	}
    	else{
    		GLRenderer.CurMotion="Stationary";
    		GLRenderer.StepCount=(int)steps_now;
    		
    		// raise error exception
    		//protected Dialog onCreateDialog(int id){
    			//Dialog dialog;
    		//	switch(id){
    			
    			//}
   		}
    	return credits;
   	} 
}

