package framework.net.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Log;

import com.uni.netframe.GlobalDefs;

import framework.net.impl.Pair;

public class ImageUploader {

	private final static String TAG = "ImageUploader";
	
	public String uploadImage(String uUrl, Pair pair, String imagePath)
    {
		System.out.println("image path:"+imagePath);
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
        try
        {
    		String url = "";
    		if(pair == null){
    			url = GlobalDefs.NetWork.URL+"/"+uUrl;
    		}else{
    			url = GlobalDefs.NetWork.URL+"/"+uUrl+"?"+pair.parseURL();
    		}
        	Log.d(TAG, "image url"+url);
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
        	FileInputStream fStream = new FileInputStream(imagePath);
        	Log.e(TAG, "picPath:"+imagePath);
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
        	Log.d(TAG, "image upload succeed:"+b.toString().trim());
        	ds.close();
        	return b.toString().trim();
        }
        catch(Exception e)
        {
        	Log.d(TAG, "image upload failed:"+e.getMessage());
        }
        return null;
    }
	
	public static String uploadImage(String uUrl, Pair pair, Bitmap bm)
    {
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
        try
        {
    		String url = "";
    		if(pair == null){
    			url = GlobalDefs.NetWork.URL+"/"+uUrl;
    		}else{
    			url = GlobalDefs.NetWork.URL+"/"+uUrl+"?"+pair.parseURL();
    		}
        	Log.d(TAG, "image url"+url);
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
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
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
        	fStream.close();
        	ds.flush();
        	InputStream is = conn.getInputStream();
        	int ch;
        	StringBuffer b =new StringBuffer();
        	while( ( ch = is.read() ) !=-1 )
        	{
        		b.append( (char)ch );
        	}
        	Log.d(TAG, "image upload succeed:"+b.toString().trim());
        	ds.close();
        	return b.toString().trim();
        }
        catch(Exception e)
        {
        	Log.d(TAG, "image upload failed:"+e.getMessage());
        }
        return null;
    }
}
