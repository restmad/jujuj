package provider.database;

import java.util.HashMap;
import java.util.Map;

public interface AbsDBHandler {
    Object query(Map<String, String> params, String uri);
}
