package com.nebulas.io.net.retrofit;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class LoaderIdManager {

    private static HashMap<String, Integer> idClassMap = new HashMap<>();
    private static AtomicInteger seq = new AtomicInteger(0);

    private LoaderIdManager() {
    }

    /**
     * 重新获取一个新的loaderId
     * @param tmpClass
     * @return
     */
    public static int getNewLoaderId(Class tmpClass) {
        idClassMap.remove(tmpClass.toString());
        return getLoaderId(tmpClass);
    }

    /**
     * 获取一个loaderId，优先使用缓存中的id
     * @param tmpClass
     * @return
     */
    public static int getLoaderId(Class tmpClass) {
        String className = tmpClass.toString();
        if (idClassMap.get(className) == null || idClassMap.get(className) == 0) {
            int id = seq.incrementAndGet();
            idClassMap.put(className, id);
            return id;
        } else {
            return idClassMap.get(className);
        }
    }

    public static int getNewLoaderId() {
        return seq.incrementAndGet();
    }

}
