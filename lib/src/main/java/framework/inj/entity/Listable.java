package framework.inj.entity;

import java.util.Collection;

public interface Listable<T extends Collection>{

    public T getEntity();

}
