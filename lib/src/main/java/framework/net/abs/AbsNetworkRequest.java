package framework.net.abs;

/**
 * Created by shinado on 15/8/27.
 */
public interface AbsNetworkRequest{

    public void requestJson(String url, Object params, String charset,
                            Listener.Response response, Listener.Error error);
    public void requestArray(String url, Object params, String charset,
                             Listener.Response response, Listener.Error error);

}
