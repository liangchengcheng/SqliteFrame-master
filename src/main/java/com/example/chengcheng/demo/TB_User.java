package com.example.chengcheng.demo;


import com.example.chengcheng.annotation.SerializedDbInfo;
import com.example.chengcheng.sqlite.DataBase;

/**
 * @Description 用户表
 * @author  chaoshuai
 * @date 2015/4/15.
 */
public class TB_User extends DataBase {
    public static final String TABLE_NAME = "TB_User";

    public static final String FIELD_EMP_ID               = "EmpID";    //用户编号
    public static final String FIELD_EMP_NAME             = "EmpName";  //用户名称
    public static final String FIELD_LOGIN_ID             = "LoginID";  //登录名
    public static final String FIELD_PASSWORD             = "Password"; //密码
    public static final String FIELD_EMPTEL               = "EmpTel";   //座机
    public static final String FIELD_EMPMOBILE            = "EmpMobile";//手机
    public static final String FIELD_F0001                = "F0001";    //照片

    @SerializedDbInfo(colame=FIELD_EMP_ID, dataType="String")
    private String mEmpID;

    @SerializedDbInfo(colame=FIELD_EMP_NAME, dataType="String")
    private String mEmpName;

    @SerializedDbInfo(colame=FIELD_LOGIN_ID, dataType="String")
    private String mLoginID;

    @SerializedDbInfo(colame=FIELD_PASSWORD, dataType="String")
    private String mPassword;

    @SerializedDbInfo(colame=FIELD_EMPTEL, dataType="String")
    private String mEmpTel;

    @SerializedDbInfo(colame=FIELD_EMPMOBILE, dataType="String")
    private String mEmpMobile;

    @SerializedDbInfo(colame=FIELD_F0001, dataType="byte")
    private String mF0001;

    public String getmEmpID() {
        return mEmpID;
    }

    public void setmEmpID(String mEmpID) {
        this.mEmpID = mEmpID;
    }

    public String getmEmpName() {
        return mEmpName;
    }

    public void setmEmpName(String mEmpName) {
        this.mEmpName = mEmpName;
    }

    public String getmLoginID() {
        return mLoginID;
    }

    public void setmLoginID(String mLoginID) {
        this.mLoginID = mLoginID;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmEmpTel() {
        return mEmpTel;
    }

    public void setmEmpTel(String mEmpTel) {
        this.mEmpTel = mEmpTel;
    }

    public String getmEmpMobile() {
        return mEmpMobile;
    }

    public void setmEmpMobile(String mEmpMobile) {
        this.mEmpMobile = mEmpMobile;
    }

    public String getmF0001() {
        return mF0001;
    }

    public void setmF0001(String mF0001) {
        this.mF0001 = mF0001;
    }

    @Override
    protected boolean onCheckParams() {
        return true;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getmPrimaryKey() {
        return FIELD_EMP_ID;
    }

    @Override
    protected String getmPrimaryKeyValue() {
        return mEmpID;
    }

    /**
     *
     * @param username TB_User 表中的LoginID
     * @return 从数据库中查询到 LoginID = username 的数据
     */
    public static TB_User selectOneBeanFromeID(String username) {
        return deserializeFromDb(TB_User.class, FIELD_LOGIN_ID, username);
    }

    @Override
    public String toString() {
        return "TB_User{" +
                "mEmpID='" + mEmpID + '\'' +
                ", mEmpName='" + mEmpName + '\'' +
                ", mLoginID='" + mLoginID + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mEmpTel='" + mEmpTel + '\'' +
                ", mEmpMobile='" + mEmpMobile + '\'' +
                ", mF0001='" + mF0001 + '\'' +
                '}';
    }
}
