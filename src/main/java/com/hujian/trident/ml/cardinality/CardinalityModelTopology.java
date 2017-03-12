package com.hujian.trident.ml.cardinality;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.FrequencySpout;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/10.
 */
public class CardinalityModelTopology {

    /**
     *
     * @param topologyName
     * @param cardinality
     * @return
     */
    public static StormTopology builder(String topologyName,ICardinality cardinality){
        TridentTopology tridentTopology = new TridentTopology();
        tridentTopology
                .newStream(topologyName,new FrequencySpout(10))
                .each(new Fields("item","frequency","type"),
                        new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new CardinalityModelUpdater("cardinality",cardinality));

        return tridentTopology.build();
    }

}
