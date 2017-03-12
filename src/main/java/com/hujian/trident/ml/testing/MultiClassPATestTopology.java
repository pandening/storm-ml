package com.hujian.trident.ml.testing;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import com.hujian.trident.ml.classifier.ClassifierModelUpdate;
import com.hujian.trident.ml.classifier.PassiveAggressive.MultiClassPAClassier;
import com.hujian.trident.ml.classifier.PassiveAggressive.PATypeEnum;
import com.hujian.trident.ml.core.InstanceCreator;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/4.
 */
public class MultiClassPATestTopology {

    public static void main( String[] args ){
        TridentTopology tridentTopology = new TridentTopology();
        String filePath = "E:\\IdeaProjects\\data\\train.csv";
        String topologyDesc = "Pa-Classifier-test";
        if( args != null && args.length != 0 ){
            topologyDesc = args[0];
            filePath = args[1];
            System.out.println("get the file path:"+filePath);
            Utils.sleep(1000);
        }

        //train the model.
        TridentState tridentState = tridentTopology
                .newStream("multi-PA-trains",
                        new DataSourceSpoutForClassification(filePath,100))
                .parallelismHint(1)
                .each(new Fields("label","x0", "x1", "x2","x3","x4","x5", "x6", "x7", "x8",
                        "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19",
                        "x20", "x21", "x22", "x23", "x24", "x25", "x26", "x27"),
                        new InstanceCreator<Integer>(true),new Fields("instance"))
                .parallelismHint(1)
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdate("MultiPa",
                                new MultiClassPAClassier(PATypeEnum.PA_II,2,0.001)))
                .parallelismHint(1);

        Config config = new Config();
        config.setMaxSpoutPending(10);
        config.setNumWorkers(1);

        if( args.length == 0 ) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("multi-pac", new Config(), tridentTopology.build());
        }else{
            try {
                StormSubmitter.submitTopologyWithProgressBar(topologyDesc,config,tridentTopology.build());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
        }

    }

}
