package demo4;

import org.json.JSONException;
import org.json.JSONObject;

import framework.net.abs.AbsDataProvider;
import framework.net.abs.Listener;

/**
 * Created by shinado on 15/8/28.
 */
public class LocalProvider implements AbsDataProvider {

    @Override
    public void requestJson(String url, Object params, String charset, Listener.Response response, Listener.Error error) {
        String json = "{\"userPortrait\":\"http:\\/\\/img3.douban.com\\/icon\\/ul50757825-11.jpg\",\"userName\":\"Dan\",\"email\":\"fckgfw@china.com\",\"married\":\"false\",\"numbers\":[{\"number\":\"13555855443\"},{\"number\":\"15366783412\"}]}";
        JSONObject obj = new JSONObject();
        try {
            response.onResponse(obj.getJSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestArray(String url, Object params, String charset, Listener.Response response, Listener.Error error) {

    }
}
