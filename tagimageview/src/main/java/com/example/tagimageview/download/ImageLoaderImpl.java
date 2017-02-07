package com.example.tagimageview.download;

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


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author Administrator
 * @desc 锟届步锟斤拷锟斤拷图片锟斤拷锟斤拷锟斤拷
 *
 */
public class ImageLoaderImpl {
	//锟节达拷锟叫碉拷锟斤拷应锟矫伙拷锟斤拷
	private Map<String, SoftReference<Bitmap>> imageCache;
	
	//锟角否缓达拷图片锟斤拷锟斤拷锟斤拷锟侥硷拷
	private boolean cache2FileFlag = true;
	
	//锟斤拷锟斤拷目录,默锟斤拷锟斤拷/data/data/package/cache/目录
	private String cachedDir;
	
	public ImageLoaderImpl(Map<String, SoftReference<Bitmap>> imageCache, Context context){
		this.imageCache = imageCache;
		cachedDir = context.getCacheDir().getAbsolutePath();
	}
	
	/**
	 * 锟角否缓达拷图片锟斤拷锟解部锟侥硷拷
	 * @param flag 
	 */
	public void setCache2File(boolean flag){
		cache2FileFlag = flag;
	}
	
	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟酵计�
	 * @param url 锟斤拷锟斤拷图片锟斤拷URL锟斤拷址
	 * @param cache2Memory 锟角否缓达拷(锟斤拷锟斤拷锟斤拷锟节达拷锟斤拷)
	 * @return bitmap 图片bitmap锟结构
	 * 
	 */
	public Bitmap getBitmapFromUrl(String url, boolean cache2Memory){
		Bitmap bitmap = null;
		try{
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();  
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			
			if(cache2Memory){
				//1.锟斤拷锟斤拷bitmap锟斤拷锟节达拷锟斤拷锟斤拷锟斤拷锟斤拷
				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
				if(cache2FileFlag){
					//2.锟斤拷锟斤拷bitmap锟斤拷/data/data/packageName/cache/锟侥硷拷锟斤拷锟斤拷
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
			
			//2.锟斤拷锟斤拷bitmap锟斤拷/data/data/packageName/cache/锟侥硷拷锟斤拷锟斤拷
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
	
	/**
	 * 锟斤拷锟节存缓锟斤拷锟叫伙拷取bitmap
	 * @param url
	 * @return bitmap or null.
	 */
	public Bitmap getBitmapFromMemory(String url){
		Bitmap bitmap = null;
		if(imageCache.containsKey(url)){
			synchronized(imageCache){
				SoftReference<Bitmap> bitmapRef = imageCache.get(url);
				if(bitmapRef != null){
					bitmap = bitmapRef.get();
					return bitmap;
				}
			}
		}
//		if(cache2FileFlag){
//			bitmap = getBitmapFromFile(url);
//			if(bitmap != null){
//				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
//			}
//		}
		
		return bitmap;
	}
	
	/**
	 * 锟斤拷锟解部锟侥硷拷锟斤拷锟斤拷锟叫伙拷取bitmap
	 * @param url
	 * @return
	 */
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
		} catch (FileNotFoundException e) {
			bitmap = null;
		}
		return bitmap;
	}
	
	
	/**  
     * MD5 锟斤拷锟斤拷  
     */   
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
			// TODO Auto-generated catch block
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
	/**  
     * MD5 锟斤拷锟斤拷  
    private static String getMD5Str(Object...objects){
    	StringBuilder stringBuilder=new StringBuilder();
    	for (Object object : objects) {
			stringBuilder.append(object.toString());
		}
    	return getMD5Str(stringBuilder.toString());
    }*/ 
}
