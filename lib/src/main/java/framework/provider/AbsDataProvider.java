package framework.provider;

import java.util.Map;

public abstract class AbsDataProvider{

    protected AbsDataProvider supervisor;

    public void setSupervisor(AbsDataProvider supervisor){
        this.supervisor = supervisor;
    }

    public AbsDataProvider getSupervisor(){
        return supervisor;
    }

    public abstract void handleData(String uri, Map<String, String> params, Class cls,
                                     Listener.Response response, Listener.Error error);


}
