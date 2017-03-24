package com.hujian.hotmem.functions;

import backtype.storm.tuple.Values;
import com.hujian.hotmem.mysql.Mysql;
import com.hujian.hotmem.mysql.MysqlConfig;
import com.hujian.hotmem.redis.RedisCache;
import com.hujian.hotmem.redis.RedisConfig;
import com.hujian.trident.ml.frequency.spaceSaving.SpaceSavingIml;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hujian on 2017/3/24.
 */
public class RedisStatueUpdate extends BaseFunction implements Serializable{

    private static final long serialVersionUID = - 122223124134211498L;

    private Boolean useCache = null;
    private RedisConfig redisConfig = null;
    private RedisCache redisCache = null;
    private MysqlConfig mysqlConfig = null;
    private Mysql mysql = null;
    private SpaceSavingIml<String> spaceSavingIml = null;
    private Long instanceID = null;
    private int vote  = 0;
    private int redisSize = 0;
    private String comedyName = null;

    /**
     * constructor
     * @param redisSize
     * @param mysqlConfig
     * @param redisConfig
     */
    @SuppressWarnings("serial")
    public RedisStatueUpdate(int redisSize,RedisConfig redisConfig,MysqlConfig mysqlConfig){
        this.redisSize = redisSize;
        this.redisConfig = redisConfig;
        this.mysqlConfig = mysqlConfig;
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if (tridentTuple == null) {
            System.out.println("[" + RedisStatueUpdate.class.getName() + "] :trident tuple is null");
            return;
        }
        //get the redis and mysql
        this.redisCache = RedisCache.getInstance(this.redisConfig.getHost(),this.redisConfig.getPort(),
                this.redisConfig.getTimeout());
        this.mysql = Mysql.getInstance(this.mysqlConfig.getConnectUrl(),this.mysqlConfig.getUserName(),
                this.mysqlConfig.getPassword(),this.mysqlConfig.getDriver());

        if( this.redisCache == null || this.mysql == null ){
            System.out.println("["+HotEntryFrequencyUpdater.class.getName()+"] get empty redis or mysql instance");
            return;
        }

        //if use redis cache
        this.useCache = this.redisCache.getJedis().get( "use_cache" ).trim().equals("1") ? Boolean.TRUE: Boolean.FALSE;

        //get the vote,id,frequency object
        this.comedyName = tridentTuple.getString( 0 );
        this.vote = tridentTuple.getInteger(1);
        this.instanceID = tridentTuple.getLong(2);
        this.spaceSavingIml = (SpaceSavingIml<String>) tridentTuple.get(3);

        if( this.spaceSavingIml == null ){
            System.out.println("get empty space saving model");
            return;
        }

        //if use cache
        if (this.useCache.equals(Boolean.TRUE)) {

            Long currentRedisCacheSize = this.redisCache.getJedis().scard("hotKeys");

            if( currentRedisCacheSize < this.redisSize ){
               // System.out.println("["+RedisStatueUpdate.class.getName()+"] the redis is not full:" +
                //        currentRedisCacheSize + "/" + this.redisSize);
                Values values = new Values();
                values.add( this.instanceID );
                values.add( this.comedyName );
                values.add( this.vote );
                //id ,comedy_name, vote
                tridentCollector.emit( values);
                return;
            }else {//the redis is full

                List<Map.Entry<String,Long>> topK = this.spaceSavingIml.peek(this.redisSize);

                if (topK == null) {
                    System.out.println("[" + RedisStatueUpdate.class.getName() + "] get empty topK");
                    return;
                }

                //get hotEntry from redis
                Set<String> hotEntry = this.redisCache.getJedis().zrange("hotEntry", 0, -1);

                //System.out.println("[" + RedisStatueUpdate.class.getName() + "] redis is full:" +
                 //       currentRedisCacheSize + "/" + this.redisSize);
                //remove the redis some entries
                for (String hotComedy : hotEntry) {
                    topK.remove(hotComedy);
                }

                //if there is new hot entry
                if (topK.size() != 0) {
                    //remove some cache entry from redis,and flush the entry's information to mysql
                    Set<String> removeKeys = this.redisCache.getJedis().zrange("hotEntry", 0, topK.size());
                    for (String key : removeKeys) {
                        //get the vote value
                        int voteValue = (int) Math.round(this.redisCache.getJedis().zincrby("hotEntry", 0, key));

                        //if this entry existed mysql
                        String sql = "select vote from comedyVote where comedy_name = '" + key + "'";
                        ResultSet resultSet = this.mysql.execSql(sql);
                        if( resultSet != null ) {
                            try {
                                String voteInfo = null;
                                while (resultSet.next()) {
                                    voteInfo = resultSet.getString(1);
                                    break;
                                }
                                if (voteInfo == null || voteInfo.length() == 0) {
                                    sql = "insert into comedyVote values( '" + key + "', " + voteValue + ")";
                                    this.mysql.exec(sql);
                                    this.vote = voteValue;
                                } else {
                                    this.vote = Integer.parseInt( voteInfo );
                                    //update the vote
                                    sql = "update comedyVote set vote = " + vote + " where comedy_name = '" + key + "'";
                                    this.mysql.exec(sql);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }else{
                            sql = "insert into comedyVote values( '" + key + "', " + voteValue + ")";
                            try {
                                this.mysql.exec(sql);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            this.vote = voteValue;
                        }
                        this.redisCache.getJedis().zrem("hotEntry", key);
                        this.redisCache.getJedis().srem("hotKeys", key);
                    }

                    //System.out.println("[" + RedisStatueUpdate.class.getName() + "] remove " + topK.size() + " entries from redis");

                    //add new hot entry from mysql to cache
                    for (Map.Entry<String,Long> entry : topK) {
                        //insert into redis
                        this.redisCache.getJedis().sadd("hotKeys", entry.getKey());
                        this.redisCache.getJedis().zincrby("hotEntry", 1.0 * this.vote, entry.getKey());
                    }

                    //System.out.println("[" + RedisStatueUpdate.class.getName() + "] add " + topK.size() + " hotEntries to redis");
                }else{
                    System.out.println("["+RedisStatueUpdate.class.getName()+"] it's unnecessary to change cache");
                }
            }
        }

        Values values = new Values();
        values.add( this.instanceID );
        values.add( this.comedyName );
        values.add( this.vote );

        //id ,comedy_name, vote
        tridentCollector.emit( values);
    }
}
