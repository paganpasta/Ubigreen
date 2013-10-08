package com.stepgreen.ubigreen;

import it.sauronsoftware.base64.Base64;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.stepgreen.ubigreen.Constants;


public class PrepareRequestTokenActivity extends Activity {
	final String TAG = getClass().getName();
	private OAuthConsumer consumer; 
    private OAuthProvider provider;
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       	try {
           	System.setProperty("debug", "true");
           	//construct consumer and provider variables with the needed paramters
       	      this.consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
       	        this.provider = new CommonsHttpOAuthProvider(
       	        		Constants.REQUEST_TOKEN_URL,
       	        		Constants.ACCESS_TOKEN_URL,
       	        		Constants.AUTHORIZE_URL);
           	} catch (Exception e) {
           		Log.e(TAG, "Error creating consumer / provider",e);
       		}
       		//calling for the class that sends a request for retrieving the request tokens
           Log.i(TAG, "Starting task to retrieve request token.");
    	   new OAuthRequestTokenTask(this,consumer,provider).execute();
    }

/*    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
	   			System.setProperty("debug", "true");
	   			this.consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);	
	   	        this.provider = new CommonsHttpOAuthProvider(
	   	        		Constants.REQUEST_TOKEN_URL,//  + "?scope=" + URLEncoder.encode(Constants.SCOPE, Constants.ENCODING),
	   	        		Constants.ACCESS_TOKEN_URL,
	   	        		Constants.AUTHORIZE_URL);
	       	} catch (Exception e) {
	       		Log.e("error", "Error creating consumer / provider",e);
	   		}
	       	

	       Log.i("error", "Starting task to retrieve request token.");
		//new OAuthRequestTokenTask(this,consumer,provider).execute();
	       new OAuthRequestTokenTask(this,consumer,provider).execute();

	}*/
	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */

	public void onNewIntent(Intent intent) {
		//fetching response parameters
		Log.i(TAG, "This should be printed..!");
		super.onNewIntent(intent);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		//asdf//
		if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
			Log.i(TAG, "Callback received : " + uri);
			Log.i(TAG, "Retrieving Access Token");
			// calling for the class that sends a request for retrieving the access tokens 
			new RetrieveAccessTokenTask(this,consumer,provider,prefs).execute(uri);
			finish();
		}
	}


}
