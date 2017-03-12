package com.hujian.trident.ml.examples.Perceptron;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.classifier.ClassifierModelUpdate;
import com.hujian.trident.ml.classifier.Perceptron.PerceptronClassifier;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomBooleanDataForClassifySpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/27.
 */
public class PerceptronTopology {

    public static void main(String[] args){

        TridentTopology tridentTopology = new TridentTopology();
        //train the classifier model
        TridentState tridentState = tridentTopology
                .newStream("perceptron",
                        new RandomBooleanDataForClassifySpout(100,4,true))
                .each(new Fields("label","x0","x1","x2","x3"),
                        new InstanceCreator<Boolean>(true),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdate("perceptron",
                                new PerceptronClassifier()));

        //local cluster test.
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("perceptron-topology",new Config(), tridentTopology.build());
    }
}
