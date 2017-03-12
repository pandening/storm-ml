package com.hujian.trident.ml;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import com.hujian.trident.hybrid.HybridClassifierTopology;
import com.hujian.trident.ml.average.AverageModelTopology;
import com.hujian.trident.ml.average.EWMAAverage;
import com.hujian.trident.ml.average.MovingAverage;
import com.hujian.trident.ml.cardinality.*;
import com.hujian.trident.ml.classifier.ClassificationModelTopology;
import com.hujian.trident.ml.classifier.PassiveAggressive.MultiClassPAClassier;
import com.hujian.trident.ml.classifier.PassiveAggressive.PATypeEnum;
import com.hujian.trident.ml.frequency.FrequencyModelTopology;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/9.
 */
public class GPAPPBuilder implements Serializable {

    private static final long serialVersionUID = 80870009998L;

    /**
     * args => {topology_name,Algorithm,File[+-]}
     * @param args
     */
    public static void main(String[] args){
        //local or cluster
        if( args.length != 0 && (args.length == 2 || args.length == 3)){
            //cluster model
            String topologyName = args[0];
            String algorithm = args[1];
            String file = null;
            if( args.length == 3 ){
                file = args[2];
            }
            System.out.println("topology name:" + topologyName);
            System.out.println("algorithm:" + algorithm);
            if( file != null )
                System.out.println("file path:" + file);

            //according to the algorithm, get the topology.
            StormTopology topology = null;

            switch (algorithm)
            {
                //average part
                case "MovingAverage":{
                    topology = AverageModelTopology.builder("Moving-Average",
                            new MovingAverage(1000));
                    break;
                }
                case "EWMAAverage":{
                    topology = AverageModelTopology.builder("EWMA-Average",
                            new EWMAAverage(0.1));
                    break;
                }

                //cardinality part
                case "LogLogCardinality":{
                    topology = CardinalityModelTopology.builder("LogLog-cardinality",
                            new LogLogCardinality(12));
                    break;
                }
                case "LinearCountingCarding":{
                    topology = CardinalityModelTopology.builder("LinearCounting-cardinality",
                            new LinearCountingCardinality());
                    break;
                }
                case "HyperLogLogCardinality":{
                    topology = CardinalityModelTopology.builder("HyperLogLogCardinality-cardinality",
                            new HyperLogLogCardinality(16));
                    break;
                }
                case "AdaptiveCountingCardinality":{
                    topology = CardinalityModelTopology.builder("AdaptiveCountingCardinality-cardinality",
                            new AdaptiveCountingCardinality(16));
                    break;
                }

                //classification part
                case "CommitteeClassifier":{
                    break;
                }
                case "PassiveAggressiveClassifier":{
                    break;
                }
                case "MultiClassPAClassifier":{
                    topology = ClassificationModelTopology.builder("PA-classifier",
                            new MultiClassPAClassier(PATypeEnum.PA,2,0.1),file);
                    break;
                }
                case "PerceptronClassifier":{
                    break;
                }
                case "WinnowClassifier":{
                    break;
                }
                case "BalancedWinnowClassifier":{
                    break;
                }
                case "ModifyBalancedWinnowClassifier":{
                    break;
                }

                //clustering part
                case "BirchClustering":{
                    break;
                }
                case "CanopyClustering":{
                    break;
                }
                case "KmeansClustering":{
                    break;
                }

                //counting part
                case "CountSketch":{
                    topology = FrequencyModelTopology.builder("CountSketch");
                    break;
                }
                case "LossyCounting":{
                    break;
                }
                case "StickySamplingCounting":{
                    topology = FrequencyModelTopology.builder("SamplingCounting");
                    break;
                }
                case "SpaceSaving":{
                    topology = FrequencyModelTopology.builder("ISpaceSaving");
                    break;
                }

                //top k (counting) part
                case "SampleTopK":{
                    break;
                }
                case "FrequencyTopK":{
                    topology = FrequencyModelTopology.builder("topK");
                    break;
                }

                //regression part
                case "FtrlRegression":{
                    break;
                }
                case "PARegression":{
                    break;
                }
                case "PerceptronRegression":{
                    break;
                }

                //hybrid classifier
                case "HybridClassifier":{
                    topology = HybridClassifierTopology.builder("E:\\IdeaProjects\\data\\abalone.txt");
                    break;
                }

                default:{
                    break;
                }
            }
            //no this topology ready
            if( topology == null ){
                System.out.println("no this topology,check your input");
                return;
            }
            //run in the cluster
            Config config = new Config();
            config.setNumWorkers(2);

            try {
                StormSubmitter.submitTopologyWithProgressBar(topologyName,config,topology);
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
            //end of the running...

        }else{
            //local model
            //average model
            StormTopology topology = null;

            //cardinality model
            topology = CardinalityModelTopology.builder("LogLog-cardinality",new LogLogCardinality(10));

            //top k and frequency
            topology = FrequencyModelTopology.builder("topK");
            topology = FrequencyModelTopology.builder("CountSketch");
            topology = FrequencyModelTopology.builder("ISpaceSaving");
            topology = FrequencyModelTopology.builder("SamplingCounting");
            topology = AverageModelTopology.builder("Moving-Average",
                    new MovingAverage(100));
            //classification model
            topology = ClassificationModelTopology.builder("PA-classifier",
                    new MultiClassPAClassier(PATypeEnum.PA_II,3,0.1),"E:\\IdeaProjects\\data\\abalone.txt");

            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("topology",new Config(),topology);

        }
    }

}
