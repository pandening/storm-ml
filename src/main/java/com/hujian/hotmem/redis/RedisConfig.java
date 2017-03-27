package com.hujian.hotmem.redis;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/24.
 */
public class RedisConfig implements Serializable{

    private String host = null;
    private String port = null;
    private int timeout = 0;

    /**
     * constructor
     * @param host
     * @param port
     * @param timeout
     */
    public RedisConfig(String host , String port , int timeout){
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
