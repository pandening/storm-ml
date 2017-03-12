package com.hujian.trident.ml.examples.Regression;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomRegressionSpout;
import com.hujian.trident.ml.regression.Perceptron.PerceptronRegression;
import com.hujian.trident.ml.regression.RegressionModelUpdate;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/1.
 */
public class PerceptronRegressionTopology {

    public static  void main(String[] args){
        TridentTopology tridentTopology = new TridentTopology() ;
        TridentState tridentState = tridentTopology
                .newStream("perceptron-regression",new RandomRegressionSpout(10,3))
                .each(new Fields("Label","i0","i1","i2"),
                        new InstanceCreator<Double>(true),new Fields( "instance" ))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new RegressionModelUpdate("perceptron-regression",
                                new PerceptronRegression(0.01,0.3)));

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("perceptron-regression-topology",new Config(),tridentTopology.build());
    }

}
