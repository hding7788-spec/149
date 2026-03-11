package ext.casc.part.cache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectCache {
    public  static Map<String, JSONObject> wzCache = new HashMap<String, JSONObject>();
    public  static Map<String, JSONArray> gyCsTemplateCache = new HashMap<String, JSONArray>();
    public  static Map<String, JSONObject> queryMiddleTableCache = new HashMap<String, JSONObject>();

}
