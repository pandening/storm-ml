package com.hujian.trident.ml.clustering.Birch;

import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/2/25.
 */
public class BirchClusterModelUpdater extends BaseStateUpdater<MapState<BirchCluster>> {

    private static final long serialVersionUID = -620643560901768921L;

    private String birchClusterModelName = null;

    private BirchCluster birchCluster = null;

    public BirchClusterModelUpdater(String birchClusterModelName,BirchCluster birchCluster){
        this.birchClusterModelName = birchClusterModelName;
        this.birchCluster = birchCluster;
    }

    @Override
    public void updateState(MapState<BirchCluster> birchClusterMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        /**
         * get the cluster model from store
         */
        List<BirchCluster> birchClusterList = birchClusterMapState.
                multiGet(Arrays.asList(Arrays.asList( (Object) this.birchClusterModelName )));
        BirchCluster birchCluster_ = null;
        if( birchClusterList != null && !birchClusterList.isEmpty() ){
            birchCluster_ = birchClusterList.get( 0 );
        }
        /**
         * the cluster model is null means we need to set up the cluster model.
         * the old cluster model may be cleared.
         */
        if( birchCluster_ == null ){
            birchCluster_ = this.birchCluster;
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
            birchCluster_.update( instance.getFeatures() );
        }

        /**
         * stored the cluster model after updating
         */
        birchClusterMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.birchClusterModelName )),
                Arrays.asList( birchCluster_ ));
    }

    public String getBirchClusterModelName() {
        return birchClusterModelName;
    }

    public void setBirchClusterModelName(String birchClusterModelName) {
        this.birchClusterModelName = birchClusterModelName;
    }

    public BirchCluster getBirchCluster() {
        return birchCluster;
    }

    public void setBirchCluster(BirchCluster birchCluster) {
        this.birchCluster = birchCluster;
    }

    @Override
    public String toString(){
        return "Class:" + BirchClusterModelUpdater.class.getName() + " \t "
                + "ClusterModelName:" + this.birchClusterModelName + " \t "
                + "ClusterModel:" + this.birchCluster;
    }
}
