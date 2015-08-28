package framework.inj.entity;

import android.content.Context;


public interface Downloadable {
	
	public String onDownLoadUrl(Context context);
	public void onDownLoadResponse(Context context);
	public Object onDownloadParams();
    public void onError(Context context, String msg);
	
}
