package com.hujian.trident.ml.examples.Birch;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.clustering.Birch.BirchClusterIml;
import com.hujian.trident.ml.clustering.Birch.BirchClusterModelUpdater;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomFeaturesForClusteringSpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/25.
 */
public class BirchTopology {

    public static void main( String[] args ){

        TridentTopology tridentTopology = new TridentTopology();
        TridentState tridentState = tridentTopology
                .newStream("birch",new RandomFeaturesForClusteringSpout(4))
                //.parallelismHint(3)
                .each(new Fields("x0","x1","x2","x3"),new InstanceCreator<Boolean>(),
                        new Fields("instance"))
                //.parallelismHint(3)
                .partitionPersist(new MemoryMapState.Factory(),new Fields("instance"),
                        new BirchClusterModelUpdater("birch-model",
                                new BirchClusterIml(4)));
                //.parallelismHint(3);

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("birch-cluster",new Config(),tridentTopology.build());
    }
}
