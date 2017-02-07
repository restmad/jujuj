package com.example.tagimageview.download;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class AsyncLoader {
	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟截碉拷图片URL锟斤拷锟较ｏ拷锟斤拷锟斤拷锟截革拷锟斤拷锟斤拷锟斤拷
	private static HashSet<String> sDownloadingSet;
	//锟斤拷锟斤拷锟斤拷锟节存缓锟斤拷
	private static Map<String,SoftReference<Bitmap>> sImageCache; 
	//图片锟斤拷锟街伙拷取锟斤拷式锟斤拷锟斤拷锟竭ｏ拷锟斤拷锟斤拷URL锟斤拷取锟斤拷锟节存缓锟斤拷锟饺★拷锟斤拷獠匡拷募锟斤拷锟斤拷锟斤拷取
	private static ImageLoaderImpl impl;
	//锟竭程筹拷锟斤拷锟�
	private static ExecutorService sExecutorService;
	
	//通知UI锟竭筹拷图片锟斤拷取ok时使锟斤拷
	private Handler handler; 
	
	private Context context;
	
	
	/**
	 * 锟届步锟斤拷锟斤拷图片锟斤拷系幕氐锟斤拷涌锟�
	 */
	public interface ImageCallback{
		/**
		 * 锟截碉拷锟斤拷锟斤拷
		 * @param bitmap: may be null!
		 * @param imageUrl 
		 */
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
	/**锟斤拷锟斤拷锟竭程筹拷*/
	public static void startThreadPoolIfNecessary(){
		if(sExecutorService == null || sExecutorService.isShutdown() || sExecutorService.isTerminated()){
			sExecutorService = Executors.newFixedThreadPool(3);
			//sExecutorService = Executors.newSingleThreadExecutor();
		}
	}
	
	/**
	 * 锟届步锟斤拷锟斤拷图片锟斤拷锟斤拷锟斤拷锟芥到memory锟斤拷
	 * @param url	
	 * @param callback	see ImageCallback interface
	 */
	public void downloadImage(final String url, final ImageCallback callback){
		downloadImage(url, true, callback);
	}
	
	/**
	 * 
	 * @param url
	 * @param cache2Memory 锟角否缓达拷锟斤拷memory锟斤拷
	 * @param callback
	 */
	public void downloadImage(final String url, final boolean cache2Memory, final ImageCallback callback){
		if(sDownloadingSet.contains(url)){
			return;
		}
		
		Bitmap bitmap = impl.getBitmapFromMemory(url);
		if(bitmap != null){
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
						bitmap = impl.getBitmapFromUrl(url, cache2Memory);
					}else{
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
	
	/**
	 * 预锟斤拷锟斤拷锟斤拷一锟斤拷图片锟斤拷锟斤拷锟斤拷锟斤拷memory锟斤拷
	 * @param url 
	 */
	public void preLoadNextImage(final String url){
		//锟斤拷callback锟斤拷为锟秸ｏ拷只锟斤拷bitmap锟斤拷锟芥到memory锟斤拷锟缴★拷
		downloadImage(url, null);
	}

}

