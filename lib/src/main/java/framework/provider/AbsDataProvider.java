package framework.provider;

import java.util.Map;

/**
 * Created by shinado on 15/8/27.
 */
public abstract class AbsDataProvider{

    protected AbsDataProvider supervisor;

    public void setSupervisor(AbsDataProvider supervisor){
        this.supervisor = supervisor;
    }

    public AbsDataProvider getSupervisor(){
        return supervisor;
    }

    public abstract void handleData(String uri, Map<String, String> params, Object target,
                                     Listener.Response response, Listener.Error error);


}
