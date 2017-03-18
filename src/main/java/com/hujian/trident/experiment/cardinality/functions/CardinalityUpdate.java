package com.hujian.trident.experiment.cardinality.functions;

import backtype.storm.tuple.Values;
import com.hujian.trident.experiment.core.Instance;
import com.hujian.trident.ml.cardinality.ICardinality;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/15.
 */
public class CardinalityUpdate<T> extends BaseStateUpdater<MapState<ICardinality<T>>> {

    private static final long serialVersionUID = -701990012131119L;

    private String modelName = null;
    private ICardinality<T> cardinality = null;

    /**
     * constructor
     * @param modelName
     * @param cardinality
     */
    public CardinalityUpdate(String modelName,ICardinality<T> cardinality){
        this.modelName = modelName;
        this.cardinality = cardinality;
    }

    @Override
    public void updateState(MapState<ICardinality<T>> iCardinalityMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old model
        List<ICardinality<T>> cardinalityList = iCardinalityMapState.multiGet(Arrays.asList( Arrays.asList( (Object) this.modelName ) ));

        ICardinality<T> cardinality_ = null;

        if( cardinalityList != null && cardinalityList.size() != 0 ){
            cardinality_ = cardinalityList.get( 0 );
        }

        //if first time
        if( cardinality_ == null ){
            cardinality_ = this.cardinality;
        }

        //update the model
        Instance<T> instance = null;

        for( TridentTuple tridentTuple : list ) {
            instance = (Instance<T>) tridentTuple.get(0);
            //do the job!
            if (instance.getDataType() == InputDataType.FREQUENCY_STATISTIC) {
                cardinality_.update(instance.getData());
            } else if (instance.getDataType() == InputDataType.FREQUENCY_QUERY) {
                /**
                 * query the average.
                 * remove the around code if you want to build your own application with
                 * this program.this codes just for testing the code can work in this way.
                 */
                Long cardinality = cardinality_.cardinality();
                //emit to next function
                //id,cardinality
                tridentCollector.emit(new Values(instance.getInstanceId(),cardinality));
            }
        }
        //stored the new model
        iCardinalityMapState.multiPut(Arrays.asList( Arrays.asList( (Object)this.modelName ) ),
                Arrays.asList( cardinality_ ));
    }
}
