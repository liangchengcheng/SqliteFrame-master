package com.example.chengcheng.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.chengcheng.annotation.MappingDbJson;
import com.example.chengcheng.annotation.SerializedDbInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日13:11:48
 * Description:
 */
public abstract class DataBase implements IDbData{
    //标识
    private static final String TAG = "DataBase";

    //获取主键
    protected abstract String getmPrimaryKey();

    //获取主键的值
    protected abstract String getmPrimaryKeyValue();

    protected void init(){

    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public boolean isItemExist() {
        return true;
    }

    @Override
    public boolean checkParams() {
        if (!onCheckParams()) {
            Log.v(TAG, "Invalide params. " + mInvalidParamDes);
            return false;
        }

        return true;
    }

    @Override
    public void save() {
        if (!checkParams()) {
            throw new RuntimeException(mInvalidParamDes);
        }

        if (!isItemExist()) {
            FrameHelper.getInstance().getDB().execSQL(genInsertSql());
        } else {

            FrameHelper.getInstance().getDB().execSQL(genReplaceSql());
            DataCache.getInstance().remove(this);
        }
    }

    protected static  <T extends DataBase> T generateDataFromCursor(final Class<T> cls,Cursor c)throws  InstantiationException, IllegalAccessException{
        T item=(T)cls.newInstance();
        Class<?> itemCls=cls;
        while (itemCls != null){
            Field []fs=itemCls.getDeclaredFields();
            for (Field f:fs){
                SerializedDbInfo a=f.getAnnotation(SerializedDbInfo.class);
                if (a!=null){
                    f.setAccessible(true);
                    String colName=a.colame();
                    String dataType=a.dataType();
                    String value=c.getString(c.getColumnIndexOrThrow(colName));
                    try {
                        f.set(item, getValue(dataType, value));
                    } catch (Exception e) {
                        Log.v(TAG, "generateDataFromCursor() method:" + e.toString());
                    }
                }
            }
            itemCls=itemCls.getSuperclass();
        }
        item.init();
        return item;
    }

    public static <T extends  DataBase>List<T> deserializeFromDb(final  Class<T> cls){
        ArrayList<T> items=new ArrayList<T>();
        if (cls==null){
            Log.e("Fatal error", "deserializeFromDb cls is null!");
            return items;
        }

        Cursor c=null;
        try{
            SQLiteDatabase db=FrameHelper.getInstance().getDB();
            StringBuilder sql = new StringBuilder("select * from ").append(cls.newInstance().getTableName());
            c = db.rawQuery(sql.toString(), null);
            if (c == null || !c.moveToFirst()) {
                return items;
            }

            do {
                T item = generateDataFromCursor(cls, c);
                items.add(item);
            } while (c.moveToNext());

            return items;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
           if (null!=c){
               c.close();
           }
        }
    }

    /**
     * 判断id是否为空
     * @param id id
     * @return 是否
     */
    public static boolean isLegalID(String id) {
        return !TextUtils.isEmpty(id);
    }

    /**
     * 根据id主键查询数据
     * @param cls 映射对象
     * @param primaryKey 主键
     * @param id 主键值
     * @param <T> 对象
     * @return 数据
     */
    public static <T extends DataBase> T deserializeFromDb(final Class<T> cls, String primaryKey, String id) {
        if (cls == null || !isLegalID(id)) {
            Log.e("Fatal error", "deserializeFromDb cls is null or id illegal!");
            return null;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = FrameHelper.getInstance().getDB();
            StringBuilder sql = new StringBuilder("select * from ").
                    append(cls.newInstance().getTableName()).
                    append(" where ").
                    append(primaryKey).append(" = ").append('\'').
                    append(safeSQLString(id)).append('\'');
            c = db.rawQuery(sql.toString(), null);
            if (c == null || !c.moveToFirst()) {
                return null;
            }

            return generateDataFromCursor(cls, c);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != c) {
                c.close();
            }
        }
    }

    /**
     * 直接执行sql语句查询数据
     * @param cls 映射对象
     * @param sql sql语句
     * @param <T> 对象
     * @return 结果集
     */
    public static <T extends DataBase> List<T> deserializeFromDb(final Class<T> cls, String sql) {
        ArrayList<T> items = new ArrayList<T>();
        if (cls == null) {
            Log.e("Fatal error", "deserializeFromDb cls is null!");
            return items;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = FrameHelper.getInstance().getDB();
            c = db.rawQuery(sql, null);
            if (c == null || !c.moveToFirst()) {
                return items;
            }

            do {
                T item = generateDataFromCursor(cls, c);
                items.add(item);
            } while (c.moveToNext());

            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != c) {
                c.close();
            }
        }
    }

    /**
     * 从cursor里面获取数据集
     * @param cls 映射对象
     * @param c cursor
     * @param <T> 对象
     * @return 数据集
     */
    public static <T extends DataBase> List<T> deserializeFromDb(final Class<T> cls, Cursor c) {
        ArrayList<T> result = new ArrayList<T>();
        if (cls == null || c == null) {
            Log.e("Fatal error", "deserializeFromDb cls is null or cursor is null!");
            return result;
        }

        if (!c.moveToFirst()) {
            return result;
        }

        try {
            do {
                T item = generateDataFromCursor(cls, c);
                result.add(item);
            } while (c.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 将json字符串转换成类
     * @param json json
     * @param cls 类
     * @param <T> 对象
     * @return 结果集
     */
    public static <T> T fromJson(String json, Class<T> cls) {
        return GsonHelper.getmInstance().getGson().fromJson(json, cls);
    }

    /**
     * 将对象转换成json
     * @param obj 对象
     * @return json
     */
    public static String toJson(Object obj) {
        return GsonHelper.getmInstance().getGson().toJson(obj);
    }
    /**
     * 进行sql语句的换换
     * @param strSrcString sql
     * @return 转换之后
     */
    public static String safeSQLString(String strSrcString) {
        if (strSrcString == null || strSrcString.length() <= 0) {
            return "";
        }

        return strSrcString.replace("'", "''");
    }
    /**
     * 获取值
     * @param dataType 类型
     * @param value 原来值
     * @return 转换之后
     */
    private static Object getValue(String dataType, String value) {
        String actValue = value;
        if (actValue == null || actValue.equalsIgnoreCase("null")) {
            if (dataType.equals("String")) {
                actValue = "";
            } else {
                actValue = "0";
            }
        }

        if (dataType.equals("String")) {
            return actValue;
        } else if (dataType.equals("long")) {
            return Long.parseLong(actValue);
        } else if (dataType.equals("int")) {
            return Integer.parseInt(actValue);
        } else if (dataType.equals("float")) {
            return Float.parseFloat(actValue);
        } else if (dataType.equals("boolean")) {
            return Boolean.parseBoolean(actValue);
        } else if (dataType.equals("double")) {
            return Double.parseDouble(actValue);
        } else if (dataType.equals("short")) {
            return Short.parseShort(actValue);
        } else if (dataType.equals("byte")) {
            return Byte.parseByte(actValue);
        } else {
            throw new RuntimeException("Unsupported type!!!!");
        }
    }

    private static boolean isInvalid(String src) {
        if (src == null || src.length() < 1) {
            return true;
        }

        return false;
    }

    protected String mInvalidParamDes;

    public String getInvalidParamDes() {
        return mInvalidParamDes;
    }

    protected abstract boolean onCheckParams();

    /**
     * 获取字段的值
     * @return hashtable
     */
    public Hashtable<String, Object> getSerializedFieldValuePair() {
        Hashtable<String, Object> result = new Hashtable<String, Object>();

        Class<?> c = getClass();
        while (c != null) {
            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) {
                SerializedDbInfo a = f.getAnnotation(SerializedDbInfo.class);
                if (a != null) {
                    f.setAccessible(true);
                    String serializeName = a.colame();
                    String colType = a.dataType();
                    Object value = null;
                    try {
                        value = f.get(this);
                    } catch (Exception e) {
                        Log.v(TAG, "getSerializedFieldValuePair() method:" + e.toString());
                        result.clear();
                        return result;
                    }

                    if (value == null) {
                        if (colType.equals("String")) {
                            value = "";
                        } else {
                            result.clear();
                            Log.v(TAG, "invalid value. Table: " + getTableName() + ", column: " + serializeName);
                            break;
                        }
                    }
                    result.put(serializeName, value);
                }
            }
            c = c.getSuperclass();
        }

        return result;
    }

    /**
     * 生成增加或者替换的语句
     * @param action insert或者replace
     * @return sql语句
     */
    public String genSql(String action) {
        Hashtable<String, Object> fieldValuePairs = getSerializedFieldValuePair();
        StringBuffer cols = new StringBuffer(action).append(" into ");
        StringBuffer values = new StringBuffer(" values(");
        cols.append(getTableName());
        cols.append('(');

        Iterator<String> iterator = fieldValuePairs.keySet().iterator();
        boolean isFirst = true;
        while (iterator.hasNext()) {
            String fieldSerName = iterator.next();
            Object value = fieldValuePairs.get(fieldSerName);
            boolean isString = false;
            if (value instanceof String) {
                isString = true;
            }

            if (isFirst) {
                isFirst = false;
                if (isString) {
                    values.append('\'');
                }
            } else {
                cols.append(',');
                values.append(',');
                if (isString) {
                    values.append('\'');
                }
            }
            cols.append(fieldSerName);
            if (isString) {
                values.append(safeSQLString((String) value));
                values.append('\'');
            } else {
                values.append(value);
            }
        }
        cols.append(')');
        values.append(')');

        return cols.append(values).toString();

    }

    /**
     * sql语句
     * @return insert
     */
    public String genInsertSql() {
        return genSql("insert");
    }

    /**
     * sql语句
     * @return replace
     */
    public String genReplaceSql() {
        return genSql("replace");
    }

    /**
     * 拼装更新的语句
     * @return sql更新
     */
    public String genUpdateSql() {
        Hashtable<String, Object> fieldValuePairs = getSerializedFieldValuePair();
        StringBuffer updateSql = new StringBuffer("update ");
        updateSql.append(getTableName());
        updateSql.append(" set ");

        boolean isFirst = true;
        Iterator<String> iterate = fieldValuePairs.keySet().iterator();
        while (iterate.hasNext()) {
            String fieldSerName = iterate.next();
            Object value = fieldValuePairs.get(fieldSerName);
            if (fieldSerName.equals(getmPrimaryKey())) {
                continue;
            }

            boolean isString = false;
            if (value instanceof String) {
                isString = true;
            }

            if (isFirst) {
                isFirst = false;
            } else {
                updateSql.append(',');
            }
            updateSql.append(fieldSerName);
            updateSql.append('=');
            if (isString) {
                updateSql.append('\'');
            }

            if (isString) {
                updateSql.append(safeSQLString((String) value));
                updateSql.append('\'');
            } else {
                updateSql.append(value);
            }
        }

        updateSql.append(" where ");
        updateSql.append(getmPrimaryKey());
        updateSql.append('=').append('\'');
        updateSql.append(safeSQLString(getmPrimaryKeyValue())).append('\'');
        return updateSql.toString();
    }


    public <T extends DataBase> String toCustomJson(final Class<T> cls,
                                                    ArrayList<MappingDbJson> maps) {
        Cursor c = null;
        try {
            String tableName = (String) cls.getDeclaredField("TABLE_NAME").get(null);
            String sql = "select * from " + tableName;
            c = FrameHelper.getInstance().getDB().rawQuery(sql, null);
            return toCustomJson(cls, c, maps);
        } catch (Exception e) {
            Log.v("DataBase", "toCustomJson failed");
            return toCustomJson(cls, c, maps);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public <T extends DataBase> String toCustomJson(final Class<T> cls,
                                                    String sql, ArrayList<MappingDbJson> maps) {
        Cursor c = null;
        try {
            c = FrameHelper.getInstance().getDB().rawQuery(sql, null);
            return toCustomJson(cls, c, maps);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public synchronized <T extends DataBase> String toCustomJson(final Class<T> cls,
                                                                 Cursor c, ArrayList<MappingDbJson> maps) {
        String jsonContent = "";
        try {
            String tableName = (String) cls.getDeclaredField("TABLE_NAME").get(null);
            JSONArray array = new JSONArray();
            jsonContent = "\"" + tableName + "\"";
            if (maps == null || maps.size() < 1
                    || c == null || !c.moveToFirst()) {
                return jsonContent + ":" + array.toString();
            }
            mUploadObjectIds = new ArrayList<String>();
            do {
                JSONObject json = new JSONObject();
                for (MappingDbJson map : maps) {
                    String value = c.getString(c.getColumnIndexOrThrow(map.mDbColName));
                    json.put(map.mJsonColName, value);
                }
                array.put(json);
                mUploadObjectIds.add(c.getString(c.getColumnIndex(getmPrimaryKey())));
            } while (c.moveToNext());

            jsonContent = jsonContent + ":" + array.toString();

        } catch (Exception e) {
            Log.v("DataBase", "toCustomJson failed");
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return jsonContent;
    }

    private List<String> mUploadObjectIds;

    public List<String> getUploadObjectIds() {
        return mUploadObjectIds;
    }

    public synchronized void deleteUploadObjectId(int key, String id) {

        Log.v("TaskManager", "upload deleteUploadObjectId:" + key + "," + id);

        if (mUploadObjectIds != null) {
            for (int i = 0; i < mUploadObjectIds.size(); i++) {
                Log.v("TaskManager", "upload id:" + mUploadObjectIds.get(i));
                if (id == mUploadObjectIds.get(i)) {
                    mUploadObjectIds.remove(i);
                    Log.v("TaskManager", "upload removed " + key + "," + id);
                }
            }
        }
    }

    /**
     * 从数据库删除一个值
     * @return 是否删除成功
     */
    public boolean deleteOneBeanFromDB() {
        boolean flag = false;
        try {
            FrameHelper.getInstance().getDB().execSQL("delete from " + getTableName() + " where " + getmPrimaryKey() + " = '" + safeSQLString(getmPrimaryKeyValue()) + "'") ;
            flag = true;
        } catch (Exception e) {
           Log.e("sql",e.toString());
        }

        return flag;
    }

    public synchronized void setUploadObjectIds(List<String> lists) {
        mUploadObjectIds = lists;
    }


}
