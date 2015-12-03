package com.example.chengcheng.annotation;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日13:15:06
 * Description:
 */
public class MappingDbJson {

    //数据库里面列的名称
    public String mDbColName;

    //组织成json的时候json的键的名称
    public String mJsonColName;

    public MappingDbJson(String db,String json){
        mDbColName=db;
        mJsonColName=json;
    }


}
