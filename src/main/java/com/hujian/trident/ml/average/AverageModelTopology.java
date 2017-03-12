package com.hujian.trident.ml.average;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.DoubleSpout;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/10.
 */
public class AverageModelTopology {

    /**
     * topology builder
     * @param topologyName
     * @param average
     * @return
     */
     public static StormTopology builder(String topologyName, IAverage average){

         TridentTopology tridentTopology = new TridentTopology();

         tridentTopology.newStream(topologyName,new DoubleSpout(10))
                 .each(new Fields("item","frequency","type"),
                         new CountEntryInstanceCreator<Double>(),new Fields("instance"))
                 .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                         new AverageModelUpdater("average-model-update",average),new Fields("average"))
                 .newValuesStream()
                 .each(new Fields("average"),new ShowAverageFunction(),new Fields("done"))
                 .each(new Fields("done"),new ShowAverageFunction(),new Fields(""));

         return tridentTopology.build();
     }
}
