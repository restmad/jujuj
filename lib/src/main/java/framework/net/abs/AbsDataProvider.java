package framework.net.abs;

/**
 * Created by shinado on 15/8/27.
 */
public abstract class AbsDataProvider implements Comparable<AbsDataProvider>{

    public abstract void requestJson(String url, Object params, String charset,
                            Listener.Response response, Listener.Error error);
    public abstract void requestArray(String url, Object params, String charset,
                             Listener.Response response, Listener.Error error);

    public abstract int index();

    @Override
    public int compareTo(AbsDataProvider another) {
        return index() - another.index();
    }
}
