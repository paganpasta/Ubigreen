package com.stepgreen.ubigreen;
import net.rbgrn.android.glwallpaperservice.*;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import java.lang.Math;
import java.util.Calendar;
import java.util.Scanner;
import android.opengl.*;
import android.content.SharedPreferences;
import android.os.Environment;
import android.graphics.*;
import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.*;
import android.content.res.Resources;
public class GLRenderer implements GLWallpaperService.Renderer {
	public static Resources context;                              //Store Wallpaper Service's Resources
	public static Context actcontext;                             //Store Wallpaper Service's Context
	public static int  GreenCoeff=15;                             //Green Coeffecient Value
	public static String  CurMotion="Stationary";                 //Current Detected Motion
	public static int StepCount=0;                                //Duration of Detected Motion
	public int rotated=0;										  //Check the angle of rotation of device                                           
	public static boolean EnableBackground;                       //Enable Blue Gradient->Brown Gradient Background
	public static boolean EnableBackgroundOverlay;                //Enable Black Overlay For Night
	public static boolean EnableSunAndMoon;                       //Enable Sun And Moon
	public static boolean EnableTrees;                            //Enable Trees
	public static boolean EnableDrawPlatform;                     //Draw Platform
	public static boolean EnableGuy;                              //Draw Action performed
	public static boolean EnableTree;                             //Enable Custom Tree
	public static boolean EnableCustomBackground=false;           //Enable Custom Backgroung Fetching
	public static boolean EnableTouch=false;                      //Enable Touch Leaf Rendering
	public static boolean EnableInteraction=true;                 //Enable Touch Leaf
	public static float touchX,touchY;                            //X And Y Coords of Point of TOuching the Screen
	public static int leaftime=45;                                //Get the TOuch-Leaf's Duration Starts at 45 as angle of motion is best suited
	public int SimuConst=10;                                      //Manual SImulation
	public static int canvasHT,canvasWD;						  //Canvas Dimention
	static Bitmap sunbmp=null;                                      
	static Bitmap leaf=null;
	static Bitmap moonbmp=null;
	static Bitmap stationary=null;
	static Bitmap walking=null;
	static Bitmap car=null;
	static Bitmap stairs=null;
	static Bitmap lift=null;
	static Bitmap bike=null;
	static float guyx1,guyy1,guyx2,guyy2;
	static Bitmap[] treebmp={null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
	public static Bitmap[] bgpic={null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
	private float intendedBG[]={0.30980f,0.59215f,0.89411f};                              //Intended Background Colour
	private float intendedtime=0;                                                         //Intended part of the day 
	private float intendedsuncoord[]={1,1};                                               //Intended Coordinates of sun/moon
	private float currentsuncoord[]={1,1};                                                //Current Coordinates of sun/moon
	private float intendedtimeblackval=0;                                                 //Black Overlay Current Value
	private float currentBG[]={0.30980f,0.59215f,0.89411f};                               //Current Background Color
	Calendar cal=Calendar.getInstance();                                                  //Get Calender instance for time
	GLRenderer(Resources contextc,Context ActContext)
	{
		context = contextc;                                               				  //Initialise Context 
		actcontext=ActContext;          								                  //Initialise Resource
		int hr_frm_start=cal.get(Calendar.HOUR_OF_DAY);                                   //Calculate Time of the day(7AM to 7PM) or night(ELSE)
		if(hr_frm_start>=7&&hr_frm_start<19)
		{hr_frm_start-=7;intendedtime=0;}
		else if(hr_frm_start>12)
		{hr_frm_start-=19;intendedtime=1;}
		else
		{intendedtime=1;}
		intendedsuncoord[0]=-1+(hr_frm_start/12.0f)*2;                                   //Set  intended coordinated of the sun/moon
		intendedsuncoord[1]=0.8f-(modu(hr_frm_start-6)/6)*0.2f;
		currentsuncoord[0]=-1+(hr_frm_start/12.0f)*2;                                    //Update current coordinates of sun/moon to move towars intended value
		currentsuncoord[1]=0.8f-(modu(hr_frm_start-6)/6)*0.2f;	    
		init();                                                                          //Initialise variables
		loadsettings();                                                                  //Load Settings
	}
	public static void init()
	{
		EnableBackground=true;                                                           //Initially set to display a default wallpaper(hardcoded) 
		EnableBackgroundOverlay=true;
		EnableSunAndMoon=true;
		EnableTrees=true; 
		EnableDrawPlatform=true;
		EnableGuy=true;
		EnableTree=true;
		EnableInteraction=true;
		EnableCustomBackground=false;	
		guyx1=0;
		guyy1=0;
		guyx2=-0.9f;
		guyy2=-0.9f;                                                                     //Position guy at(0,0f)->(-0.9f,-0.9f) rectangle
		stationary=null;                                                                 //Initialise all Bitmaps to null
		walking=null;
		car=null;
		bike=null;
		lift=null;
		leaf=null;
		sunbmp=null;
		moonbmp=null;
		stairs=null;
		for(int i=0;i<=25;i++)
			bgpic[i]=null;	
		for(int i=0;i<=25;i++)
			treebmp[i]=null;	
		while(stationary==null)                                                         //Load All Bitmaps
			stationary=BitmapFactory.decodeResource(context,R.drawable.stationary);     //Keep retrying to load bitmap as SD card bus could be bottlenecked
		while(bike==null)
			bike=BitmapFactory.decodeResource(context,R.drawable.sun);
		while(car==null)
			car=BitmapFactory.decodeResource(context,R.drawable.car);
		while(leaf==null)
			leaf=BitmapFactory.decodeResource(context,R.drawable.leaf);
		while(lift==null)
			lift=BitmapFactory.decodeResource(context,R.drawable.lift);
		while(stairs==null)
			stairs=BitmapFactory.decodeResource(context,R.drawable.stairs);
		while(walking==null)
			walking=BitmapFactory.decodeResource(context,R.drawable.walking);
		while(sunbmp==null)
			sunbmp=BitmapFactory.decodeResource(context,R.drawable.sun);
		while(moonbmp==null)
			moonbmp=BitmapFactory.decodeResource(context,R.drawable.moon);
		while(treebmp[0]==null)
			treebmp[0]=BitmapFactory.decodeResource(context,R.drawable.tree0);
		while(treebmp[1]==null)
			treebmp[1]=BitmapFactory.decodeResource(context,R.drawable.tree1);
		while(treebmp[2]==null)
			treebmp[2]=BitmapFactory.decodeResource(context,R.drawable.tree2);
		while(treebmp[3]==null)
			treebmp[3]=BitmapFactory.decodeResource(context,R.drawable.tree3);
		while(treebmp[4]==null)
			treebmp[4]=BitmapFactory.decodeResource(context,R.drawable.tree4);
		while(treebmp[5]==null)
			treebmp[5]=BitmapFactory.decodeResource(context,R.drawable.tree5);
		while(treebmp[6]==null)
			treebmp[6]=BitmapFactory.decodeResource(context,R.drawable.tree6);
		while(treebmp[7]==null)
			treebmp[7]=BitmapFactory.decodeResource(context,R.drawable.tree7);
		while(treebmp[8]==null)
			treebmp[8]=BitmapFactory.decodeResource(context,R.drawable.tree8);
		while(treebmp[9]==null)
			treebmp[9]=BitmapFactory.decodeResource(context,R.drawable.tree9);
		while(treebmp[10]==null)
			treebmp[10]=BitmapFactory.decodeResource(context,R.drawable.tree10);
		while(treebmp[11]==null)
			treebmp[11]=BitmapFactory.decodeResource(context,R.drawable.tree11);
		while(treebmp[12]==null)
			treebmp[12]=BitmapFactory.decodeResource(context,R.drawable.tree12);
		while(treebmp[13]==null)
			treebmp[13]=BitmapFactory.decodeResource(context,R.drawable.tree13);
		while(treebmp[14]==null)
			treebmp[14]=BitmapFactory.decodeResource(context,R.drawable.tree14);
		while(treebmp[15]==null)
			treebmp[15]=BitmapFactory.decodeResource(context,R.drawable.tree15);
		while(treebmp[16]==null)
			treebmp[16]=BitmapFactory.decodeResource(context,R.drawable.tree16);                                //Load the 25 inbuilt trees
		while(treebmp[17]==null)
			treebmp[17]=BitmapFactory.decodeResource(context,R.drawable.tree17);
		while(treebmp[18]==null)
			treebmp[18]=BitmapFactory.decodeResource(context,R.drawable.tree18);
		while(treebmp[19]==null)
			treebmp[19]=BitmapFactory.decodeResource(context,R.drawable.tree19);
		while(treebmp[20]==null)
			treebmp[20]=BitmapFactory.decodeResource(context,R.drawable.tree20);
		while(treebmp[21]==null)
			treebmp[21]=BitmapFactory.decodeResource(context,R.drawable.tree21);
		while(treebmp[22]==null)
			treebmp[22]=BitmapFactory.decodeResource(context,R.drawable.tree22);
		while(treebmp[23]==null)
			treebmp[23]=BitmapFactory.decodeResource(context,R.drawable.tree23);
		while(treebmp[24]==null)
			treebmp[24]=BitmapFactory.decodeResource(context,R.drawable.tree24);
		while(treebmp[25]==null)
			treebmp[25]=BitmapFactory.decodeResource(context,R.drawable.tree25);

	}
	public static void loadsettings()
	{
		init();
		for(int i=0;i<=25;i++)
			bgpic[i]=null;	                                                                                        //Set Custom Background Images to null.
		SharedPreferences  sharedpref=actcontext.getSharedPreferences(UbiGreenWallpaperService.SHARED_PREFS_NAME,1);//Extract Instance of preference	
		String filevalue=sharedpref.getString("livewallpaper_testpattern", "Error");                                //Extract the value of folder from above
		String basepath=Environment.getExternalStorageDirectory() + "/Ubigreen/"+filevalue+"/";                     //Get the directory of content
		File f = new File(Environment.getExternalStorageDirectory() + "/Ubigreen/"+filevalue+"/details");           //Set Details file location
		if(!f.exists()||!f.canRead()||filevalue.equals("ERROR"))                                                    //If error in reading
		{        
			init();																									//Revert to default settings
			return;
		}
		try
		{
			InputStream in = new BufferedInputStream(new FileInputStream(f));	                                    //Set a Buffered Input for performance
			Scanner ts=new Scanner(in);                                                                             //Initialise Scanner
			Toast.makeText(actcontext, "Please Wait while the Wallpaper loads!", Toast.LENGTH_LONG).show();
			ts.nextLine();																							//Ignore 1st line as its the description 												
			while(ts.hasNext()) 
			{
				String command=ts.next();
				String value=ts.next();
				command.toLowerCase();
				value.toLowerCase();                                                                                //Extract Each COmmand from details and Set the corresponding variable to true or false 
				if(command.equals("background")&&value.equals("enable"))
					GLRenderer.EnableBackground=true;
				if(command.equals("background")&&value.equals("disable"))
					GLRenderer.EnableBackground=false;
				if(command.equals("backgroundoverlay")&&value.equals("enable"))
					GLRenderer.EnableBackgroundOverlay=true;
				if(command.equals("backgroundoverlay")&&value.equals("disable"))
					GLRenderer.EnableBackgroundOverlay=false;
				if(command.equals("touch")&&value.equals("enable"))
					GLRenderer.EnableInteraction=true;
				if(command.equals("touch")&&value.equals("disable"))
					GLRenderer.EnableInteraction=false;
			    if(command.equals("platform")&&value.equals("enable"))
					GLRenderer.EnableDrawPlatform=true;
				if(command.equals("platform")&&value.equals("disable"))
					GLRenderer.EnableDrawPlatform=false;
				if(command.equals("guy")&&value.equals("enable"))
					GLRenderer.EnableGuy=true;
				if(command.equals("guy")&&value.equals("custom"))                                                            //If Guy is set to custom
				{                                                                                                            //Extract images and load it
					GLRenderer.EnableGuy=true;
					stationary=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"stationary.png"),512,256,false);
					bike=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"bike.png"),512,256,false);
					walking=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"walking.png"),512,256,false);
					car=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"car.png"),512,256,false);
					lift=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"lift.png"),512,256,false);
					stairs=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+"stairs.png"),512,256,false);
					guyx1=ts.nextFloat();																					//Get guy's location
					guyy1=ts.nextFloat();
					guyx2=ts.nextFloat();
					guyy2=ts.nextFloat();
				}
				if(command.equals("guy")&&value.equals("disable"))
					GLRenderer.EnableGuy=false;

				if(command.equals("sunandmoon")&&value.equals("disable"))
				{GLRenderer.EnableSunAndMoon=false;}
				if(command.equals("sunandmoon")&&value.equals("enable"))
					GLRenderer.EnableSunAndMoon=true;
				if(command.equals("trees")&&value.equals("disable"))
					GLRenderer.EnableTrees=false;
				if(command.equals("trees")&&value.equals("enable"))
					GLRenderer.EnableTrees=true;
				if(command.equals("custombackground")&&value.equals("enable"))
				{
					String filename=ts.next();																					//If custom background
					GLRenderer.EnableCustomBackground=true;																		//Load each image and resize it to a power of 2 as per Opengl Restriction
					for(int i=0;i<=25;i++)
						while(GLRenderer.bgpic[i]==null)
							GLRenderer.bgpic[i]=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(basepath+filename+i+".png"), 512, 256, false);
	
				}
				if(command.equals("custombackground")&&value.equals("disable"))
					GLRenderer.EnableCustomBackground=false;		
	
			}
			ts.close();
			in.close();																											//Close All file Pointer
			Toast.makeText(actcontext, "Wallpaper Loaded!", Toast.LENGTH_LONG).show();
			
		}
		catch (Exception e)
		{
			Toast.makeText(actcontext, "Invalid details file", Toast.LENGTH_LONG).show();
			Toast.makeText(actcontext, "Reverting to default", Toast.LENGTH_LONG).show();
			init();																											//Any error? Rever to Default Values
		}

	}


	private float modu(float x)																								//Function to calculate Modulus
	{
		if(x>0)
			return x;
		else
			return -1*x;
	}
	private void drawBackground(GL10 gl) {
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);                                                                         //Enable Necessary Client States
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);																//Create Buffers to Put values in buffer to enable faster rendering
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer cbb = ByteBuffer.allocateDirect(4 * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		FloatBuffer _colorBuffer = cbb.asFloatBuffer();
		short _indicesArray[]={0,1,2,3};
		float[] coords = {
				1f, 1f, 0f, 
				-1f, 1f, 0f,
				1f, -0.7f, 0f,
				-1f,-0.7f,0f
		};
		float[] colors = {																								 //Initialise COlour Array
				currentBG[0],currentBG[1],currentBG[2],1f,
				currentBG[0],currentBG[1],currentBG[2],1f,
				currentBG[0]+0.33f,currentBG[1]+0.33f,currentBG[2]+0.33f,1f,
				currentBG[0]+0.33f,currentBG[1]+0.33f,currentBG[2]+0.33f,1f

		};
		if(currentBG[0]>intendedBG[0])                                                                                  //Update current Background COlour to match the intended one
			currentBG[0]-=0.004;
		else if(currentBG[0]<intendedBG[0])
			currentBG[0]+=0.004;

		if(currentBG[1]>intendedBG[1])
			currentBG[1]-=0.004;
		else if(currentBG[1]<intendedBG[1])
			currentBG[1]+=0.004;

		if(currentBG[2]>intendedBG[2])
			currentBG[2]-=0.004;
		else if(currentBG[2]<intendedBG[2])
			currentBG[2]+=0.004;

		_vertexBuffer.put(coords);                                                                                     //Insert values into respective buffers
		_indexBuffer.put(_indicesArray);
		_colorBuffer.put(colors);
		_vertexBuffer.position(0);
		_indexBuffer.position(0);
		_colorBuffer.position(0);
		gl.glColorPointer(4,GL10.GL_FLOAT,0,_colorBuffer);															  //Initialise the colorpointer with the color Buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);														  //Initialise the Coordinates with vertex Buffer
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, _indexBuffer);                           //Render Elements
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);                                                                 //Disable unused Client States
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}
	private void drawBackgroundOverlay(GL10 gl)
	{
		if(intendedtime==1)                                                                                           //Set Intended Black GRadient
			intendedtimeblackval+=0.01;
		if(intendedtime==0)
			intendedtimeblackval-=0.01;
		if(intendedtimeblackval<0)
			intendedtimeblackval=0;
		if(intendedtimeblackval>1)
			intendedtimeblackval=1;
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY); 																	//Enable Needed Client States
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_BLEND);                                                                                     //Enable Alpha Blending
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);															//Create COrespoinding buffers
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();
		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer cbb = ByteBuffer.allocateDirect(4 * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		FloatBuffer _colorBuffer = cbb.asFloatBuffer();
		short _indicesArray[]={0,1,2,3};
		float[] coords = {                                                                                              //Set Coordinates
				1f, 1f, 0f, 
				-1f, 1f, 0f,
				1f, -0.7f, 0f,
				-1f,-0.7f,0f
		};
		float[] colors = {
				0,0,0,intendedtimeblackval,																				//Set Colour
				0f,0f,0f,intendedtimeblackval,
				0,0,0,0,
				0,0,0,0
		};

		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_colorBuffer.put(colors);
		_vertexBuffer.position(0);																						//Fill the respective buffers
		_indexBuffer.position(0);
		_colorBuffer.position(0);
		gl.glColorPointer(4,GL10.GL_FLOAT,0,_colorBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);                                                        //Initialise pipeline for rendering with Buffer
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, _indexBuffer);                            //Render the texture 
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);                    											   //Disable the respective Client states
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
	}
	private void updateIBG()
	{
		intendedBG[0]=(228f-GreenCoeff*5.96f)/255f;																	  //Update the intended background RGB colour values;
		intendedBG[1]=151f/255f;
		intendedBG[2]=(20+79f+GreenCoeff*5.96f)/255f;  
	}
	void updateITime()
	{
		cal=Calendar.getInstance();																					  //Update Calendar Instance 
		float hr_frm_start=cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)/60.f;                               //Update the time value
		if(hr_frm_start>=7&&hr_frm_start<19)
		{hr_frm_start-=7;intendedtime=0;}
		else if(hr_frm_start>12)
		{hr_frm_start-=19;intendedtime=1;}
		else
		{intendedtime=1;}
		intendedsuncoord[0]=-1.2f+(hr_frm_start/12.0f)*2.1f;														 //Get current Sun Coordinates
		intendedsuncoord[1]=0.9f-(modu(hr_frm_start-6.0f)/6.0f)*0.4f;

		if(modu((currentsuncoord[0])-(intendedsuncoord[0]))>0.02)
			currentsuncoord[0]+=0.02;

		if(currentsuncoord[0]>0.9)
			currentsuncoord[0]=-1.2f;																				//Update Current Sun Coords


		if(modu(currentsuncoord[1]-intendedsuncoord[1])>0.02)
		{
			if(currentsuncoord[1]<intendedsuncoord[1])
				currentsuncoord[1]+=0.02;
			else if(currentsuncoord[1]>intendedsuncoord[1])
				currentsuncoord[1]-=0.02;
		}

	}
	private void drawplatform(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);																//Enable Needed Client States
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);														//create buffers
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer cbb = ByteBuffer.allocateDirect(4 * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		FloatBuffer _colorBuffer = cbb.asFloatBuffer();
		short _indicesArray[]={0,1,2,3};
		float[] coords = {																							//Create the values Array
				1f, -0.7f, 0f, 
				-1f, -0.7f, 0f, 
				1f, -1f, 0f,
				-1f,-1f,0f
		};
		float[] colors = {
				0.5921f,0.3098f,0.313f,1f,																			//Set the Colours values
				0.5921f,0.3098f,0.313f,1f,
				0.5921f,0.3098f,0.313f,1f,
				0.5921f,0.3098f,0.313f,1f
		};

		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_colorBuffer.put(colors);																					//Initialise Buffer with Colours
		_vertexBuffer.position(0);
		_indexBuffer.position(0);
		_colorBuffer.position(0);
		gl.glColorPointer(4,GL10.GL_FLOAT,0,_colorBuffer);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT, _indexBuffer);							//Render
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);																//Disable Enabled Clients
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}

	private void drawTree(GL10 gl,float[] coords,float redval,float greenval,float blueval,float alphaval)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);															   		//Enable Needed Client States
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);																				   
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());																		  		//Create Buffers 
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _textureBuffer = tbb.asFloatBuffer();

		short _indicesArray[]={0,1,2,3};
		float texture[] = {    		
				0.0f, 0.0f,		// top left		(V2)
				1.0f, 0.0f,		// bottom left	(V1)
				1.0f, 1.0f,		// top right	(V4)																	//Create Values Arrays
				0.0f, 1.0f		// bottom right	(V3)
		};

		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_textureBuffer.put(texture);

		_vertexBuffer.position(0);
		_indexBuffer.position(0);																						//Fill Buffer with Array
		_textureBuffer.position(0);
		while(treebmp[GreenCoeff]==null);
		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);																				//Load bitmap texures
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, treebmp[GreenCoeff], 0);												//Create Texture

		// Clean up
		//Bcause We dont want Color array so disable it
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);														  	//Enable Clients

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(redval, greenval, blueval, alphaval);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);																	//Render

		//Disable the client state before leaving
		//	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);															//Disable Client states

		gl.glDeleteTextures(1, textures, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);

	}	

	void drawTrees(GL10 gl)
	{

		for(float i=-1.5f;i<=1;i+=0.5)																					//Render 5 trees with given COordinates
		{
			float[] coords = {
					0f+i, 0.2f, 0, 
					1f+i, 0.2f, 0,
					1f+i, -0.7f, 0,
					0f+i,-0.7f,0
			};
			drawTree(gl,coords,1f,1f,1f,1f);

		}
	}
	void drawsun(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);           																	//Enable Client States
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);	
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);																			//Create Buffer
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _textureBuffer = tbb.asFloatBuffer();

		short _indicesArray[]={0,1,2,3};
		float texture[] = {    		
				0.0f, 0.0f,																											//Create Arrays
				1.0f, 0.0f,		
				1.0f, 1.0f,		
				0.0f, 1.0f		
		};

		float coords[]={
				currentsuncoord[0],currentsuncoord[1],0f,
				currentsuncoord[0]+0.7f,currentsuncoord[1],0f,
				currentsuncoord[0]+0.7f,currentsuncoord[1]-0.45f,0f,
				currentsuncoord[0],currentsuncoord[1]-0.45f,0f

		};


		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_textureBuffer.put(texture);
		_vertexBuffer.position(0);
		_indexBuffer.position(0);																									//Fill Buffer
		_textureBuffer.position(0);

		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		while(sunbmp==null||moonbmp==null);
		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		if(intendedtime==0)
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, sunbmp, 0);
		else
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, moonbmp, 0);
		// Clean up
		//		treebmp.recycle();
		//Bcause We dont want Color array so disable it
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(1f, 1f, 1f, 1f);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		//Disable the client state before leaving
		//	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDeleteTextures(1, textures, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	void drawguy(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);																												//Enable Clients
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());																										//Create Buffer
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _textureBuffer = tbb.asFloatBuffer();

		short _indicesArray[]={0,1,2,3};
		float texture[] = {    		
				0.0f, 0.0f,		
				1.0f, 0.0f,		
				1.0f, 1.0f,																														//Create Values
				0.0f, 1.0f		
		};

		float coords[]={
				guyx1,guyy1,0f,
				guyx2,guyy1,0f,
				guyx2,guyy2,0f,
				guyx1,guyy2,0f

		};


		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_textureBuffer.put(texture);																											//Fill Buffer
		_vertexBuffer.position(0);
		_indexBuffer.position(0);
		_textureBuffer.position(0);

		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);													
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);


		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		//Get the appropriate 
		while(stationary==null||walking==null||car==null||stairs==null);																		//Wait till paralell thread loads image
		if(CurMotion=="Stationary")
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,stationary , 0);
		else if(CurMotion=="Automobile")
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,car , 0);
		else if(CurMotion=="Walking")
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,walking , 0);
		else if(CurMotion=="Staircase")
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,stairs , 0);
		else
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, walking, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(1f, 1f, 1f, 1f);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		//Disable the client state before leaving
		//	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDeleteTextures(1, textures, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	public static void drawTouchLeaf(GL10 gl)
	{
		float xco=touchX;
		float yco=touchY;
		leaftime++;
		touchY+=-0.005f*Math.abs(Math.sin(Math.PI*leaftime/180.0f));
		touchX+=0.018f*Math.sin(Math.PI*leaftime*2/180.0f);
	
		if(touchY>1||touchY<-1)
		{EnableTouch=false;leaftime=0;}
		if(!EnableTouch)
			return;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);																										//Enable CLients
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());																							   //Create Buffers
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _textureBuffer = tbb.asFloatBuffer();

		short _indicesArray[]={0,1,2,3};
		float texture[] = {    		
				0.0f, 0.0f,		
				1.0f, 0.0f,		
				1.0f, 1.0f,		
				0.0f, 1.0f		
		};

		float coords[]={
				xco-0.1f,yco+0.1f,0f,
				xco+0.1f,yco+0.1f,0f,																									//Set Buffers
				xco+0.1f,yco-0.1f,0f,
				xco-0.1f,yco-0.1f,0f

		};


		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_textureBuffer.put(texture);
		_vertexBuffer.position(0);																										//Fill Buffer
		_indexBuffer.position(0);
		_textureBuffer.position(0);

		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		while(leaf==null);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, leaf, 0);
		// Clean up
		//treebmp.recycle();
		//Bcause We dont want Color array so disable it
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(1f, 1f, 1f, 1f);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		//Disable the client state before leaving
		//	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDeleteTextures(1, textures, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}

	
	void drawbgp(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);	
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
																																			//Set CLient State
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();																						//Create buffer

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _textureBuffer = tbb.asFloatBuffer();

		short _indicesArray[]={0,1,2,3};
		float texture[] = {    		
				0.0f, 0.0f,		
				1.0f, 0.0f,		
				1.0f, 1.0f,																													//Create Array
				0.0f, 1.0f		
		};

		float coords[]={
				-1f,1f,0f,
				1f,1,0f,
				1f,-1f,0f,
				-1f,-1f,0f

		};


		_vertexBuffer.put(coords);
		_indexBuffer.put(_indicesArray);
		_textureBuffer.put(texture);
		_vertexBuffer.position(0);
		_indexBuffer.position(0);																											//Fill Buffer
		_textureBuffer.position(0);

		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		while(bgpic[GreenCoeff]==null);
		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,bgpic[GreenCoeff], 0);


		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(1f, 1f, 1f, 1f);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		//Disable the client state before leaving
		//	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDeleteTextures(1, textures, 0);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}
	public void onDrawFrame(GL10 gl) {
																											//Render Frame
		long frameStartTime = System.currentTimeMillis();
		// 	if(prevTime==0||modu(frameStartTime-prevTime)>timer)
		{

			gl.glClearColor(0f, 0f, 0f, 1.0f);																//Clear Background to Black
			updateIBG();
			updateITime();																				   //Call needed Functions
			if(EnableBackground)
				drawBackground(gl);
			if(EnableCustomBackground)
				drawbgp(gl);
			if(EnableBackgroundOverlay)
				drawBackgroundOverlay(gl);
			if(EnableSunAndMoon)
				drawsun(gl);   
			if(EnableTrees)
				drawTrees(gl);
			if(EnableDrawPlatform)
				drawplatform(gl);
			if(EnableTouch&&EnableInteraction)
				drawTouchLeaf(gl);
			if(EnableGuy)
				drawguy(gl);

		}

	}    

	public void onSurfaceChanged(GL10 gl, int width, int height) {

		gl.glViewport(0,0,width, height);
		{gl.glRotatef(360-rotated, 0, 0, 1);rotated=0;}
		if(width>height)																											//Disable Rotation
		{float angle=90;gl.glRotatef(angle,0,0,1);rotated+=angle;}
        canvasHT=height;
        canvasWD=width;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}



	public void release() {
	}

}
