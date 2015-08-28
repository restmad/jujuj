package sample;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uni.netframe.Configuration;
import com.uni.netframe.Netframe;
import com.uni.netframe.NetframeApplication;

public class MyApplication extends NetframeApplication{

	public static final String URL = "http://tagme.sinaapp.com/";

	@Override
	public void onCreate() {
		super.onCreate();
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		Netframe.getInstance().init(Configuration.getDefault());
	}
}
