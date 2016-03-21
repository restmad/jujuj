package framework.inj.entity.utility;

public interface Notifiable {

	void onDownloadResponse();
	void onError(String msg);
	
}
