package com.stepgreen.ubigreen;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import com.stepgreen.ubigreen.Coefficient_calculatorActivity;
import com.stepgreen.ubigreen.XMLpusherActivity;
public class DetectionActivity extends Service implements SensorEventListener  {
	/** Called when the activity is first created. */
	SensorManager mSensorManager; 
	Sensor mag_sens,acc_sens;
	float Scale_factor;
	float Offset;
	int steps,invoked;
	Timer tmer=null,tmer2=null;
	float PrevVal;
	float PrevExtreme[],limit;
	float Prevdiff,PrevType;
	int Prevdir;
	long totsteps=0,updated_on_server=0;
	Coefficient_calculatorActivity inst=null; //To update green coefficient
	XMLpusherActivity inst_XML=null; //To POST XML on the stepgreen server 
	String status;
	Queue<Long> times= null;
	Queue<Float> peak=null;
	TextView Vid,Vid2,Vid3,Vid4;
	long autotime=0;
	@Override
	public void onCreate() { //Various initializations required at the beginning of the program.
		super.onCreate();
		mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		acc_sens=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Scale_factor=(-120.0f/(SensorManager.STANDARD_GRAVITY));
		Offset=240;
		times=new LinkedList<Long>();
		peak=new LinkedList<Float>();
		tmer=new Timer();
		PrevExtreme=new float[3];
		inst=new Coefficient_calculatorActivity();
		inst_XML=new XMLpusherActivity();
		limit=10;
		//        Vid2=(TextView)findViewById(R.id.textView1);
		//        Vid3=(TextView)findViewById(R.id.textView3);
		//        Vid4=(TextView)findViewById(R.id.text4);
		status="Computing...";
		mSensorManager.registerListener(this,acc_sens,SensorManager.SENSOR_DELAY_FASTEST);
		//        Vid2.setText(status);
		tmer.scheduleAtFixedRate(new popandupdate(),0,1000);
	}
	public void onSensorChanged(SensorEvent event) {// Every time the sensor changes its values, this method is called. 
		synchronized(this){ //Since there would be parallel threads created, we don't want all of them to modify the  queue randomly
			int i,cdir; //This prevents the threads from interfering and causing errors while accessing the same memory.
			float sum=0; //Heuristic Algorithm to detect walking(This is similar  to kalman's filtering).
			for(i=0;i<3;i++)
				sum+=(Offset+event.values[i]*Scale_factor);
			sum/=3.0;
			if(sum<PrevVal)
				cdir=-1; //get whether subject is moving up or down. 
			else
				cdir=1;
			if(-Prevdir==cdir){ //For it to be a spike, it has to have the opposite direction as the direction of the last 2 points.
				float diff;
				int type;
				type=(cdir>0?0:1);
				PrevExtreme[type]=PrevVal; //If cdir is positive(rising),then the previous two points must be falling and vice versa.
				diff=PrevExtreme[0]-PrevExtreme[1];
				diff=((diff>0)?diff:-diff); //Calculating absolute value of difference b/w upper and lower extreme of the previous spike.
				if(limit<diff){
					if((diff>(Prevdiff*2.0f/3.0f))&&(diff<(Prevdiff*3.0f))&&(PrevType==type || PrevType==-1)){ //Setting some limits on the height of spikes 
						times.add(getseconds()); //based on some simple experiments done.
						invoked=4;
						peak.add(PrevExtreme[0]);
						peak.add(PrevExtreme[1]);
						steps++;    //Counts the number of steps.
						totsteps++;
						// Vid3.setText(Integer.toString(steps)+" Steps/Minute");
						PrevType=type; //The next spike needs to be of this type(basically it 
						//takes care that small deviations(or falls) in the rising curve are ignored).
					}
					else
						PrevType=-1; //The next spike can have any type.
				}
				Prevdiff=diff;
			}
			PrevVal=sum;
			Prevdir=cdir;
		}
	}
	protected void onPause(){ //When application pauses this is invoked.
		// mSensorManager.unregisterListener(this);
	}
	protected void onResume(){ //When application resumes the sensors have to be started again.
	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	public long getseconds(){ // Returns the number of seconds from the beginning of the year
		long ctime1=0;
		ctime1+=(Calendar.getInstance().get(6)-1)*86400;
		ctime1+=(Calendar.getInstance().get(11)-1)*3600;
		ctime1+=(Calendar.getInstance().get(12)-1)*60;
		ctime1+=Calendar.getInstance().get(13);
		return ctime1;
	}
	public final Handler hand=new Handler(){ //Handler invoked by popandupdate which is used to pop all elements in 
		@Override
		public void handleMessage(Message msg){ //queue where time<(current_time-60) (i.e. number of spikes in the past 1 minute)
			long ctime=getseconds()-60; //It also tells the other class what is the number of steps per minute so that
			if(steps!=0){ //other things can be calculated.
				while(times.element()<ctime){ // Chuck out times which are older than a minute
					times.remove();
					steps--;
					peak.remove(); //Also remove corresponding peak values
					peak.remove();
					if(steps==0)
						break;
				}
			}
			updated_on_server++;
			Log.e("UPDATED x times"," where x="+updated_on_server);
			if(updated_on_server==10){ // Update all the actions done by the user on the stepgreen server everyday. 
				updated_on_server=0;
				inst_XML.upd_on_server();
			}
			int minacc=0,maxacc=0,delta=2,stairmin=0,stairmax=0;
			Queue<Float> Stmp=new LinkedList<Float>();
			while(Stmp.size()!=0)
				Stmp.remove();
			while(peak.size()!=0){
				maxacc+=(peak.element()>218?1:0); //Checking for Car (upperbound)
				minacc+=(peak.element()<201?1:0); //Lower bound for Car
				stairmax+=(peak.element()>225?1:0); //Checking for Stair Case(upperbound)
				stairmin+=(peak.element()<185?1:0); //Lower Bound for Stair Case
				Stmp.add(peak.element());
				peak.remove();
			}
			while(Stmp.size()!=0){
				peak.add(Stmp.element());
				Stmp.remove();
			}
			if(invoked>=0)
				invoked--;
			if(steps==0 || invoked<0){ //If Sensor Has not changed since a long time, the device must be stationary
				status="Stationary";
				autotime=0;
				inst.Calculate("Stationary", totsteps);
				inst_XML.createxml("Stationary");
			}
			else if(stairmax>=delta && stairmin>=delta && 2*steps-stairmax-stairmin<stairmax+stairmin){ //If staircase, number of peaks outside the range 
				status="Staircase"; //should be greater than those in the range
				autotime=0;
				inst.Calculate("Staircase", totsteps);
				inst_XML.createxml("Staircase");
			}
			else if(2*steps-maxacc-minacc>=minacc+maxacc){ //If Car, values in the range should be greater than 
				status="Automobile"; //number of values out of range
				autotime++;
				inst.Calculate(status, autotime);
				inst_XML.createxml("Automobile");
			}
			else{
				status="Walking";
				autotime=0;
				inst.Calculate(status, totsteps);
				inst_XML.createxml("Walking");
			}
			/* if(steps==0)
GLRenderer.CurMotion="Stationary";
else
GLRenderer.CurMotion="Walking";
GLRenderer.StepCount=steps;
Vid4.setText("Green Coefficient: "+Float.toString(tmp));
Vid2.setText(status);
Vid3.setText(Integer.toString(steps)+" Steps/Minute");*/
		}
	};
	public class popandupdate extends TimerTask{//It needs to be run every few seconds using Timer and hence a TimerTask needs to be created.
		public void run(){
			hand.sendEmptyMessage(0);
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}