package com.hujian.hotmem.mysql;

import java.io.Serializable;
import java.sql.*;

/**
 * Created by hujian on 2017/3/24.
 */
@SuppressWarnings("serial")
public class Mysql implements Serializable {

    private static final long serialVersionUID = - 12413421149101028L;

    private String connectUrl = null;
    private String userName = null;
    private String password = null;
    private String driver = null;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;

    private static Mysql mysql = null;

    /**
     * the constructor
     * @param connectUrl
     * @param userName
     * @param password
     * @param driver
     */
    private Mysql(String connectUrl,String userName,String password,String driver){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(connectUrl, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get a mysql instance
     * @param connectUrl
     * @param userName
     * @param password
     * @param driver
     * @return
     */
    public static Mysql getInstance(String connectUrl,String userName,String password,String driver){
        if( mysql == null ){
            mysql = new Mysql(connectUrl,userName,password,driver);
        }
        return mysql;
    }

    /**
     * get an new mysql instance
     * @param connectUrl
     * @param userName
     * @param password
     * @param driver
     * @return
     */
    public static Mysql getNewMysql(String connectUrl,String userName,String password,String driver){
        mysql = new Mysql(connectUrl,userName,password,driver);
        return mysql;
    }

    /**
     * do mysql query
     * @param sql
     * @return
     */
    public ResultSet execSql(String sql){
        ResultSet resultSet = null;
        try {
            this.preparedStatement = this.connection.prepareStatement(sql);
            resultSet = this.preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * just exec
     * @param sql
     * @return
     */
    public Boolean exec( String sql ) throws SQLException {
        this.preparedStatement = this.connection.prepareStatement( sql );
        return this.preparedStatement.execute();
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

    public Connection getConnection(){
        return this.connection;
    }

    public PreparedStatement getPreparedStatement(){
        return this.preparedStatement;
    }

}
