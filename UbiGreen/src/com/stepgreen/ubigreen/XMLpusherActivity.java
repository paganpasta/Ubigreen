package com.stepgreen.ubigreen;

import java.util.ArrayList;
import java.util.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class XMLpusherActivity extends Activity {
    /** Called when the activity is first created. */
    String str="Automobile";
    int walk;				// counting the number of times walking activity is detected.
    int stat;				// counting the number of times stationary activity is detected.
    int stair;				// counting the number of times stair activity is detected.
    int auto;				// counting the number of times automobile activity is detected.
    Queue<Integer> Q=(Queue<Integer>) new ArrayList<Integer> ();
    Queue<Integer> newacts=(Queue<Integer>)new ArrayList<Integer>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	createxml(str);  						// calling the function to create Xml.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        stat=walk=stair=auto=0;
        Log.e("UBIGREEN","XMLPUSHER:RAN");
    }
    // Function called to create final xml to be updated on the server
    public void upd_on_server(){
    	int type;
    	String finalstr ="<user_action> <assumptions/> <recurring type='boolean'>";
    	String ttl=null,rectype=null,tag1=null,tag2=null,uploadxml="";
    	if(newacts==null)
    		return;
    	while(newacts.size()!=0){

        	Log.e("UBIGREEN", "XMLPUSHER: Entered");
    		type=newacts.element();
    		uploadxml="";
    		if(type==1){							// Type -> Automobile
    			ttl = "Used car for 1 km"; 
    			rectype = "false";
    			tag1 = "travel";
    			tag2 = "not-green";    			
    		}
    		else if(type==3){
    			ttl = "Used Staircase instead of elevator"; 		// Type -> Staircases
    			rectype = "true";
    			tag1 = "travel";
    			tag2 = "green";
    		}
    		else{
    			ttl = "Walked for 1 km instead of using a motorized vehicles";	 // Type-> Walking
    			rectype = "true";
    			tag1 = "travel";
    			tag2 = "green";    			
    		}
    		newacts.remove();
    		// creating uploadxml which is the final string to be posted.
    		uploadxml = finalstr + rectype + "</recurring> <title>"+ttl+"</title> <tags> <tag>" + tag1 + "</tag> <tag>" + tag2 + "</tag> </tags> </user_action>";
    		System.out.println(uploadxml);
    		Constants.postXML=uploadxml;
    		Constants.operation=3;
    		Intent dialogIntent = new Intent(getBaseContext(), SignPostAndroidActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);    
    	}
    }
    public void createxml(String str)
    {
    	if(true)
    	return;
    	// Final xml string to be posted on the server
    	Log.e("stairs+walk  ",""+(stair+walk)+"   "+newacts.size());
    	if(str.equals("Automobile"))			// if the action is using Automobile
    	{
    		auto++;
    		Q.add(1);
    		if(auto>=2000){
    			auto=0;
    			newacts.add(0);
    		}
    	}
    	else if(str.equals("Stationary"))  		// If the action is stationary
    	{
    		Q.add(2);
    		stat++;
    	}
    	else if(str.equals("Staircase"))		// If the action is using staircases 
    	{
    		Q.add(3);
    		stair++;
    		if(stair+walk==5){
    			stair=0;
    			newacts.add(4);
    		}
      	}
    	else if(str.equals("Walking"))			// If the action is walking
    	{
    		Q.add(4);
    		walk++;
    		if(walk+stair==5){
    			walk=0;
    			newacts.add(4);
    		}
    	}
    	else
    	{
    	}
    	int type=0;
    	if(Q.size()>1000){
    		type=Q.element();
    		Q.remove();
    		if(type==1)
    			auto--;
    		else if(type==2)
    			stat--;
    		else if(type==3)
    			stair--;
    		else
    			walk--;
    	}
    }
}