package framework.provider;

import android.content.Context;

import java.util.Map;

public abstract class AbsDataProvider{

    protected AbsDataProvider supervisor;

    public AbsDataProvider getJunior() {
        return junior;
    }

    private void setJunior(AbsDataProvider junior) {
        this.junior = junior;
    }

    protected AbsDataProvider junior;

    public void setSupervisor(AbsDataProvider supervisor){
        this.supervisor = supervisor;
        supervisor.setJunior(this);
    }

    public AbsDataProvider getSupervisor(){
        return supervisor;
    }

    public abstract void handleData(Context context, String uri, Map<String, String> params, Class cls,
                                     Listener.Response response, Listener.Error error);

    public abstract void handleResult(Context context, Object result);

}
