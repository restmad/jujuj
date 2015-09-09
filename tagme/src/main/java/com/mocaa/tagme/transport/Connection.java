package com.mocaa.tagme.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.mocaa.tagme.download.ImageLoaderImpl;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Connection{
	public static final String URL_FILE = "http://launchy-yidesk.stor.sinaapp.com";
	public static final String URL_MAIN = "http://tagme.sinaapp.com";
	public static final String URL_DOWNLOAD = "http://tagme-tagme.stor.sinaapp.com/apks-daily/Tag.me.apk";
	private static final String TAG = "Connection";

	public int sendRequestForInteger(String servlet, ArrayList<NameValuePair> params){

		String str = sendRequestForString(servlet, params);
		if(str != null){
		    try {
				return Integer.parseInt(str.trim());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		return -2;
	}

	public String sendRequestForString(String servlet, ArrayList<NameValuePair> params){
		String url = URL_MAIN+"/"+servlet;
		System.out.println(url);
		HttpPost httpRequest =new HttpPost(url);  
//		httpRequest.addHeader("Content-Type", "text/html");
//		httpRequest.addHeader("charset", HTTP.UTF_8);      
	    try{
	    	UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
	    	httpRequest.setEntity(entity);
	    	HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);  

	    	if(httpResponse.getStatusLine().getStatusCode()==200){
	    		String str = EntityUtils.toString(httpResponse.getEntity());
	    		System.out.println("result:"+str);
	    		return str;
	    	} else{
	    		System.out.println("http error:"+httpResponse.getStatusLine().getStatusCode());
	    	}
	    }catch(Exception e){  
	    	e.printStackTrace();  
	    } 
	    return null;
	}
	
	
	  
	public JSONObject sendRequestForObj(String servlet, ArrayList<NameValuePair> params){

		String json = sendRequestForString(servlet, params);
	    if(json != null){
	    	JSONObject obj = null;
			try {
				obj = new JSONObject(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return obj;
	    }
	    
	    return null;
	}

	public String uploadPic(String servlet, Pair pair, String picPath)
    {
		System.out.println("pic path:"+picPath);
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
        try
        {
    		String url = "";
    		if(pair == null){
    			url = URL_MAIN+"/"+servlet;
    		}else{
    			url = URL_MAIN+"/"+servlet+"?"+pair.parseURL();
    		}
        	Log.d("http url", url);
        	URL u =new URL(url);
        	HttpURLConnection conn=(HttpURLConnection)u.openConnection();
        	conn.setDoInput(true);
        	conn.setDoOutput(true);
        	conn.setUseCaches(false);
        	conn.setRequestMethod("POST");
        	/* setRequestProperty */
        	conn.setRequestProperty("Connection", "Keep-Alive");
        	conn.setRequestProperty("Charset", "UTF-8");
        	conn.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        	DataOutputStream ds =
        			new DataOutputStream(conn.getOutputStream());
        	ds.writeBytes(twoHyphens + boundary + end);
        	ds.writeBytes("Content-Disposition: form-data; "+
                      "name=\"file\";filename=\""+
                      "image" +"\""+ end);
        	ds.writeBytes(end);
        	FileInputStream fStream =new FileInputStream(picPath);
        	Log.e(TAG, "picPath:"+picPath);
        	int bufferSize =1024;
        	byte[] buffer =new byte[bufferSize];
        	int length =-1;
        	while((length = fStream.read(buffer)) !=-1)
        	{
        		ds.write(buffer, 0, length);
        	}
        	ds.writeBytes(end);
        	ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        	/* close streams */
        	fStream.close();
        	ds.flush();
        	InputStream is = conn.getInputStream();
        	int ch;
        	StringBuffer b =new StringBuffer();
        	while( ( ch = is.read() ) !=-1 )
        	{
        		b.append( (char)ch );
        	}
        	ds.close();
        	return b.toString().trim();
        }
        catch(Exception e)
        {
        }
        return null;
    }

	public String uploadPic(String servlet, Pair pair, Bitmap bm)
    {
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
        try
        {
    		String url = "";
    		if(pair == null){
    			url = URL_MAIN+"/"+servlet;
    		}else{
    			url = URL_MAIN+"/"+servlet+"?"+pair.parseURL();
    		}
        	URL u =new URL(url);
        	HttpURLConnection conn=(HttpURLConnection)u.openConnection();
        	conn.setDoInput(true);
        	conn.setDoOutput(true);
        	conn.setUseCaches(false);
        	conn.setRequestMethod("POST");
        	/* setRequestProperty */
        	conn.setRequestProperty("Connection", "Keep-Alive");
        	conn.setRequestProperty("Charset", "UTF-8");
        	conn.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary="+boundary);
        	DataOutputStream ds =
        			new DataOutputStream(conn.getOutputStream());
        	ds.writeBytes(twoHyphens + boundary + end);
        	ds.writeBytes("Content-Disposition: form-data; "+
                      "name=\"file\";filename=\""+
                      "image" +"\""+ end);
        	ds.writeBytes(end);
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
            InputStream fStream = new ByteArrayInputStream(baos.toByteArray());
        	int bufferSize =1024;
        	byte[] buffer =new byte[bufferSize];
        	int length =-1;
        	while((length = fStream.read(buffer)) !=-1)
        	{
        		ds.write(buffer, 0, length);
        	}
        	ds.writeBytes(end);
        	ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        	/* close streams */
        	fStream.close();
        	ds.flush();
        	InputStream is = conn.getInputStream();
        	int ch;
        	StringBuffer b =new StringBuffer();
        	while( ( ch = is.read() ) !=-1 )
        	{
        		b.append( (char)ch );
        	}
        	ds.close();
        	return b.toString().trim();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return null;
    }
}
