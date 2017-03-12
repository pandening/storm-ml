package com.hujian.trident.ml.frequency.lossyCounting;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.FrequencySpout;
import com.hujian.trident.ml.examples.data.IntegerSpout;
import com.hujian.trident.ml.frequency.FrequencyModelUpdater;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/7.
 */
public class LossyCountingTopology  {

    public static void main( String[] args ){
        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("lossy-count",new IntegerSpout(10))
                .each(new Fields("item","frequency","type"),
                        new CountEntryInstanceCreator<Integer>(),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new FrequencyModelUpdater("lossy-counting",
                                new LossyCounting<Integer>(0.1)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("lossy-counting-topology",new Config(),tridentTopology.build());

    }

}
