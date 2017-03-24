package com.hujian.hotmem.functions;

import backtype.storm.tuple.Values;
import com.hujian.hotmem.mysql.Mysql;
import com.hujian.hotmem.mysql.MysqlConfig;
import com.hujian.hotmem.redis.RedisCache;
import com.hujian.hotmem.redis.RedisConfig;
import com.hujian.hotmem.source.ComedyComparisonsInstance;
import com.hujian.trident.ml.frequency.lossyCounting.LossyCounting;
import com.hujian.trident.ml.frequency.spaceSaving.SpaceSavingIml;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/23.
 */
public class HotEntryFrequencyUpdater extends BaseStateUpdater<MapState<SpaceSavingIml<String>>>
        implements Serializable {

    private static final long serialVersionUID = - 124134211498L;

    private String SpaceSavingModelName = null;
    private String lossyCountingModelName = null;
    private LossyCounting<String> lossyCounting = null;
    private SpaceSavingIml<String> stringSpaceSaving = null;
    private Long redisSize = 0L;
    private RedisConfig redisConfig = null;
    private RedisCache redisCache = null;
    private MysqlConfig mysqlConfig = null;
    private Mysql mysql = null;
    private Boolean useCache = null;
    private Boolean useMysql = null;
    private int vote = 0;

    /**
     * offer the model name and the initialization lossy counting instance
     * @param lossyCountingModelName
     * @param lossyCounting
     * @param mysqlConfig
     * @param redisCacheConfig
     * @param redisSize
     */
    @SuppressWarnings("serial")
    public HotEntryFrequencyUpdater(String lossyCountingModelName, LossyCounting<String> lossyCounting,
                                    RedisConfig redisCacheConfig, MysqlConfig mysqlConfig,Long redisSize ){
        this.lossyCountingModelName = lossyCountingModelName;
        this.lossyCounting = lossyCounting;
        this.redisConfig = redisCacheConfig;
        this.mysqlConfig = mysqlConfig;
        this.redisSize = redisSize;
        this.useMysql = Boolean.TRUE;
    }

    /**
     *
     * @param spaceSavingModelName
     * @param spaceSavingIml
     * @param redisCacheConfig
     * @param mysqlConfig
     * @param redisSize
     */
    public HotEntryFrequencyUpdater(String spaceSavingModelName, SpaceSavingIml<String> spaceSavingIml,
                                    RedisConfig redisCacheConfig, MysqlConfig mysqlConfig,Long redisSize ){
        this.SpaceSavingModelName = spaceSavingModelName;
        this.stringSpaceSaving = spaceSavingIml;
        this.redisConfig = redisCacheConfig;
        this.mysqlConfig = mysqlConfig;
        this.redisSize = redisSize;
        this.useMysql = Boolean.TRUE;
    }

    @Override
    public void updateState(MapState<SpaceSavingIml<String>> spaceSavingImlMapState,
                            List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old lossy counting model
        List<SpaceSavingIml<String>> spaceSavingImlList =
                spaceSavingImlMapState.multiGet(
                        Arrays.asList(Arrays.asList((Object) (this.SpaceSavingModelName))));

        //get the redis and mysql
        this.redisCache = RedisCache.getInstance(this.redisConfig.getHost(), this.redisConfig.getPort(),
                this.redisConfig.getTimeout());
        this.mysql = Mysql.getInstance(this.mysqlConfig.getConnectUrl(), this.mysqlConfig.getUserName(),
                this.mysqlConfig.getPassword(), this.mysqlConfig.getDriver());

        if (this.redisCache == null || this.mysql == null) {
            System.out.println("[" + HotEntryFrequencyUpdater.class.getName() + "] get empty redis or mysql instance");
            return;
        }

        //if use redis cache
        this.useCache = this.redisCache.getJedis().get("use_cache").trim().equals("1") ? Boolean.TRUE : Boolean.FALSE;

        SpaceSavingIml<String> stringSpaceSavingIml_ = null;

        if (spaceSavingImlList != null && spaceSavingImlList.size() != 0) {
            stringSpaceSavingIml_ = spaceSavingImlList.get(0);
        }

        //if this is the first time to update the model.
        if (stringSpaceSavingIml_ == null) {
            stringSpaceSavingIml_ = this.stringSpaceSaving;
            this.useMysql = Boolean.TRUE;
        }

        if( stringSpaceSaving == null ){
            stringSpaceSaving = new SpaceSavingIml<>(1000);
            this.useMysql = Boolean.TRUE;
        }

        //get the comedy comparison instance
        for (TridentTuple tridentTuple : list) {
            ComedyComparisonsInstance comparisonsInstance =
                    (ComedyComparisonsInstance) tridentTuple.get(0);
            if (comparisonsInstance == null) {
                continue;
            }

            //the vote comedy
            String voteComedy = comparisonsInstance.getComedyVote() == "left" ?
                    comparisonsInstance.getComedyLeft() : comparisonsInstance.getComedyRight();

            if (voteComedy == null) {
                continue;
            }

            /**
             * if the system use redis cache
             */
            if (this.useCache.equals(Boolean.TRUE)) {
                //update the model
                stringSpaceSaving.add(voteComedy,1L);

                //if the comedy existed in the hotKeys
                if (this.redisCache.getJedis().sismember("hotKeys", voteComedy).equals(Boolean.TRUE)) {

                    //get the new vote of this comedy
                    this.vote = (int) Math.round(this.redisCache.getJedis().zincrby("hotEntry", 1, voteComedy));
                    this.useMysql = Boolean.FALSE;
                } else {
                    //if the cache is not full,just insert into redis,or,use mysql
                    Long currentRedisCacheSize = this.redisCache.getJedis().scard("hotKeys");
                    if (this.redisSize > currentRedisCacheSize) {
                        //get the new vote of this comedy
                        this.redisCache.getJedis().sadd("hotKeys", voteComedy);
                        //if this comedy is existed mysql
                        //get the old vote value
                        String sql = "select vote from comedyVote where comedy_name = '" + voteComedy + "'";
                        ResultSet resultSet = this.mysql.execSql(sql);
                        String voteInfo = null;
                        if (resultSet != null) {
                            try {
                                while (resultSet.next()) {
                                    voteInfo = resultSet.getString(1);
                                    break;
                                }
                                if (voteInfo == null || voteInfo.length() == 0) {
                                    this.vote = (int) Math.round(this.redisCache.getJedis().zincrby("hotEntry", 1, voteComedy));
                                } else {
                                    this.vote = Integer.parseInt(voteInfo) + 1;
                                    //update the vote
                                    this.vote = (int) Math.round(this.redisCache.getJedis().zincrby("hotEntry", this.vote, voteComedy));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            this.vote = (int) Math.round(this.redisCache.getJedis().zincrby("hotEntry", 1, voteComedy));
                        }
                        this.useMysql = Boolean.FALSE;
                    }else{
                        //use mysql
                        this.useMysql = Boolean.TRUE;
                    }
                }
            }

                /**
                 * is just need mysql or not existed in redis cache
                 */
                if (this.useMysql.equals(Boolean.TRUE)) {

                    //get the old vote value
                    String sql = "select vote from comedyVote where comedy_name = '" + voteComedy + "'";
                    ResultSet resultSet = this.mysql.execSql(sql);
                    if (resultSet != null) {
                        try {
                            String voteInfo = null;
                            while (resultSet.next()) {
                                voteInfo = resultSet.getString(1);
                                break;
                            }
                            if (voteInfo == null || voteInfo.length() == 0) {
                                sql = "insert into comedyVote values( '" + voteComedy + "',1)";
                                this.mysql.exec(sql);
                                this.vote = 1;
                            } else {
                                this.vote = Integer.parseInt(voteInfo) + 1;
                                //update the vote
                                sql = "update comedyVote set vote = " + vote + " where comedy_name = '" + voteComedy + "'";
                                this.mysql.exec(sql);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {//insert into mysql
                        sql = "insert into comedyVote values( '" + voteComedy + "',1)";
                        try {
                            this.mysql.exec(sql);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        this.vote = 1;
                    }
                }

                //
                //   comedy_name,vote,id,frequency_instance
                //
                Values values = new Values();
                values.add(voteComedy);
                values.add(this.vote);
                values.add(comparisonsInstance.getInstanceId());
                values.add(stringSpaceSaving);

                tridentCollector.emit(values);
        }
        //save the updated frequency model
        spaceSavingImlMapState.multiPut(Arrays.asList(Arrays.asList((Object) this.SpaceSavingModelName)),
                Arrays.asList(stringSpaceSavingIml_));
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
