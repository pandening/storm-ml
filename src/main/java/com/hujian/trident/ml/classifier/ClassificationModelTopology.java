package com.hujian.trident.ml.classifier;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.hujian.trident.hybrid.data.AbaloneDataSpout;
import com.hujian.trident.hybrid.functions.ClassifierModelUpdater;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/3/10.
 */
public class ClassificationModelTopology {

    /**
     * the classification model topology builder
     * @param topologyName
     * @param classifier
     * @param filePath
     * @return
     */
    public static StormTopology builder(String topologyName,Classifier classifier,String filePath){
        TridentTopology tridentTopology = new TridentTopology();
        /*
        tridentTopology
                .newStream(topologyName,
                        new DataSourceSpoutForClassification(filePath,10))
                .each(new Fields("label","x0", "x1", "x2","x3","x4","x5", "x6", "x7", "x8",
                                "x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19",
                                "x20", "x21", "x22", "x23", "x24", "x25", "x26", "x27"),
                        new InstanceCreator<Integer>(true),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdate("MultiPa", classifier));
                        */

        tridentTopology
                .newStream(topologyName,new AbaloneDataSpout(filePath,1))
                .each(new Fields("id","label","x1","x2","x3","x4","x5","x6","x7","x8"),
                        new com.hujian.trident.hybrid.data.InstanceCreator<Integer>
                                (true),new Fields("instance"))
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new ClassifierModelUpdater("pa",classifier));


        return tridentTopology.build();
    }
}
