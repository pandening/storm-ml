package com.hujian.trident.ml.frequency.SamplingCounting;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.FrequencySpout;
import com.hujian.trident.ml.frequency.FrequencyModelUpdater;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/7.
 */
public class StickySamplingCountingTopology {

    public static void main(String[] args){

        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("sticky-sampling-counting",new FrequencySpout(10))
                .each(new Fields("item","frequency","type"),new CountEntryInstanceCreator<Integer>(),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new FrequencyModelUpdater("stick-sampling-counting",
                                new StickySamplingCounting(0.1,0.1,0.01)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("sticky-counting",new Config(),tridentTopology.build());

    }

}
