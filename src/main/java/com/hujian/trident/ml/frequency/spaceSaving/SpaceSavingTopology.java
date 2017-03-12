package com.hujian.trident.ml.frequency.spaceSaving;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.CountEntryInstanceCreator;
import com.hujian.trident.ml.examples.data.FrequencySpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/7.
 */
public class SpaceSavingTopology {

    public static void main(String[] args){

        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("spaceSaving",new FrequencySpout(10))
                .each(new Fields("item","frequency","type"),new CountEntryInstanceCreator<Integer>(),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new SpaceSavingModelUpdater("ss",
                                new SpaceSavingIml<Integer>(100)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("SS-topology",new Config(),tridentTopology.build());
    }

}
