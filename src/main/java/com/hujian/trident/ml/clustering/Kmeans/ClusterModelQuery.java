package com.hujian.trident.ml.clustering.Kmeans;

import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/2/24.
 */
public class ClusterModelQuery  extends BaseQueryFunction<MapState<Cluster>,Integer>{

    private static final long serialVersionUID = -992645214534251498L;

    /**
     * the cluster model name,this is the key,we can get the
     * old/existed cluster model from store by this key.
     */
    private String clusterModelName;

    public ClusterModelQuery( String clusterModelName ){
        this.clusterModelName = clusterModelName;
    }

    @Override
    public List<Integer> batchRetrieve(MapState<Cluster> clusterMapState, List<TridentTuple> list) {
       //the result.
        List<Integer> clusterIndexList = new ArrayList<Integer>();
        List<double[]> featuresList = new ArrayList<double[]>();

        //first of all,we need to get the cluster model.
        List<Cluster> clusterList = clusterMapState
                .multiGet(Arrays.asList( Arrays.asList( (Object) this.clusterModelName ) ));
        if( clusterList != null && !clusterList.isEmpty() ){
            Cluster cluster = clusterList.get( 0 );
            Integer clusterIndex ;
            Instance<?> instance;
            for (TridentTuple tridentTuple: list) {
                instance = (Instance<?>) tridentTuple.get( 0 );
                clusterIndex = cluster.classify( instance.getFeatures() );
                featuresList.add(instance.getFeatures());
                clusterIndexList.add( clusterIndex );
            }
        }

        return clusterIndexList;
    }

    @Override
    public void execute(TridentTuple tridentTuple, Integer integer, TridentCollector tridentCollector) {
        tridentCollector.emit( new Values( integer ));
    }

    public String getClusterModelName() {
        return clusterModelName;
    }

    public void setClusterModelName(String clusterModelName) {
        this.clusterModelName = clusterModelName;
    }
}
