package framework.net.image;

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
	//�����������ص�ͼƬURL���ϣ������ظ�������
	private static HashSet<String> sDownloadingSet;
	//�������ڴ滺��
	private static Map<String,SoftReference<Bitmap>> sImageCache; 
	//ͼƬ���ֻ�ȡ��ʽ�����ߣ�����URL��ȡ���ڴ滺���ȡ���ⲿ�ļ������ȡ
	private static ImageLoaderImpl impl;
	//�̳߳����?
	private static ExecutorService sExecutorService;
	
	//֪ͨUI�߳�ͼƬ��ȡokʱʹ��
	private Handler handler; 
	
	private Context context;
	
	
	/**
	 * �첽����ͼƬ��ϵĻص��ӿ�?
	 */
	public interface ImageCallback{
		/**
		 * �ص�����
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
	/**�����̳߳�*/
	public static void startThreadPoolIfNecessary(){
		if(sExecutorService == null || sExecutorService.isShutdown() || sExecutorService.isTerminated()){
			sExecutorService = Executors.newFixedThreadPool(3);
			//sExecutorService = Executors.newSingleThreadExecutor();
		}
	}
	
	/**
	 * �첽����ͼƬ�������浽memory��
	 * @param url	
	 * @param callback	see ImageCallback interface
	 */
	public void downloadImage(final String url, final ImageCallback callback){
		downloadImage(url, true, callback);
	}
	
	/**
	 * 
	 * @param url
	 * @param cache2Memory �Ƿ񻺴���memory��
	 * @param callback
	 */
	public void downloadImage(final String url, final boolean cache2Memory, final ImageCallback callback){
		if(sDownloadingSet.contains(url)){
			Log.i("AsyncImageLoader", "###��ͼƬ�������أ������ظ����أ�");
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
			Log.i("AsyncImageLoader", "###��ͼƬ�������أ������ظ����أ�");
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
	 * Ԥ������һ��ͼƬ��������memory��
	 * @param url 
	 */
	public void preLoadNextImage(final String url){
		//��callback��Ϊ�գ�ֻ��bitmap���浽memory���ɡ�
		downloadImage(url, null);
	}
	
	/**
	 * 
	 * @param url       upload URL, relative. e.g upload_img.php
	 * @param bm        Bitmap to be uploaded
	 * @param callback  the result of which is the URL of the image
	 */
	public void uploadImage(final String url, final Bitmap bm, final NetworkCallback callback){
		sExecutorService.submit(new Runnable(){
			@Override
			public void run() {
				final String result = ImageUploader.uploadImage(url, null, bm);
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
		/**
		 * �ص�����
		 * @param bitmap: may be null!
		 * @param imageUrl 
		 */
		public void onLoaded(Object obj);
	}
	
}

