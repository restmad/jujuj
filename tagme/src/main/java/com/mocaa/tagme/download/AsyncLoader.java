package com.mocaa.tagme.download;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.Transport;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class AsyncLoader {

	private static HashSet<String> sDownloadingSet;

	private static Map<String,SoftReference<Bitmap>> sImageCache; 

	private static ImageLoaderImpl impl;

	private static ExecutorService sExecutorService;
	

	private Handler handler; 
	
	private Context context;
	
	

	public interface ImageCallback{

		public void onImageLoaded(Bitmap bitmap, String imageUrl);
	}
	
	static{
		sDownloadingSet = new HashSet<String>();
		sImageCache = new HashMap<String,SoftReference<Bitmap>>();
	}

	public AsyncLoader(Context context){
		this.context = context;
		impl = new ImageLoaderImpl(sImageCache, context);
		handler = new Handler();
		startThreadPoolIfNecessary();
		
	}

	public static void startThreadPoolIfNecessary(){
		if(sExecutorService == null || sExecutorService.isShutdown() || sExecutorService.isTerminated()){
			sExecutorService = Executors.newFixedThreadPool(3);

		}
	}
	

	public void downloadImage(final String url, final ImageCallback callback){
		downloadImage(url, true, callback);
	}
	

	public void downloadImage(final String url, final boolean cache2Memory, final ImageCallback callback){
		if(sDownloadingSet.contains(url)){
			Log.i("AsyncImageLoader", "###锟斤拷图片锟斤拷锟斤拷锟斤拷锟截ｏ拷锟斤拷锟斤拷锟截革拷锟斤拷锟截ｏ拷");
			return;
		}
		
		Bitmap bitmap = impl.getBitmapFromMemory(url);
		if(bitmap != null){
			GlobalDefs.o("set view from cache");
			if(callback != null){
				callback.onImageLoaded(bitmap, url);
			}
		}else{
			sDownloadingSet.add(url);
			sExecutorService.submit(new Runnable(){
				@Override
				public void run() {
					final Bitmap bitmap;
					Bitmap bmp = impl.getBitmapFromFile(url);
					if(bmp == null){
						GlobalDefs.o("get bitmap from internet");
						bitmap = impl.getBitmapFromUrl(url, cache2Memory);
					}else{
						GlobalDefs.o("get bitmap from file");
						bitmap = bmp;
					}
					handler.post(new Runnable(){
						@Override
						public void run(){
							if(callback != null)
								callback.onImageLoaded(bitmap, url);
							sDownloadingSet.remove(url);
						}
					});
				}
			});
		}
	}
	
	public Bitmap downloadImage(final String url, final boolean cache2Memory){
		if(sDownloadingSet.contains(url)){
			Log.i("AsyncImageLoader", "###锟斤拷图片锟斤拷锟斤拷锟斤拷锟截ｏ拷锟斤拷锟斤拷锟截革拷锟斤拷锟截ｏ拷");
			return null;
		}
		
		Bitmap bitmap = impl.getBitmapFromMemory(url);
		if(bitmap != null){
			System.out.println("from memo");
		}else{
			System.out.println("from net");
			bitmap = impl.getBitmapFromUrl(url, cache2Memory);
		}
		return bitmap;
	}
	

	public void preLoadNextImage(final String url){

		downloadImage(url, null);
	}
	
	public void uploadImage(final String url, final Bitmap bm, final NetworkCallback callback){
		sExecutorService.submit(new Runnable(){
			@Override
			public void run() {
				Connection conn = new Connection();
				final String result = conn.uploadPic(url, null, bm);
				handler.post(new Runnable(){
					@Override
					public void run(){
						if(callback != null)
							callback.onLoaded(result);
					}
				});
			}
		});
	}

	public void downloadMessage(final Transport tp, final Object obj, final String[] array, 
			final NetworkCallback callback){
		
		sExecutorService.submit(new Runnable(){
			@Override
			public void run() {
				final Object result = tp.getMsg(context, new Connection(), obj, array);
				handler.post(new Runnable(){
					@Override
					public void run(){
						if(callback != null)
							callback.onLoaded(result);
					}
				});
			}
		});
	}

	public interface NetworkCallback{

		public void onLoaded(Object obj);
	}
	
}

