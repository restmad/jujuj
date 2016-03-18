package framework.inj.entity;

import android.content.Context;

import framework.inj.Requestable;

/**
 *
 * @param <T> target result of this Postable
 *           when not specified, the target is this Postable itself
 */
public interface Postable<T> extends Requestable<T>{

    public int getSubmitButtonId();

}
