package com.hujian.trident.experiment.speed;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;
import com.hujian.trident.experiment.core.InstanceCreator;
import com.hujian.trident.experiment.data.DoubleWithIDSpout;
import com.hujian.trident.ml.cardinality.AdaptiveCountingCardinality;
import com.hujian.trident.ml.cardinality.HyperLogLogCardinality;
import com.hujian.trident.ml.cardinality.LinearCountingCardinality;
import com.hujian.trident.ml.cardinality.LogLogCardinality;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/15.
 */
public class SpeedTopology {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        TridentTopology tridentTopology = new TridentTopology();

        tridentTopology
                .newStream("speed-loglog",new DoubleWithIDSpout(100000))
                //.parallelismHint(10)
                .each(new Fields("id","data","type"),new InstanceCreator<Double>(1),
                        new Fields("instance"))
                //.parallelismHint(20)
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new LogLogModelUpdateForSpeedTest("loglog-speed",
                                new LogLogCardinality(10)));
        //.parallelismHint(20);
        Config config = new Config();
        config.setNumWorkers(8);

        //cluster model
        //StormSubmitter.submitTopologyWithProgressBar("Speed-of-LogLog-Cardinality-topology",
        //        config,tridentTopology.build());

        //local model
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("speed-loglog",new Config(),tridentTopology.build());
    }

}
