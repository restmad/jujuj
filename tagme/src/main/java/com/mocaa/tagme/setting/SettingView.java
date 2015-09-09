package com.mocaa.tagme.setting;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.Toast;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.AboutDialog;
import com.mocaa.tagme.dialog.NormalDialog;
import com.mocaa.tagme.dialog.QRCodeDialog;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.download.BackgroundDownloadService;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.transport.GetLatestVersion;
import com.mocaa.tagme.view.MainView;

public class SettingView extends MainView{

	public SettingView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		rootView = LayoutInflater.from(context).inflate(
				R.layout.layout_setting, null);
		pref = new UserPref(context);
		create();
		checkForUpdate();
	}

	private View updateIcon;
	private View rootView;
	private UserPref pref;
	private boolean needToUpdate = false;
	private String versionDetail = "";
	
	private void checkForUpdate(){
		final String cv = getCurrentVersion();
		System.out.println("current version"+cv);
		String sv = pref.getServerVersion(cv);
		System.out.println("server version"+sv);
		needToUpdate = sv.compareTo(cv) > 0;
		if(!needToUpdate){
			new AsyncLoader(context).downloadMessage(new GetLatestVersion(),
					null, null, new NetworkCallback(){
     
						@Override
						public void onLoaded(Object obj) {
							String sv = (String) obj;
							if(sv == null){
								return;
							}
							String[] detail = sv.split("-");
							if(detail!= null && detail.length >= 2){
								String newVersion = detail[0];
								System.out.println(newVersion+""+newVersion);
								needToUpdate = newVersion.compareTo(cv) > 0;
								versionDetail = context.getResources().getString(R.string.latest_version) + 
										newVersion + detail[1].replace('*', '\n');
							}
							if(needToUpdate){
								updateIcon.setVisibility(View.VISIBLE);
							}
						}
				
			});
		}else{
			updateIcon.setVisibility(View.VISIBLE);
		}
	}
	
    private String getCurrentVersion(){
    	PackageManager packageManager = context.getPackageManager();
        String version = "";
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return version;
    }

	@Override
	public void show() {
		setTitle(context.getResources().getString(R.string.title_settings));
		root.addView(rootView);
		btn.setVisibility(View.GONE);
	}
	
	private TextSwitcher notiSwitcher;
	
	private void create(){
		
		notiSwitcher = (TextSwitcher) rootView.findViewById(R.id.setting_noti_switcher);
		if(!pref.isNotificating()){
			notiSwitcher.showNext();
		}
		updateIcon = rootView.findViewById(R.id.setting_update_ic);

		rootView.findViewById(R.id.setting_about).setOnClickListener(mClickListener);
		rootView.findViewById(R.id.setting_update).setOnClickListener(mClickListener);
		rootView.findViewById(R.id.setting_mail).setOnClickListener(mClickListener);
		rootView.findViewById(R.id.setting_support).setOnClickListener(mClickListener);
		rootView.findViewById(R.id.setting_qrcode).setOnClickListener(mClickListener);
		rootView.findViewById(R.id.setting_noti).setOnClickListener(mClickListener);
	}
	
	private OnClickListener mClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.setting_qrcode:
				qrcode();
				break;
			case R.id.setting_support:
				support();
				break;
			case R.id.setting_mail:
				mail_to();
				break;
			case R.id.setting_update:
				checkUpdate();
				break;
			case R.id.setting_about:
				about();
				break;
			case R.id.setting_noti:
				setNoti();
				break;
			}
		}
	};
	
	private void setNoti(){
		notiSwitcher.showNext();
		pref.switchNotification();
	}
	
	private void about(){
		AboutDialog.createDialog(context).show();
	}
	
	@SuppressWarnings("static-access")
	private void checkUpdate(){
		if(needToUpdate){
			NormalDialog.createDialog(context)
					.setTitleText(context.getResources().getString(R.string.download_latest))
					.setContentText(versionDetail)
					.setOnListener(new NormalDialog.BtnListener() {
						@Override
						public void onClicked(int id) {
							if(id == NormalDialog.DONE)
								download();
						}
					}).show();
		}else{
			Toast.makeText(context, R.string.latest, Toast.LENGTH_LONG).show();
		}
	}
	
	private void support(){
		NormalDialog.createDialog(context)
				.setContentText(context.getResources().getString(R.string.dialog_support))
				.show();
	}
	   
	private void qrcode(){
		QRCodeDialog.createDialog(context).show();
	}
	   
	private void mail_to(){
		Intent i = new Intent(Intent.ACTION_SEND);  
		i.setType("message/rfc822") ;
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{"shinado023@gmail.com"});  
		i.putExtra(Intent.EXTRA_SUBJECT,"mail from Tag.me user");
		i.putExtra(Intent.EXTRA_TEXT,"");  
		context.startActivity(Intent.createChooser(i,
				context.getResources().getString(R.string.select_email)));
	}

    private void download(){
    	Intent intent = new Intent(context, BackgroundDownloadService.class);
    	context.startService(intent);
    }
	
	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent intent){
		return false;
	}

	@Override
	public void hide() {
		btn.setVisibility(View.VISIBLE);
	}

	@Override
	public void startMoving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopMoving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onButtonClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
