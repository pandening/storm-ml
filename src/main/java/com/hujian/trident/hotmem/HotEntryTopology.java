package com.hujian.hotmem;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.hotmem.functions.HotEntryFrequencyUpdater;
import com.hujian.hotmem.functions.RedisStatueUpdate;
import com.hujian.hotmem.functions.TailFunction;
import com.hujian.hotmem.mysql.MysqlConfig;
import com.hujian.hotmem.redis.RedisConfig;
import com.hujian.hotmem.source.ComedyComparisonsInstanceCreator;
import com.hujian.hotmem.source.ComedyVoteSpout;
import com.hujian.trident.ml.frequency.lossyCounting.LossyCounting;
import com.hujian.trident.ml.frequency.spaceSaving.SpaceSavingIml;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/24.
 */
public class HotEntryTopology implements Serializable{

    private static final long serialVersionUID = - 124134211498L;

    public static void main(String[] args){


        String url = "jdbc:mysql://10.134.72.137/hujian";
        String driver = "com.mysql.jdbc.Driver";
        String username = "root";
        String password = "root";
        RedisConfig redisConfig = new RedisConfig("10.134.72.137","6379",60000);
        MysqlConfig mysqlConfig = new MysqlConfig(url,username,password,driver);

        @SuppressWarnings("serial")TridentTopology tridentTopology = new TridentTopology();
        @SuppressWarnings("serial")String file = "I:\\comedy_comparisons.train";


        tridentTopology
                .newStream("comedyVoteRequest",new ComedyVoteSpout(file,200000))
                //.parallelismHint(4)
                .each(new Fields("id","comedyLeft","comedyRight","comedyVote"),
                        new ComedyComparisonsInstanceCreator(),new Fields("comedyInstance"))
                //.parallelismHint(5)
                .partitionPersist(new MemoryMapState.Factory(),new Fields("comedyInstance"),
                        new HotEntryFrequencyUpdater("hotMem",
                                new SpaceSavingIml<String>(10000),redisConfig,mysqlConfig,10L),
                        new Fields("comedy","vote","instanceId","lossy"))
                //.parallelismHint(10)
                .newValuesStream()
                .each(new Fields("comedy","vote","instanceId","lossy"),
                        new RedisStatueUpdate(10,redisConfig,mysqlConfig),
                        new Fields("id","comedyName","voteValue"))
                //.parallelismHint(10)
                .each(new Fields("id","comedyName","voteValue"),new TailFunction(),new Fields("Tail"));


        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();

        localCluster.submitTopology("HotMemTopology",config,tridentTopology.build());

    }
}
