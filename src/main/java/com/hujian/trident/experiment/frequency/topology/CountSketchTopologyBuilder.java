package com.hujian.trident.experiment.frequency.topology;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.experiment.core.InstanceCreator;
import com.hujian.trident.experiment.data.IntegerWithIDSpout;
import com.hujian.trident.experiment.frequency.functions.CloseOutFunction;
import com.hujian.trident.experiment.frequency.functions.CountSketchUpdate;
import com.hujian.trident.ml.frequency.CountSketch.CountSketchIml;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/14.
 */
public class CountSketchTopologyBuilder {

    /**
     * the builder
     * @param batchSize
     * @param epsilon
     * @param gamma
     * @return
     */
    public static StormTopology builder(Integer batchSize,double epsilon, double gamma){
        TridentTopology tridentTopology = new TridentTopology();
        tridentTopology
                .newStream("countSketch",new IntegerWithIDSpout(batchSize))
                .each(new Fields("id","data","type"),new InstanceCreator<Integer>(),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new CountSketchUpdate("cs",
                                new CountSketchIml(epsilon,gamma)),
                        new Fields("id","data","frequency")).newValuesStream()
                .each(new Fields("id","data","frequency"),new CloseOutFunction<Integer>(),new Fields("end"));
        return tridentTopology.build();
    }
}
