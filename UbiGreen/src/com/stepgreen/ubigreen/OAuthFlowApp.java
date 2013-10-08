package com.stepgreen.ubigreen;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.regex.*;
// Entry point in the application. Launches the OAuth flow by starting the PrepareRequestTokenActivity


public class OAuthFlowApp extends Activity {

	final String TAG = getClass().getName();
	public static String User;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button launchOauth = (Button) findViewById(R.id.launchOAuth);
		User=null;

		launchOauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));
			}
		});
		if(Constants.operation==1)
			performApiCall();
		else if(Constants.operation==2)
			try {
				getXML(Constants.getXML);
			} catch (OAuthMessageSignerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			else if(Constants.operation==3)
				try {
					postXML(Constants.postXML);
				} catch (OAuthMessageSignerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OAuthExpectationFailedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OAuthCommunicationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	}
	// retrieving xml 
	public String getXML(String urls) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException{
		OAuthConsumer consumer=Constants.CONSUMER_GLOBAL;
		URL url=new URL(urls);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url.toString());
		consumer.sign(request);
		HttpResponse response=httpclient.execute(request);
		InputStream data = response.getEntity().getContent();
		BufferedReader in = new BufferedReader(new InputStreamReader(data));
		String inputLine;
		String xmlread="";
		while ((inputLine = in.readLine()) != null) {
			xmlread+=inputLine;
		}
		return xmlread;
	}
	public void postXML(String uploadxml) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException{
		OAuthConsumer consumer=Constants.CONSUMER_GLOBAL;
		if(User==null)
			return;
		uploadxml="<user_comment> Just walked a kilometer</user_comment>";
		String urls="http://www.stepgreen.org/api/v1/actions/3/comments.xml";//+User+"/user_actions.xml";
		DefaultHttpClient httpclient = new DefaultHttpClient();  
		URL url=new URL(urls);
		HttpPost request=new HttpPost(url.toString());
		StringEntity se=new StringEntity(uploadxml,HTTP.UTF_8);
		se.setContentType("text/xml");
		request.setHeader("Content-Type","application/xml;charset=UTF-8");
		consumer.sign(request);
		request.setEntity(se);
		HttpResponse httpresponse = httpclient.execute(request);
		HttpEntity resEntity = httpresponse.getEntity();
		Log.e("IN POST XML!!!  ",EntityUtils.toString(resEntity));        
	}
	// extracting username from the retrieved action xml
	private void performApiCall() {
		try {
			TextView textView = (TextView) findViewById(R.id.response_code);
			String xmlread=getXML("http://www.stepgreen.org/api/v1/users/current_user.xml");
			String res=Pattern.compile("href=\"/api/v1/users/").split(xmlread)[1];
			User=Pattern.compile(".xml\"").split(res)[0];
			textView.setText("Welcome "+User);
		} catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
		}
	}

}