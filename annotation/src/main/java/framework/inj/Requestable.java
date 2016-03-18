package framework.inj;

import android.content.Context;

public interface Requestable <T>{
    public void onPostResponse(Context context, T obj);
    public String onPostUrl(Context context);
    public void onError(Context context, String msg);
}
