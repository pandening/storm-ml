package com.hujian.trident.experiment.cardinality.topology;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.experiment.cardinality.functions.CardinalityUpdate;
import com.hujian.trident.experiment.cardinality.functions.FinalFunction;
import com.hujian.trident.experiment.core.InstanceCreator;
import com.hujian.trident.experiment.data.DoubleWithIDSpout;
import com.hujian.trident.ml.cardinality.HyperLogLogCardinality;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/15.
 */
public class HyperLogLogCardinalityTopology {

    /**
     * builder
     * @param batchSize
     * @param bucketBitesSize
     * @return
     */
    public static StormTopology builder(int batchSize,int bucketBitesSize){
        TridentTopology tridentTopology = new TridentTopology();

        tridentTopology
                .newStream("HyperLogLog",new DoubleWithIDSpout(batchSize))
                .each(new Fields("id","data","type"),new InstanceCreator<Double>(1),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new CardinalityUpdate("hyper-LogLog",
                                new HyperLogLogCardinality(bucketBitesSize)),new Fields("id","frequency"))
                .newValuesStream().each(new Fields("id","frequency"),
                new FinalFunction<Double>(),new Fields("end"));
        return tridentTopology.build();
    }

}
