package framework.inj.entity;

import android.content.Context;


public interface Downloadable {
	
	public String onDownLoadUrl(Context context);
	public abstract void onDownLoadResponse(Context context);
	public Object onDownloadParams();
	
}
