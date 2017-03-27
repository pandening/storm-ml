package com.hujian.hotmem.redis;

import redis.clients.jedis.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hujian on 2017/3/23.
 */
@SuppressWarnings("serial")
public class RedisCache implements Serializable{

    private static final long serialVersionUID = 9001656L;

    private static RedisCache ourInstance = null;

    public static RedisCache getInstance(String ip,String port,int timeout) {
        if( ourInstance == null ){
            ourInstance = new RedisCache(ip,port,timeout);
        }
        return ourInstance;
    }
    /**
     * the local Jedis pool
     */
    private JedisPool     jedisPool = null;
    /**
     * the local jedis client
     */
    private Jedis          jedis = null;
    /**
     * the remote pool,for hash store
     */
    private ShardedJedisPool  shardedJedisPool = null;
    /**
     * the share remote Jedis client(hash to store)
     */
    private ShardedJedis  shardedJedis = null;

    /**
     * the simple constructor
     * @param ip the redis server's ip address
     * @param port the redis server listener port
     * @param timeout the connection timeout
     */
    private RedisCache(String ip,String port,int timeout){
        /**
         * local pool
         */
        JedisPoolConfig jConfig = new JedisPoolConfig();
        jConfig.setMaxIdle(50);
        jConfig.setMaxIdle(10);

        jConfig.setMaxWaitMillis(timeout);
        jConfig.setTestOnBorrow(false);
        jedisPool = new JedisPool(jConfig,ip,Integer.parseInt(port));
        /**
         * remote pool,you can add the connection here
         */
        List<JedisShardInfo> shardInfos = new ArrayList<JedisShardInfo>();
        shardInfos.add(new JedisShardInfo(ip,6379,"master"));
        shardedJedisPool=new ShardedJedisPool(jConfig,shardInfos);
        /**
         * get the jedis and shared jedis client
         */
        jedis = jedisPool.getResource();
        shardedJedis = shardedJedisPool.getResource();
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public ShardedJedis getShardedJedis() {
        return shardedJedis;
    }

    public void setShardedJedis(ShardedJedis shardedJedis) {
        this.shardedJedis = shardedJedis;
    }
}
