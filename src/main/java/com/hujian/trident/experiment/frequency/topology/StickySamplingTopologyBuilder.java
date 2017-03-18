package com.hujian.trident.experiment.frequency.topology;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.experiment.core.InstanceCreator;
import com.hujian.trident.experiment.data.IntegerWithIDSpout;
import com.hujian.trident.experiment.frequency.functions.CloseOutFunction;
import com.hujian.trident.experiment.frequency.functions.StickySamplingCountingUpdate;
import com.hujian.trident.ml.frequency.SamplingCounting.StickySamplingCounting;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/14.
 */
public class StickySamplingTopologyBuilder {

    /**
     * topology builder of Lossy Counting
     * @param error
     * @return
     */
    public static StormTopology builder(Integer batchSize,double error,double support,double epsilon){
        TridentTopology tridentTopology = new TridentTopology();
        tridentTopology
                .newStream("Sticky-sampling-counting",new IntegerWithIDSpout(batchSize))
                .each(new Fields("id","data","type"),new InstanceCreator<Integer>(),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new StickySamplingCountingUpdate("Sticky-sampling-counting",
                                new StickySamplingCounting(error,support,epsilon)),
                        new Fields("id","data","frequency"))
                .newValuesStream().each(new Fields("id","data","frequency"),
                new CloseOutFunction<Integer>(),new Fields("end"));
        return tridentTopology.build();
    }
}
