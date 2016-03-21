package framework.inj.entity;

import android.content.Context;

public interface Director {
    String onDownLoadUrl(Context context);
    Object onDownloadParams();
}
