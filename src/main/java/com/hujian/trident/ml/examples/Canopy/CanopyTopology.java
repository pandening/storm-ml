package com.hujian.trident.ml.examples.Canopy;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.clustering.Canopy.CanopyDriver;
import com.hujian.trident.ml.clustering.Canopy.CanopyModelUpdate;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomFeaturesForClusteringSpout;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/24.
 */
public class CanopyTopology {

    public static  void main( String[] args ){

        TridentTopology tridentTopology = new TridentTopology();
        tridentTopology
                .newStream("canopy",new RandomFeaturesForClusteringSpout(4))
                .parallelismHint(1)
                .each(new Fields("x0","x1","x2","x3"/*,"x4","x5","x6","x7","x8","x9"*/),
                        new InstanceCreator<Integer>(),new Fields("instance"))
                .parallelismHint(1)
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new CanopyModelUpdate("canopy",new CanopyDriver()))
                .parallelismHint(1);

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("canopy-cluster",new Config(),tridentTopology.build());
    }
}
