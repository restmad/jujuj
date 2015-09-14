package framework.inj.groupview;

import java.util.Collection;

public interface Listable<T>{

    public T getItem(int position);
    public int getCount();

}
