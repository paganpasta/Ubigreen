package com.stepgreen.ubigreen;

import android.os.AsyncTask;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {
final String TAG = getClass().getName();
	
	private Context	context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	private SharedPreferences prefs;
	
	public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
		//filling up the passed parameters
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
		this.prefs=prefs;
	}

	/**
	 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret
	 * for future API calls.
	 */
	@Override
	protected Void doInBackground(Uri...params) {
		final Uri uri = params[0];
		final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

		try {
			//sending the request
			provider.retrieveAccessToken(consumer, oauth_verifier);
			final Editor edit = prefs.edit();
			edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
			edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
			edit.commit();
			String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
			String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			consumer.setTokenWithSecret(token, secret);
			Log.i("TOKEN!!!! ",token);
			Log.i("SECRET!!!! ",secret);
			Constants.ACCESS_TOKEN=token;
			Constants.ACCESS_TOKEN_SECRET=secret;
			Constants.CONSUMER_GLOBAL=consumer;
			// Now the ubigreen service is fully authorized to access user account
			//calling for OAuthflowapp that will retrieve actions and post actions to the user account on stepgreen
			Intent intent=new Intent(context,OAuthFlowApp.class);
			Log.e("In Try",oauth_verifier);
			context.startActivity(intent);
			Log.i(TAG, "OAUTH STAGE TWO OK!");
		} catch (Exception e) {
			Log.e(TAG, "OAUTH STAGE TWO ERROR", e);
		}

		return null;
	}


}
