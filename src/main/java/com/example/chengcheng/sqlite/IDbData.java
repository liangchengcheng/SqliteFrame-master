package com.example.chengcheng.sqlite;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年12月3日11:01:33
 * Description:
 */
public interface IDbData {

    /**
     * 获取数据库的表的名字
     * @return 表名
     */
    String getTableName();

    /**
     * 判断item是否存在
     * @return 是否
     */
    boolean isItemExist();

    /**
     * insert语句
     * @return  语句
     */
    String genInsertSql();

    /**
     * 更新语句
     * @return 语句
     */
    String genUpdateSql();

    /**
     * 在save之前check
     * @return 是否
     */
    boolean checkParams();

    /**
     * sav语句的执行
     */
    void save();
}
