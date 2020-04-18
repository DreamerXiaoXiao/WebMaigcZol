package us.codecraft.webmagic;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static String getString(Map map, String key) {
        if (map.get(key) == null)
            return null;
        if ((map.get(key) + "").trim().length() == 0) {
            return null;
        }
        if ((map.get(key) + "").trim().equals("null")) {
            return null;
        }
            return (map.get(key) + "").trim();
        }

    @org.junit.Test
    public void test01(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("size", null);
        String key = map.get("size");
        System.out.println(key);
    }
}
