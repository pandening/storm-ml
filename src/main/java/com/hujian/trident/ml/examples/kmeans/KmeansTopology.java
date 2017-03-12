package com.hujian.trident.ml.examples.kmeans;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import com.hujian.trident.ml.clustering.Kmeans.ClusterModelUpdater;
import com.hujian.trident.ml.clustering.Kmeans.Kmeans;
import com.hujian.trident.ml.core.InstanceCreator;
import com.hujian.trident.ml.examples.data.RandomFeaturesForClusteringSpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.MemoryMapState;

/**
 * Created by hujian on 2017/2/24.
 */
public class KmeansTopology {

    public static void main( String[] args ){

        TridentTopology tridentTopology = new TridentTopology();

        /**
         * train the cluster model
         */
        TridentState kmeansState = tridentTopology
                .newStream("samples",new RandomFeaturesForClusteringSpout(3,4))
                .parallelismHint(1)
                .each(new Fields("x0","x1","x2"),
                        new InstanceCreator<Integer>(),new Fields("instance"))
                .parallelismHint(1)
                .partitionPersist(new MemoryMapState.Factory(), new Fields("instance"),
                        new ClusterModelUpdater("kmeans",new Kmeans(4)))
                .parallelismHint(1);

        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("kmeans",new Config(),tridentTopology.build());
    }
}
