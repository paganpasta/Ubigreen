package com.stepgreen.ubigreen;
import 	android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.content.res.Resources;
import android.content.Intent;
import android.view.MotionEvent;
import net.rbgrn.android.glwallpaperservice.*;
public class UbiGreenWallpaperService extends GLWallpaperService {
	public static final String	SHARED_PREFS_NAME	= "livewallpapersettings";
	Resources contextwra;
	Context actContext;
	public UbiGreenWallpaperService() {
		super();
	}
	@Override
	public Engine onCreateEngine() {
		Intent serviceIntent = new Intent();                                                //Create A New Intent
		contextwra=this.getResources();
		actContext=this.getApplicationContext();	
		serviceIntent.setAction("com.stepgreen.ubigreen.BaseService");						//Run the Base Service Class
		startService(serviceIntent);
		Intent serviceIntent2 = new Intent();
		contextwra=this.getResources();
		serviceIntent2.setAction("com.stepgreen.ubigreen.DetectionActivity");               //Run the Detection Activity
		startService(serviceIntent2);
		MyEngine engine = new MyEngine();
		return engine;
	}

	class MyEngine extends GLEngine {
		GLRenderer renderer;
		public MyEngine() {
			super();

			// handle prefs, other initialization
			renderer = new GLRenderer(contextwra,actContext);                              //Create the GLRenderer Class
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);                                        //Render Continuously
			setTouchEventsEnabled(true);                              					   //Enable TOuch Events
		}

		int tcount=0;
		@Override
		public void onTouchEvent(MotionEvent event){
			Display display=((WindowManager)actContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			GLRenderer.EnableTouch=true;
			GLRenderer.leaftime=45;
			float hfwidth=display.getWidth()/2.0f;
			float hfheight=display.getHeight()/2.0f;                                                    //On Touch Events
            Log.e("UBIGREEN",GLRenderer.canvasHT/2.0f+":"+GLRenderer.canvasWD/2.0f);
            Log.e("UBIGREEN",hfheight+":"+hfwidth);
            if(GLRenderer.canvasHT!=0&&GLRenderer.canvasWD!=0)											//Get the Rotated Ht and width
            {hfwidth=GLRenderer.canvasWD/2.0f;hfheight=GLRenderer.canvasHT/2.0f;}
			GLRenderer.touchX=(event.getX()-hfwidth)/hfwidth;           								//Set the coordinates and enable it
			GLRenderer.touchY=(event.getY()-hfheight)/(-hfheight);
		}     
		public void onDestroy() {
			super.onDestroy();
			if (renderer != null) {
				renderer.release();
			}
			renderer = null;
		}
	}
}