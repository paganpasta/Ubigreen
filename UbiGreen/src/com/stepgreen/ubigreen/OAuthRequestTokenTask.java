package com.stepgreen.ubigreen;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;

import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import android.os.AsyncTask;

public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {
	final String TAG = getClass().getName();
	private Context	context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	
	public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider) {
		//filling up the passed request paramters
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
	}
	
		 
	 // Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
	 protected Void doInBackground(Void... params) {
		try {
			//sending the request
			final String url = provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
//			Log.i(TAG, "Popping a browser with the authorize URL : " + url);
			//starting the browser and authorizing the retrieved request token
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
			//creates a separate thread to prepare a request for retrieving the access tokens 
			context.startActivity(intent);
			Uri uri=intent.getData();
//			Log.e("VERIFIER!!!! ",consumer.getToken());	
//s			Log.e("VERIFIER!!!! ",OAuth.OAUTH_VERIFIER);
			

		} catch (Exception e) {
			Log.e(TAG, "Error during OAUth retrieve request token", e);
		}

		return null;
	}

	 
	 //Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
	 
/*	@Override
	protected Void doInBackground(Void... params) {

			try {

				
				//final String url = provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
				final String url = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
				Log.i(TAG, "retrieved request tokens..!!" + consumer.getToken().toString());
				Log.i(TAG, "retrieved request tokens..!!" + consumer.getTokenSecret().toString());
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
				context.startActivity(intent);
				Log.i(TAG, "after startactivity(intent)");

			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
			}
		return null;
	}*/
	
	

}
