package com.example.chengcheng.sqlite;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日10:59:57
 * Description:
 */
public class FrameHelper {

    private static final String LOG_TAG ="FrameHelper";
    public static final String DB_FILE_NAME="hbcj.db";

    private static String  APP_DATA_PATH;
    private static String  DB_FILE_PATH;
    private static Context appContext;
    private boolean  mIsInited;
    private SQLiteDatabase myDb = null;

    private static FrameHelper   mInstance = null;
    private FrameHelper() {
        super();
    }

    /**
     * Application must set the context at the very beginning even if the method
     *  will be called soon.
     * @param context: the context to pass in.
     */
    public static void setAppContext(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    /**
     * To get the application context which passed to Frame when initialize it.
     * @return the global application context instance.
     * @see FrameHelper::init(Context context).
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * To get the singleton instance of Frame. Should call init() before use the instance returned.
     * @return the singleton instance of Frame.
     */
    public synchronized static FrameHelper getInstance() {
        if (mInstance == null) {
            mInstance = new FrameHelper();
        }

        return mInstance;
    }


    public static String getDatabasePath() {
        if (DB_FILE_PATH == null) {
            StringBuilder sb = new StringBuilder(getDataPath());
            sb.append("/");
            sb.append(DB_FILE_NAME);
            DB_FILE_PATH = sb.toString();
        }

        return DB_FILE_PATH;
    }

    public static String getDataPath() {
        if (APP_DATA_PATH != null)
            return APP_DATA_PATH;

        PackageManager pm = appContext.getPackageManager();
        String strPackName = appContext.getPackageName();
        PackageInfo p = null;
        try {
            p = pm.getPackageInfo(strPackName, 0);
            APP_DATA_PATH = p.applicationInfo.dataDir;
            return APP_DATA_PATH;
        } catch (PackageManager.NameNotFoundException e) {
            Log.v(LOG_TAG, null, e);
            return null;
        }
    }

    /**
     * @return the SQLiteDatabase instance to the database of Wacai.
     */
    public synchronized SQLiteDatabase getDB() {
        if (!mIsInited) {
            init();
        }

        return openDB();
    }

    private SQLiteDatabase openDB() {
        if (null == myDb || !myDb.isOpen()) {
            try {
                Log.v(LOG_TAG, "SqliteFrame openDB  :)");
                myDb = SQLiteDatabase.openDatabase(getDatabasePath(), null,
                        SQLiteDatabase.OPEN_READWRITE);
            } catch (Exception e) {
                Log.v(LOG_TAG, "Open DB: error = ", e);
            }
        }

        return myDb;
    }

    /**
     * It initialize the SqliteFrame work, including prepare/upgrade the database and so on.
     * @return True if initialization finished successfully, else return false.
     */
    public synchronized boolean init() {
        if (mIsInited)
            return true;

        if (appContext == null)
            return false;

        File dbFile = new File(getDatabasePath());
        if (!dbFile.exists()) {
            if (!generateDBFile(false)) {
                mIsInited = false;
                return false;
            }
        } else {
            Log.e(LOG_TAG, "update DB start !!!!");
        }

        mIsInited = true;

        return true;
    }
    private boolean generateDBFile(boolean isRemoveOldDB) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            if (myDb != null && myDb.isOpen() ) {
                myDb.close();
                myDb = null;
            }

            String strDBPath = getDatabasePath();
            File file = new File(strDBPath);
            if (file.exists()) {
                if (isRemoveOldDB) {
                    file.delete();
                } else {
                    return false;
                }
            }

            AssetManager am = appContext.getAssets();
            is = am.open(DB_FILE_NAME);
            fos = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int readLen = 0;
            while (readLen != -1) {
                readLen = is.read(buf, 0, 1024);
                if (readLen > 0) {
                    fos.write(buf, 0, readLen);
                }
            }
            return true;
        } catch(Exception e) {
            return false;
        } finally {
            try {
                if(null != fos)
                    fos.close();
                if(null != is)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
