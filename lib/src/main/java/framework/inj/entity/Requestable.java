package framework.inj.entity;

import android.content.Context;

public interface Requestable<T>{
    void onPostResponse(Context context, T obj);
    String onPostUrl(Context context);
    void onError(Context context, String msg);
}
