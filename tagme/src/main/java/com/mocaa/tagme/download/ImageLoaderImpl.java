package com.mocaa.tagme.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.mocaa.tagme.global.GlobalDefs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImageLoaderImpl {

	private Map<String, SoftReference<Bitmap>> imageCache;
	

	private boolean cache2FileFlag = true;
	

	private String cachedDir;
	
	public ImageLoaderImpl(Map<String, SoftReference<Bitmap>> imageCache, Context context){
		this.imageCache = imageCache;
		cachedDir = context.getCacheDir().getAbsolutePath();
	}
	

	public void setCache2File(boolean flag){
		cache2FileFlag = flag;
	}
	

	public Bitmap getBitmapFromUrl(String url, boolean cache2Memory){
		Bitmap bitmap = null;
		try{
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();  
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			
			if(cache2Memory){

				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
				if(cache2FileFlag){

					String fileName = getMD5Str(url);
					String filePath = this.cachedDir + "/" +fileName;
					FileOutputStream fos = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				}
			}
			
			is.close();
			conn.disconnect();
			return bitmap;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Bitmap loadBitmapFromUrl(String url, Context context){
		String cachedDir = context.getCacheDir().getAbsolutePath();
		Bitmap bitmap = null;
		try{
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();  
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			

			String fileName = getMD5Str(url);
			String filePath = cachedDir + "/" +fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				
			is.close();
			conn.disconnect();
			return bitmap;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	

	public Bitmap getBitmapFromMemory(String url){
		Bitmap bitmap = null;
		if(imageCache.containsKey(url)){
			synchronized(imageCache){
				SoftReference<Bitmap> bitmapRef = imageCache.get(url);
				if(bitmapRef != null){
					bitmap = bitmapRef.get();
					GlobalDefs.o("get bitmap from cache");
					return bitmap;
				}
			}
		}






		
		return bitmap;
	}
	

	public Bitmap getBitmapFromFile(String url){
		Bitmap bitmap = null;
		String fileName = getMD5Str(url);
		if(fileName == null)
			return null;
		
		String filePath = cachedDir + "/" + fileName;
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			imageCache.put(url, new SoftReference<Bitmap>(bitmap));
			GlobalDefs.o("get bitmap from file");
		} catch (FileNotFoundException e) {
			GlobalDefs.o("get bitmap from file null");
			bitmap = null;
		}
		return bitmap;
	}
	
	

    public static String getMD5Str(String str) {   
        MessageDigest messageDigest = null;   
        try {   
            messageDigest = MessageDigest.getInstance("MD5");   
            messageDigest.reset();   
            messageDigest.update(str.getBytes("UTF-8"));   
        } catch (NoSuchAlgorithmException e) {   
            System.out.println("NoSuchAlgorithmException caught!");   
            return null;
        } catch (UnsupportedEncodingException e) {   
            e.printStackTrace();
            return null;
        }   
   
        byte[] byteArray = messageDigest.digest();   
        StringBuffer md5StrBuff = new StringBuffer();   
        for (int i = 0; i < byteArray.length; i++) {               
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)   
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));   
            else   
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));   
        }   
   
        return md5StrBuff.toString();   
    }  

    public static void saveBitmap(Context context, String url, Bitmap bitmap){
    	if(bitmap == null){
    		return;
    	}
    	String fileName = getMD5Str(url);
		String filePath = context.getCacheDir().getAbsolutePath() + "/" +fileName;
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
    }
    
    public static Bitmap getBitmapFromFile(Context context, String url){
		Bitmap bitmap = null;
		String fileName = getMD5Str(url);
		if(fileName == null)
			return null;
		
		String filePath = context.getCacheDir().getAbsolutePath() + "/" + fileName;
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			System.out.println("no image found in file");
			bitmap = null;
		}
		return bitmap;
	}

}
