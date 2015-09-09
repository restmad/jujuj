package com.mocaa.tagme.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mocaa.tagme.R;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.util.FileUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

public class BackgroundDownloadService extends Service{

	private NotificationManager mNotificationManager;

	private Executor executor;

	private final int maxTask = 3;
	
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Task task = (Task)msg.obj;
			switch (msg.what) {
			case FusionCode.download:  
				RemoteViews contentView = task.getNotification().contentView;
				contentView.setTextViewText(R.id.rate, task.getProgress() + "%"); 
				contentView.setProgressBar(R.id.progress, amountProgress, task.getProgress(), false);
				mNotificationManager.notify(task.getId(), task.getNotification());  
				break;
			case FusionCode.downloaded:
				task.getNotification().flags = Notification.FLAG_AUTO_CANCEL;  
				task.getNotification().contentView = null;
				task.getNotification().setLatestEventInfo(
						getApplicationContext(), 
						getResources().getString(R.string.download_done),
						task.getName()+getResources().getString(R.string.download_finish), null);
				mNotificationManager.notify(task.getId(), task.getNotification());
				installApk();
				break;
			case FusionCode.cancelDownload:
				mNotificationManager.cancel(task.getId());
				break;
			default:
				break;
			}
		}
		
	};
	
	private void installApk() {  
		File file = new File(GlobalDefs.DOWNLOAD_PATH);
	    Intent intent = new Intent();  
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
	    startActivity(intent);  
	}  
	
	private float FILE_SIZE;
	private float downloadFileSize;
	private int amountProgress;

	public void startTask(){
		if(null==executor){
			executor = Executors.newFixedThreadPool(maxTask);
		}
		final Task task = new Task();
		task.setName(getResources().getString(R.string.app_name));
		setUpNotification(task);
		new Thread(){
			@Override
			public void run(){
				try {  
					String ss = Connection.URL_DOWNLOAD;
					System.out.println("connect:"+ss);
					URL myURL = new URL(ss);
					URLConnection conn = myURL.openConnection();
					conn.setReadTimeout(10000);
					conn.connect();
					InputStream is = conn.getInputStream();
					FILE_SIZE = conn.getContentLength();
					if (FILE_SIZE <= 0) throw new RuntimeException("unknown file size");
					if (is == null) throw new RuntimeException("stream is null");
					FileUtil.checkRoot();
					FileOutputStream fos = new FileOutputStream(GlobalDefs.DOWNLOAD_PATH);

					byte buf[] = new byte[1024];
					long startTime = System.currentTimeMillis();
					downloadFileSize = 0;
					while(true){

						int numread = is.read(buf);
						System.out.println("read:"+numread);
						if (numread == -1){
							break;
						}
						fos.write(buf, 0, numread);
						downloadFileSize += numread;
						if(System.currentTimeMillis() - startTime >= 1000){
							startTime = System.currentTimeMillis();
							task.setProgress((int) ((downloadFileSize/FILE_SIZE)*100));
							task.setStatus(FusionCode.download);
							System.out.println("in:"+task.getStatus());
							handler.sendMessage(handler.obtainMessage(task.getStatus(),task));
						}
					}
					task.setStatus(FusionCode.downloaded);
					System.out.println("out:"+task.getStatus());
					handler.sendMessage(handler.obtainMessage(task.getStatus(),task));
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
					BackgroundDownloadService.this.stopSelf();
				} 
			}
		}.start();
	}

	public void setUpNotification(Task task){
		int icon = R.drawable.ic_noti_bar;
		CharSequence tickerText = task.getName()+
				getResources().getString(R.string.download_start);
		long when = System.currentTimeMillis();
		Notification mNotification = new Notification(icon, tickerText, when); 
		task.setNotification(mNotification);

		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		RemoteViews contentView  = new RemoteViews(getApplicationContext().getPackageName(),
				R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.file_name, task.getName());

		mNotification.contentView = contentView;

		mNotificationManager.notify(task.getId(), mNotification);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		startTask();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
}
