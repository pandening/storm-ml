package com.hujian.trident.ml.frequency.TopKCounting;

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
public class TopKModelUpdater<T> extends BaseStateUpdater<MapState<ITopK<T>>> {

    private static final long serialVersionUID = 14230240729L;

    private String modelName;
    private ITopK<T> topK;
    private Random random;

    /**
     *
     * @param modelName
     * @param TopK
     */
    public TopKModelUpdater(String modelName,ITopK<T> TopK){
        this.modelName = modelName;
        this.topK = TopK;
        this.random = new Random();
    }

    @Override
    public void updateState(MapState<ITopK<T>> iTopKMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old model,if not existed, just assign the init model
        List<ITopK<T>> topKList = null;
        topKList = iTopKMapState.multiGet(Arrays.asList(
                Arrays.asList( (Object) this.modelName) ));
        ITopK<T> topK_ = null;
        //get a frequency model instance
        if( topKList != null && topKList.size() != 0 ){
            topK_ = topKList.get( 0 );
        }

        //if this is the first call this update , just init the frequency model
        if( topK_ == null ){
            topK_ = this.topK;
        }

        //call the update function
        CountEntry<T> instance = null;
        for(TridentTuple tridentTuple : list){
            instance = (CountEntry<T>) tridentTuple.get( 0 );
            //re-set the data's type.do random work ..|..
            instance.setInputDataType( new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)] );
            //do the job!
            if( instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC ){
                topK_.add( instance.getItem(), 1L);
            }else if( instance.getInputDataType() == InputDataType.FREQUENCY_QUERY ){
               //top k
                System.out.println("<Top K Query>:");
                for(com.hujian.trident.ml.frequency.CountEntry<T> countEntry : topK_.topK(10)){
                    System.out.println(countEntry.getItem());
                }

            }
        }
        //save the frequency model
        iTopKMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.modelName ) ),
                Arrays.asList( topK_ ));
    }
}
