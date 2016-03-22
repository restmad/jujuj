package provider;

import framework.core.Configurations;
import provider.database.DBProvider;
import provider.volley.VolleyProvider;

public class ConfigBuilder {

    public static Configurations getDefault(){
        return new Configurations.Builder()
                .setImageProvider(new ImageProvider())
                .addDataProvider(new DBProvider())
                .addDataProvider(new VolleyProvider())
                .build();
    }

}
