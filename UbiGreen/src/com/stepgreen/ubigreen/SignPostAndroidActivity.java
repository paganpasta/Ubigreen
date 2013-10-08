package com.stepgreen.ubigreen;

import java.io.BufferedReader;

import com.stepgreen.ubigreen.R;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.exception.OAuthCommunicationException;

public class SignPostAndroidActivity extends Activity {
	public static final int PICK_CONTACT    = 1;
	private SharedPreferences prefs;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //setting up the button to launch oauth
        Button launchOauth = (Button) findViewById(R.id.launchOAuth);
        Button clearCred = (Button) findViewById(R.id.clearCredentials);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        launchOauth.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		//start prepare request token activity to prepare request token parameters
        		Constants.cont=v.getContext();
        		startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));
        	}
        });
    }
}