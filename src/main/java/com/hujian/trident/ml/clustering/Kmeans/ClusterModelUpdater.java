package com.hujian.trident.ml.clustering.Kmeans;

import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/2/23.
 */
public class ClusterModelUpdater extends BaseStateUpdater<MapState<Cluster>>{

    private static final long serialVersionUID = 675643560098768921L;

    /**
     * the cluster model name,for get the old/trained cluster model from
     * memory or other place.
     */
    private String clusterModelName = null;

    /**
     * the cluster model.
     */
    private Cluster cluster = null;

    /**
     *the only constructor,offers the cluster model and the cluster
     * model's name.
     *
     * @param clusterModelName
     * @param cluster
     */
    public ClusterModelUpdater( String clusterModelName, Cluster cluster ){
        this.clusterModelName = clusterModelName;
        this.cluster = cluster;
    }


    @Override
    public void updateState(MapState<Cluster> clusterMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        /**
         * get the cluster model from store
         */
        List<Cluster> clusterList = clusterMapState.
                multiGet(Arrays.asList(Arrays.asList( (Object) this.clusterModelName )));
        Cluster cluster_ = null;
        if( clusterList != null && !clusterList.isEmpty() ){
            cluster_ = clusterList.get( 0 );
        }
        /**
         * the cluster model is null means we need to set up the cluster model.
         * the old cluster model may be cleared.
         */
        if( cluster_ == null ){
            cluster_ = this.cluster;
        }

        /**
         * so,the library's input data based on the object->Instance
         * you should transfer your object to the Instance.then run the ml
         */
        Instance<?> instance;

        /**
         * update the cluster model by the new features
         */
        for (TridentTuple tridentTuple : list) {
            instance = (Instance<?>) tridentTuple.get( 0 );
            cluster_.update( instance.getFeatures() );
        }

        /**
         * stored the cluster model after updating
         */
        clusterMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.clusterModelName )),
                Arrays.asList( cluster_ ));
    }

    public String getClusterModelName() {
        return clusterModelName;
    }

    public void setClusterModelName(String clusterModelName) {
        this.clusterModelName = clusterModelName;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString(){
        return "Class:" + ClusterModelUpdater.class.getName() + " \t "
                + "ClusterModelName:" + this.clusterModelName + " \t "
                + "ClusterModel:" + this.cluster;
    }
}
