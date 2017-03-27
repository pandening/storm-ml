package com.hujian.breastCancer;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.breastCancer.functions.AttributeAverageUpdater;
import com.hujian.breastCancer.functions.BreastClassificationFunction;
import com.hujian.breastCancer.source.BreastInstanceCreator;
import com.hujian.breastCancer.source.BreastSpout;
import storm.trident.TridentTopology;

/**
 * Created by hujian on 2017/3/27.
 */
public class BreastCancerClassificationTopology {

    public static void main(String[] args){

        TridentTopology tridentTopology = new TridentTopology();
        String file = "I:\\breast-cancer.txt";

        tridentTopology
                .newStream("breast",new BreastSpout(file))
                .each(new Fields("x0","x1","x2","x3","x4","x5","x6","x7","x8","x9"),
                        new BreastInstanceCreator(),new Fields("instance"))
                .each(new Fields("instance"),new AttributeAverageUpdater(),
                        new Fields("class","b-model","m-model","features"))
                .each(new Fields("class","b-model","m-model","features"),
                        new BreastClassificationFunction(new int[]{6}),new Fields(""));

        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();

        localCluster.submitTopology("breast-cancel-topology",config,tridentTopology.build());
    }

}
