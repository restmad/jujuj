package framework.net.image;

import android.widget.ImageView;

import java.io.InputStream;

public interface AbsImageProvider {

    int TAG = 1;
    void displayImage(String url, ImageView view);
    String upload(String url, Object params, InputStream in);

}
