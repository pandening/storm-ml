package com.hujian.hotmem.mysql;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/24.
 */
public class MysqlConfig implements Serializable {

    private String connectUrl = null;
    private String userName = null;
    private String password = null;
    private String driver = null;

    /**
     * constructor
     * @param url
     * @param userName
     * @param password
     * @param driver
     */
    public MysqlConfig(String url , String userName,String password,String driver){
        this.connectUrl = url;
        this.userName = userName;
        this.password = password;
        this.driver = driver;
    }

    public String getConnectUrl() {
        return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
