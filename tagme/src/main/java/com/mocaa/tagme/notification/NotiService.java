package com.mocaa.tagme.notification;

import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.MainActivity;
import com.mocaa.tagme.entity.Notification;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.GetNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class NotiService extends Service{
	
	private boolean isRunning = true;
	private final int SLEEPING_TIME = 1000 * 60 * 2;
	
	@Override
	public void onCreate(){

		new Thread(){
			@Override
			public void run(){
				GetNotifications get = new GetNotifications();
				Connection conn = new Connection();
				
				
				while(isRunning){
					TreeSet<Notification> list = (TreeSet<Notification>)
							get.getMsg(NotiService.this, conn, null, null);
					if(list != null && list.size() > 0){
						Intent data = new Intent(GlobalDefs.ACTION_NOTIFICATIONS);
						data.putExtra(GlobalDefs.ACTION_NOTIFICATIONS, list);
						sendBroadcast(data);
						playNotification(list);
					}
					try {
						sleep(SLEEPING_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void testNoti(){
		new Thread(){
			@Override
			public void run(){
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TreeSet<Notification> list = new TreeSet<Notification>();
				Notification noti = new Notification(1000, "s@c.m", "Shinado", 
						"2014-11-03 10:18:53", 680, 2);
				list.add(noti);
				Intent data = new Intent(GlobalDefs.ACTION_NOTIFICATIONS);
				data.putExtra(GlobalDefs.ACTION_NOTIFICATIONS, list);
				sendBroadcast(data);
				playNotification(list);
			}
		}.start();
	}
	
	private void playNotification(TreeSet<Notification> list){
		String content = "";
		String title = getResources().getString(R.string.app_name);
		NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		android.app.Notification notification = new android.app.Notification(
				R.drawable.ic_noti_bar, getResources().getString(R.string.app_name),
				System.currentTimeMillis());
		notification.defaults |= android.app.Notification.DEFAULT_ALL;
		notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		int size = list.size();
		if(size == 1){
			Notification vo = list.first();
			content = vo.getContent(this);
		}else{
			content = getResources().getString(R.string.you_got) + " "+size+" " +
					getResources().getString(R.string.notifications);
		}
		Intent intent = new Intent();
		intent.putExtra(GlobalDefs.EXTRA_NOTI, true);
		intent.setClass(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(this, title, content, contentIntent);
		mNotification.notify(1, notification);
	}
	
	@Override
	public void onDestroy(){
		isRunning = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
