package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.core.CountEntry;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/10.
 */
public class CardinalityModelUpdater<T> extends BaseStateUpdater<MapState<ICardinality<T>>> {

    private static final long serialVersionUID = -701990012131119L;

    private String modelName = null;
    private ICardinality<T> cardinality = null;
    private Random random = null;

    /**
     * constructor
     * @param modelName
     * @param cardinality
     */
    public CardinalityModelUpdater(String modelName,ICardinality<T> cardinality){
        this.modelName = modelName;
        this.cardinality = cardinality;
        this.random = new Random();
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
        CountEntry<T> instance = null;

        for( TridentTuple tridentTuple : list ) {
            instance = (CountEntry<T>) tridentTuple.get(0);

            //re-set the data's type.do random work ..|..
            instance.setInputDataType(new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)]);
            //do the job!
            if (instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC) {
                cardinality_.update(instance.getItem());
            } else if (instance.getInputDataType() == InputDataType.FREQUENCY_QUERY) {
                /**
                 * query the average.
                 * remove the around code if you want to build your own application with
                 * this program.this codes just for testing the code can work in this way.
                 */
                System.out.println("<Cardinality query>:" + cardinality_.cardinality());
            }
        }
        //stored the new model
        iCardinalityMapState.multiPut(Arrays.asList( Arrays.asList( (Object)this.modelName ) ),
                Arrays.asList( cardinality_ ));

    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public ICardinality<T> getCardinality() {
        return cardinality;
    }

    public void setCardinality(ICardinality<T> cardinality) {
        this.cardinality = cardinality;
    }
}
