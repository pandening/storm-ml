package com.hujian.trident.experiment.frequency;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import com.hujian.trident.experiment.frequency.topology.CountSketchTopologyBuilder;
import com.hujian.trident.experiment.frequency.topology.StickySamplingTopologyBuilder;

/**
 * Created by hujian on 2017/3/14.
 */
public class FrequencyExperimentBuilder {

    public static void main(String[] args) {

        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();

        //CountSketch frequency topology
        localCluster.submitTopology( "CountSketch-topology",config,
                CountSketchTopologyBuilder.builder(50000,0.01,0.001));
        //sticky sampling counting
        localCluster.submitTopology("sticky-counting-topology", config,
                StickySamplingTopologyBuilder.builder(10000, 0.1, 0.1, 0.01));

        if (args.length == 1) {
            String topology = args[0];
            switch (topology) {
                case "CountSketch": {
                    try {
                        StormSubmitter.submitTopologyWithProgressBar("CountSketch-Counting", new Config(),
                                CountSketchTopologyBuilder.builder(10000, 0.01, 0.001));
                    } catch (AlreadyAliveException e) {
                        e.printStackTrace();
                    } catch (InvalidTopologyException e) {
                        e.printStackTrace();
                    }
                }
                case "StickySampling": {
                    try {
                        StormSubmitter.submitTopologyWithProgressBar("Sticky-Sampling-Counting", new Config(),
                                StickySamplingTopologyBuilder.builder(10000, 0.1, 0.1, 0.01));
                    } catch (AlreadyAliveException e) {
                        e.printStackTrace();
                    } catch (InvalidTopologyException e) {
                        e.printStackTrace();
                    }
                }
                default: {
                    System.out.print("no this algorithm's topology builder,check please");
                    break;
                }
            }
        }
    }
}
