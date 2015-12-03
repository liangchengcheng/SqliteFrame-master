package com.example.chengcheng.sqlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日13:28:47
 * Description:
 */
public class GsonHelper {

    private static GsonHelper mInstance;

    public static GsonHelper getmInstance(){
        if (mInstance==null){
            mInstance=new GsonHelper();
        }
        return mInstance;
    }

    private final Gson mGson=buildGson();

    public  Gson getGson(){
        return mGson;
    }

    private Gson buildGson(){
        Gson gson= new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        return gson;
    }
}
