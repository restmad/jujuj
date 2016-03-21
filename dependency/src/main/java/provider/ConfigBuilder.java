package provider;

import framework.core.Configurations;
import provider.volley.VolleyProvider;

/**
 * Created by shinado on 15/8/31.
 */
public class ConfigBuilder {

    public static Configurations getDefault(){
        return new Configurations.Builder()
                .setImageProvider(new ImageProvider())
                .addDataProvider(new DBProvider())
                .addDataProvider(new VolleyProvider())
                .build();
    }

}
