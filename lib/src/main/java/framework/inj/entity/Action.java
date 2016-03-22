package framework.inj.entity;

import android.content.Context;
import android.view.View;

import framework.inj.Requestable;

public interface Action<T> extends Requestable<T> {

    Object onDownloadParams();
    Object getValue();

}
