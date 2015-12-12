package framework.inj.entity;

import android.content.Context;

public interface Postable<T>{

    public int getSubmitButtonId();
    public void onPostResponse(Context context, T obj);
    public String onPostUrl(Context context);
    public void onError(Context context, String msg);

}
