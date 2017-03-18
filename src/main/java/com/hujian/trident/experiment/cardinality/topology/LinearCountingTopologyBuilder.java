package com.hujian.trident.experiment.cardinality.topology;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.experiment.cardinality.functions.CardinalityUpdate;
import com.hujian.trident.experiment.cardinality.functions.FinalFunction;
import com.hujian.trident.experiment.core.InstanceCreator;
import com.hujian.trident.experiment.data.DoubleWithIDSpout;
import com.hujian.trident.ml.cardinality.LinearCountingCardinality;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/15.
 */
public class LinearCountingTopologyBuilder {

    /**
     * builder the topology
     * @param batchSize
     * @param bitmapSize
     * @return
     */
    public static StormTopology builder(Integer batchSize,Integer bitmapSize){
        TridentTopology tridentTopology = new TridentTopology();

        tridentTopology
                .newStream("LinearCounting",new DoubleWithIDSpout(batchSize))
                .each(new Fields("id","data","type"),new InstanceCreator<Double>(1),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new CardinalityUpdate("Linear-Counting",
                                new LinearCountingCardinality(bitmapSize)),new Fields("id","frequency"))
                .newValuesStream().each(new Fields("id","frequency"),new FinalFunction<Double>(),
                new Fields("end"));
        return tridentTopology.build();
    }
}
