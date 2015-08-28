package framework.net.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Log;
import framework.net.impl.Pair;

public class ImageUploader {

	public static String upload(String url, String imageUrl){
		
		try {
			InputStream is = new FileInputStream(imageUrl);
			return ImageUploaderImpl.uploadImage(url, is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String upload(String url, Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return ImageUploaderImpl.uploadImage(url, is);
	}
	

}
