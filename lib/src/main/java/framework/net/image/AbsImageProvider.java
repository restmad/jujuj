package framework.net.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by shinado on 15/8/31.
 */
public interface AbsImageProvider {

    public static final int TAG = 1;
    public void displayImage(String url, ImageView view);
    public String upload(String url, Object params, InputStream in);

}
