package com.hujian.trident.ml.examples.Winnow;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.classifier.ClassifierModelUpdate;
import com.hujian.trident.ml.classifier.Committee.CommitteeClassifier;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomFeaturesForClassifySpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/1.
 */
public class CommitteeTopology {

    public static  void main( String[] args ){

        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("committee",new RandomFeaturesForClassifySpout(10,4,3,true))
                .each(new Fields("class","c0","c1","c2"),new InstanceCreator<Integer>(true),
                        new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdate("committee-classifier",
                                new CommitteeClassifier(2.0,3)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("committee-cluster",new Config(),tridentTopology.build());
    }

}
