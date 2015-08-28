package framework.net.image;

public interface Uploadable {
	
	public void onUploading(String fieldName);
	public void onUploaded(String fieldName, String response);
	
}
