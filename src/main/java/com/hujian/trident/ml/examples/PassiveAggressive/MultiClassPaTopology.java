package com.hujian.trident.ml.examples.PassiveAggressive;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.classifier.ClassifierModelUpdate;
import com.hujian.trident.ml.classifier.PassiveAggressive.MultiClassPAClassier;
import com.hujian.trident.ml.classifier.PassiveAggressive.PATypeEnum;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomFeaturesForClassifySpout;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/28.
 */
public class MultiClassPaTopology {
    public static void main( String[] args ){
        TridentTopology tridentTopology = new TridentTopology();
         tridentTopology
                .newStream("multi-pa",
                        new RandomFeaturesForClassifySpout(10,3,4,true))
                .each(new Fields("class","c0","c1","c2"),new InstanceCreator<Integer>(true),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                      new ClassifierModelUpdate("m-pa",
                              new MultiClassPAClassier(PATypeEnum.PA,4,0.001)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("multi-pa",new Config(),tridentTopology.build());
    }
}
