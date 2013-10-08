package com.stepgreen.ubigreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.stepgreen.ubigreen.BaseService;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
public class xmlpuller extends Activity {
    /* Called when the activity is first created. */
    String url1="http://www.stepgreen.org/api/v1/actions/",url2=".xml?papi=zz1216ztNKfbnMJsiVVNaeIde7FaZa4c0";
;
List<String> ListActions=null;
List<Long>ActionTime=null;
@Override
    public void onCreate(Bundle savedInstanceState) {
     //   super.onCreate(savedInstanceState);
        Log.e("UBIGREEN","ENtered PUL");
        try {
        int i;
        ListActions=new ArrayList<String>();
        ActionTime=new ArrayList<Long> ();
        for(i=1;i<=5;i++){

            System.out.print("ENtered ihere in xml puller");
        	fluff(url1+i+url2);
        }
       BaseService.listactions=ListActions;
       BaseService.listtime=ActionTime;
       
} catch (XmlPullParserException e) {
// TODO Auto-generated catch block
e.printStackTrace();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
        
    }
String pro = "created_at";
    public void fluff (String urls)
    throws XmlPullParserException, IOException
    {
    URL url=new URL(urls);
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));   
 //   Log.e("URL==>  ",urls);
        String inputLine;
        String str="";
        while ((inputLine = in.readLine()) != null) {
        str+=inputLine;
        }
        //long req=(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*3600)+(Calendar.getInstance().get(Calendar.MINUTE)*60)+Calendar.getInstance().get(Calendar.SECOND);
        //System.out.println(""+req);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    factory.setNamespaceAware(true);
    XmlPullParser xpp = factory.newPullParser();
    xpp.setInput( new StringReader (str));
    int flag = 0;
    int eventType = xpp.getEventType();
    while (eventType != XmlPullParser.END_DOCUMENT) {
    String check = xpp.getName();
//    System.out.println("check => "+ check);
    if(eventType == XmlPullParser.START_TAG) {
    if(check.equals(pro) || check.equals("title"))
    flag = 1;
//     System.out.println("Start tag "+xpp.getName());
    eventType = xpp.next();
    continue;
    }
    if(eventType == XmlPullParser.END_TAG && flag == 1) {
    flag = 0;
       System.out.println("End tag "+xpp.getName());
    eventType = xpp.next();
    continue;
      } else if(eventType == XmlPullParser.TEXT) {
    if(flag == 1)
    {
    int sec = 0;
    String got = xpp.getText();
    if(got.charAt(10)=='T')
    {
    String time = got.substring(11,19);
    System.out.println("time => "+time);
    sec = (Integer.valueOf(""+time.charAt(0))*10 + Integer.valueOf(""+time.charAt(1)))*3600;
    sec += (Integer.valueOf(""+time.charAt(3))*10+ Integer.valueOf(""+time.charAt(4)))*60;
    sec += (Integer.valueOf(""+time.charAt(6)))*10 + Integer.valueOf(""+ time.charAt(7));
     System.out.println("SECONDS!!!!!   "+sec);
    ActionTime.add((long) sec);
    }
    else
    {
    String ttl = xpp.getText();
    System.out.println("title =>" + ttl);
    ListActions.add(ttl);
    }
//     System.out.println("Text "+xpp.getText());
   
    }
    eventType = xpp.next();
    continue;
    }
//     System.out.println("package => "+check + " tag => "+ pro);
  //   if(pro.equals(check))
    // {
//       System.out.println("package => "+check + " tag => "+ pro);
//     flag = 1;
 //   }
    eventType = xpp.next();
    }
    //System.out.println("End document");
    }
}