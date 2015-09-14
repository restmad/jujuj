package framework.inj.entity;

import framework.inj.entity.utility.Notifiable;

/**
 *
 * a Loadable is a mutable entity which separates downloading operation with the target entity.
 * a Loadable MUST define how the values of widgets in a layout should be set.
 * In this case, this target entity would have more than one way to download.
 *
 */
public abstract class Loadable<T> extends MutableEntity<T> implements Downloadable{
    public Loadable() {
    }

    public Loadable(T t) {
        super(t);
    }

    public Loadable(T t, Notifiable notifiable) {
        super(t, notifiable);
    }
}
