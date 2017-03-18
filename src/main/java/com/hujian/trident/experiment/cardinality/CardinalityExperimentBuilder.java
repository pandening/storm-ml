package com.hujian.trident.experiment.cardinality;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.utils.Utils;
import com.hujian.trident.experiment.cardinality.topology.AdaptiveCountingCardinalityTopologyBuilder;
import com.hujian.trident.experiment.cardinality.topology.HyperLogLogCardinalityTopology;
import com.hujian.trident.experiment.cardinality.topology.LinearCountingTopologyBuilder;
import com.hujian.trident.experiment.cardinality.topology.LogLogCardinalityTopologyBuilder;

/**
 * Created by hujian on 2017/3/15.
 */
public class CardinalityExperimentBuilder {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();

        //adaptive counting cardinality
        //localCluster.submitTopology("Adaptive-Counting-Topology",config,
        //        new AdaptiveCountingCardinalityTopologyBuilder().builder(5000,25));

        //hyper-LogLog cardinality
        //localCluster.submitTopology("Hyper-LogLog-Topology",config,
         //       new HyperLogLogCardinalityTopology().builder(5000,16));

        //LogLog cardinality
        //localCluster.submitTopology("LogLog-Topology",config,
         //       LogLogCardinalityTopologyBuilder.builder(2000,9));


        //LinearCounting Cardinality
        localCluster.submitTopology( "Linear-Counting-Topology",config,
                LinearCountingTopologyBuilder.builder(5000,1024*1024));

        //cluster model
        if( args.length > 0 ){
            //get the cardinality name
            String topology = args[0];
            //get the batch size
            int batchSize = Integer.parseInt( args[1] );
            switch (topology){
                case "linearCounting":{
                    //get the bitmap size
                    int size = Integer.parseInt(args[2]);
                    StormSubmitter.submitTopologyWithProgressBar("LinearCounting-Topology",new Config(),
                            LinearCountingTopologyBuilder.builder(batchSize,size));
                }
                case "LogLog":{
                    //get the k
                    int k = Integer.parseInt(args[2]);
                    StormSubmitter.submitTopologyWithProgressBar("LogLog-Topology",new Config(),
                            LogLogCardinalityTopologyBuilder.builder(batchSize,k));
                }
                case  "HyperLogLog":{
                    //get the bucket bit size
                    int size = Integer.parseInt( args[2] );
                    StormSubmitter.submitTopologyWithProgressBar("Hyper-LogLog-Topology",new Config(),
                            HyperLogLogCardinalityTopology.builder(batchSize,size));
                }
                case "AdaptiveCounting":{
                    //get the k
                    int k = Integer.parseInt( args[2] );
                    StormSubmitter.submitTopologyWithProgressBar("Adaptive-Counting-Topology",new Config(),
                            AdaptiveCountingCardinalityTopologyBuilder.builder(batchSize,k));
                }
                default:{
                    System.out.println("no topology named:"+topology);
                    break;
                }
            }
        }
    }
}
