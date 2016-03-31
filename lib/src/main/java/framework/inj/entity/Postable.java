package framework.inj.entity;

/**
 *
 * @param <T> target result of this Postable
 *           when not specified, the target is this Postable itself
 */
public interface Postable<T> extends Requestable<T>{

    int getSubmitButtonId();

}
