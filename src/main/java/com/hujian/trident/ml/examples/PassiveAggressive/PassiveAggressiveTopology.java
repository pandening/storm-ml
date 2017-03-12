package com.hujian.trident.ml.examples.PassiveAggressive;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.classifier.ClassifierModelUpdate;
import com.hujian.trident.ml.classifier.PassiveAggressive.PassiveAggressiveClassifier;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomBooleanDataForClassifySpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/27.
 */
public class PassiveAggressiveTopology {
    public static void main( String[] args ){
        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("PA",new RandomBooleanDataForClassifySpout(10,3,true))
                .each(new Fields("label","x0","x1","x2"),
                        new InstanceCreator<Boolean>(true),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdate("pa",
                                new PassiveAggressiveClassifier()));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("balanced-winnow",new Config(),tridentTopology.build());
    }
}
