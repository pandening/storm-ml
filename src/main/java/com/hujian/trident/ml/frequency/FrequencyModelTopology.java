package com.hujian.trident.ml.frequency;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.IntegerSpout;
import com.hujian.trident.ml.frequency.CountSketch.CountSketchIml;
import com.hujian.trident.ml.frequency.CountSketch.CountSketchModelUpdate;
import com.hujian.trident.ml.frequency.SamplingCounting.StickySamplingCounting;
import com.hujian.trident.ml.frequency.TopKCounting.FrequentTopKIml;
import com.hujian.trident.ml.frequency.TopKCounting.TopKModelUpdater;
import com.hujian.trident.ml.frequency.spaceSaving.SpaceSavingIml;
import com.hujian.trident.ml.frequency.spaceSaving.SpaceSavingModelUpdater;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/10.
 */
public class FrequencyModelTopology {

    /**
     * according to the algorithm,build the topology
     * @param algorithm like SpaceSaving..
     * @return null means no such algorithm till now
     */
    public static StormTopology builder(String algorithm){

        TridentTopology tridentTopology = new TridentTopology();

        if( algorithm == "CountSketch"){
            tridentTopology
                    .newStream("CountSketch",new IntegerSpout(10))
                    .each(new Fields("item","frequency","type"),
                            new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                    .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                            new CountSketchModelUpdate("count-sketch",
                                    new CountSketchIml()));
            return tridentTopology.build();

        }else if( algorithm == "ISpaceSaving" ){
            tridentTopology
                    .newStream("ISpaceSaving",new IntegerSpout(10))
                    .each(new Fields("item","frequency","type"),
                            new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                    .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                            new SpaceSavingModelUpdater("space-saving",
                                    new SpaceSavingIml(100)));
            return tridentTopology.build();

        }else if( algorithm == "SamplingCounting" ){
            tridentTopology
                    .newStream("SamplingCounting",new IntegerSpout(10))
                    .each(new Fields("item","frequency","type"),
                            new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                    .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                            new FrequencyModelUpdater("sampling-counting",
                                    new StickySamplingCounting(0.1,0.1,0.01)));
            return tridentTopology.build();
        }else if( algorithm == "topK" ){
            tridentTopology
                    .newStream("topK",new IntegerSpout(10))
                    .each(new Fields("item","frequency","type"),
                            new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                    .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                            new TopKModelUpdater("top-K",
                                    new FrequentTopKIml(0.1)));
            return tridentTopology.build();
        }else{
            //return null means your input "algorithm" is not existed
            return null;
        }
    }

}
