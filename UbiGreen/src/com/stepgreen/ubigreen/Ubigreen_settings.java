package com.stepgreen.ubigreen;
import android.content.SharedPreferences;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.os.Bundle;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.widget.Toast;
import java.util.ArrayList;
import com.stepgreen.ubigreen.SignPostAndroidActivity;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.util.Log;
import com.stepgreen.ubigreen.GLRenderer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.lang.reflect.Array;
import com.stepgreen.ubigreen.UbiGreenWallpaperService;
import com.stepgreen.ubigreen.R;
import java.util.List;
public class Ubigreen_settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		getPreferenceManager().setSharedPreferencesName(UbiGreenWallpaperService.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.ubigreen_wallpaper_preference);
		File f = new File(Environment.getExternalStorageDirectory() + "/Ubigreen");											//Create A Ubigreen folder in SDCARD if doesnt exist
		if(!f.isDirectory())
			f.mkdirs();	  
		Log.e("UBIGREEN",f.getPath()+ " is it a dir??" + f.isDirectory());													//
		if(!f.isDirectory())
		{
			Toast msg = Toast.makeText(this.getApplicationContext(), "Unable to Access SDCARD", Toast.LENGTH_LONG);
			msg.show();
			return;   
		}
		ListPreference lp = (ListPreference)findPreference("livewallpaper_testpattern");
		List<String> temp1=Arrays.asList(f.list());
		List<String> temp2=Arrays.asList(getResources().getStringArray(R.array.AvailWallpaperVal));
		List<String> wallval=new ArrayList();
		for(int i=0;i<temp1.size();i++)
			if((new File(Environment.getExternalStorageDirectory() + "/Ubigreen/"+temp1.get(i))).isDirectory())
				wallval.add(temp1.get(i));          
		for(int i=0;i<temp2.size();i++)
			wallval.add(temp2.get(i));

		List<String> wallname=new ArrayList();

		temp2=Arrays.asList(getResources().getStringArray(R.array.AvailWallpaper));  
		for(int i=0;i<temp1.size();i++)
			if((new File(Environment.getExternalStorageDirectory() + "/Ubigreen/"+temp1.get(i))).isDirectory())
			{
				File tread=new File(Environment.getExternalStorageDirectory() + "/Ubigreen/"+temp1.get(i)+"/details");
				try
				{
					InputStream in = new BufferedInputStream(new FileInputStream(tread));
					Scanner ts=new Scanner(in);
					String inp=ts.nextLine();
					ts.close();																							//Get the description from 1st line of details in each subfolder
					in.close();
					wallname.add(inp);
				}
				catch (Exception e)
				{
				
				}



			}
		for(int i=0;i<temp2.size();i++)
			wallname.add(temp2.get(i));

		String[] Wallval = wallval.toArray(new String[wallval.size()]);  
		String[] Wallname = wallname.toArray(new String[wallname.size()]);  
		lp.setEntries(Wallname);
		lp.setEntryValues(Wallval);
		lp.setDefaultValue("jtrees");																										//Set Default Value
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		this.onCreate(null);
	}

	@Override
	protected void onDestroy()
	{
		getPreferenceManager().getSharedPreferences()
		.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		if(key.equals("livewallpaper_testpattern")||key.equals("Refresh"))
		{
			SharedPreferences.Editor edit=sharedPreferences.edit();
			edit.putBoolean("Refresh",false);
			edit.commit();
			Toast.makeText(getApplicationContext(), "Reloading All Resources! Please Wait!", Toast.LENGTH_LONG).show();
			GLRenderer.loadsettings();									  //Reload Setting on Changing selection
		}
		if(key.equals("Login"))
		{
		
			Intent dialogIntent = new Intent(getBaseContext(), SignPostAndroidActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);                 //Launch Oauth Flow
			SharedPreferences.Editor edit=sharedPreferences.edit();
			edit.putBoolean("Login",false);							  //Set Login checkbox to false again
			edit.commit();
		}
	}
}
