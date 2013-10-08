package com.stepgreen.ubigreen;


import android.content.Context;
import oauth.signpost.OAuthConsumer;

public class Constants {
	/* 
	 * 
	 * CLIENT INFORMATION
	 * CLIENT NAME UBIGREEN
	 * Consumer Key: hkjnjEZPvkxbNk3nqzag

Consumer Secret: qzP6lOkRE2leFVO6Vtm61N0t6KEfZLRLobhTiAPv

Request Token URL http://www.stepgreen.org/oauth/request_token

Access Token URL http://www.stepgreen.org/oauth/access_token

Authorize URL http://www.stepgreen.org/oauth/authorize

We support hmac-sha1 (recommended).*/
	public static String CONSUMER_KEY = "hkjnjEZPvkxbNk3nqzag";
	public static String CONSUMER_SECRET = "qzP6lOkRE2leFVO6Vtm61N0t6KEfZLRLobhTiAPv";
	public static String REQUEST_TOKEN_URL = "http://www.stepgreen.org/oauth/request_token";
	public static String ACCESS_TOKEN_URL = "http://www.stepgreen.org/oauth/access_token";
	public static String AUTHORIZE_URL = "http://www.stepgreen.org/oauth/authorize";

	public static final String ENCODING = "UTF-8";
	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	public static final String API_REQUEST = "";
	public static String ACCESS_TOKEN = "";
	public static String ACCESS_TOKEN_SECRET = "";
	public static OAuthConsumer CONSUMER_GLOBAL;
	public static String postXML="";
	public static String getXML="";
	public static int operation=1;
	public static Context cont;
}