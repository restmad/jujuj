package provider;

import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import framework.net.image.AbsImageProvider;

public class ImageProvider implements AbsImageProvider{

    private final static String TAG = "ImageProvider";

    @Override
    public void displayImage(String url, ImageView view) {
        ImageLoader.getInstance().displayImage(url, view);
    }

    @Override
    public String upload(String url, Object params, InputStream in) {
        Log.v(TAG, "image url:" + url);
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
			/* setRequestProperty */
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"file\";filename=\"" + "image" + "\"" + end);
            ds.writeBytes(end);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
            in.close();
            ds.flush();
            InputStream is = conn.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            Log.v(TAG, "image upload succeed:" + b.toString().trim());
            ds.close();
            return b.toString().trim();
        } catch (Exception e) {
            Log.v(TAG, "image upload failed:" + e.getMessage());
        }
        return null;
    }

}
