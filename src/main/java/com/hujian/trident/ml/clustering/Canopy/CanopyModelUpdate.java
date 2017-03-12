package com.hujian.trident.ml.clustering.Canopy;

import com.hujian.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/2/24.
 */
public class CanopyModelUpdate extends BaseStateUpdater<MapState<Canopy>> {

    private static final long serialVersionUID = 75643560098210021L;

    /**
     * for storing the old canopy model.
     */
    private String canopyModelName = null;

    /**
     * the canopy model.you should init before using it.
     */
    private Canopy canopy = null;

    /**
     * the only constructor.you should offer the canopy model name.
     * and the init can model object.
     * @param canopyModelName
     * @param canopy
     */
    public CanopyModelUpdate( String canopyModelName , Canopy canopy ){
        this.canopyModelName = canopyModelName;
        this.canopy = canopy;
    }


    @Override
    public void updateState(MapState<Canopy> canopyMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        /**
         * get the cluster model from store
         */
        List<Canopy> clusterList = canopyMapState.
                multiGet(Arrays.asList(Arrays.asList( (Object) this.canopyModelName )));
        Canopy canopy_ = null;
        if( clusterList != null && !clusterList.isEmpty() ){
            canopy_ = clusterList.get( 0 );
        }
        if( canopy_ == null ){
            canopy_ = this.canopy;
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
            canopy_.addToCanopies(instance.getFeatures());
        }

        /**
         * stored the cluster model after updating
         */
        canopyMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.canopyModelName )),
                Arrays.asList( canopy_ ));
    }

    public String getCanopyModelName() {
        return canopyModelName;
    }

    public void setCanopyModelName(String canopyModelName) {
        this.canopyModelName = canopyModelName;
    }

    public Canopy getCanopy() {
        return canopy;
    }

    public void setCanopy(Canopy canopy) {
        this.canopy = canopy;
    }
}
