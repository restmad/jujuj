package framework.inj.entity;

import android.content.Context;

public interface Responsible {

    void onError(Context context, String msg);
    void onDownLoadResponse(Context context);

}
