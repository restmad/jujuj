package framework.inj.entity;

import android.content.Context;

/**
 *
 * @param <T> target result of this Postable
 *           when not specified, the target is this Postable itself
 */
public interface Postable<T>{

    public int getSubmitButtonId();
    public void onPostResponse(Context context, T obj);
    public String onPostUrl(Context context);
    public void onError(Context context, String msg);

}
