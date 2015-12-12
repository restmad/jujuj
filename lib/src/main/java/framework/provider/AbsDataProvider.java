package framework.provider;

import android.content.Context;

import java.util.Map;

public abstract class AbsDataProvider{

    protected AbsDataProvider supervisor;

    public void setSupervisor(AbsDataProvider supervisor){
        this.supervisor = supervisor;
    }

    public AbsDataProvider getSupervisor(){
        return supervisor;
    }

    public abstract void handleData(Context context, String uri, Map<String, String> params, Class cls,
                                     Listener.Response response, Listener.Error error);

}
