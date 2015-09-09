package framework.inj.entity;

import android.content.Context;

/**
 * a downloadable entity defines how a entity should be downloaded
 * notice that this entity that Downloadable directs to download could be either Downloadable itself or some one else
 * Downloadable only admits to provider the necessary info and status, not the entity itself
 * Downloadable does NOT have to define widgets logic
 */
public interface Downloadable {
	
	public String onDownLoadUrl(Context context);
	public void onDownLoadResponse(Context context);
	public Object onDownloadParams();
    public void onError(Context context, String msg);
	
}
