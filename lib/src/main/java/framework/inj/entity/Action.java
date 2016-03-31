package framework.inj.entity;

public interface Action<T> extends Requestable<T> {

    Object onDownloadParams();
    Object getValue();

}
