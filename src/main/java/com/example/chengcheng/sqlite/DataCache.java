package com.example.chengcheng.sqlite;

import android.util.LruCache;

import java.security.InvalidParameterException;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日13:10:27
 * Description:
 */
public class DataCache extends android.support.v4.util.LruCache<String, DataBase> {

    private static DataCache mInstance = new DataCache();

    public static DataCache getInstance() {
        return mInstance;
    }

    private DataCache() {
        super(1000);
    }

    public <T extends DataBase> DataBase get(Class<T> cls, String id) {
        return get(cls, id, true);
    }

    public <T extends DataBase> DataBase get(Class<T> cls, String id, boolean createNotFound) {
        DataBase data = get(cls.getName() + id);

        if (data == null && createNotFound) {
            data = DataBase.deserializeFromDb(cls, data.getmPrimaryKey(), data.getmPrimaryKeyValue());
            if (data != null) {
                put(data);
            }
        }
        return data;
    }

    public <T extends DataBase> void put(DataBase data) {
        if (data == null)
            throw new InvalidParameterException("Can't cache null data");

        put(data.getClass().getName() + data.getmPrimaryKeyValue(), data);
    }

    public <T extends DataBase> DataBase remove(DataBase data) {
        if (data == null)
            return null;

        return remove(data.getClass().getName() + data.getmPrimaryKeyValue());
    }

    public <T extends DataBase> DataBase remove(String key, long id) {
        if (key == null || id <= 0)
            return null;

        return remove(key + id);
    }
}