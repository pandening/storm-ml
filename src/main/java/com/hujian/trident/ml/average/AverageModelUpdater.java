package com.hujian.trident.ml.average;

import backtype.storm.tuple.Values;
import com.hujian.trident.ml.core.CountEntry;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/9.
 */
public class AverageModelUpdater extends BaseStateUpdater<MapState<IAverage>> {

    private static final long serialVersionUID = -701990012131009L;

    /**
     * the model name.
     */
    private String averageModelName;
    private IAverage average = null;

    private Random random = null;

    /**
     * constructor
     * @param averageModelName
     * @param average
     */
    public AverageModelUpdater(String averageModelName , IAverage average){
        this.averageModelName = averageModelName;
        this.average = average;
        this.random = new Random();
    }

    /**
     * the average model can use CountEntry data structure as the spout
     * @param iAverageMapState
     * @param list
     * @param tridentCollector
     */
    @Override
    public void updateState(MapState<IAverage> iAverageMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old model
        List<IAverage> averageList = new ArrayList<IAverage>();
        averageList = iAverageMapState.multiGet(Arrays.asList( Arrays.asList( (Object)this.averageModelName ) ));

        IAverage average_ = null;
        if( averageList != null && averageList.size() != 0 ){
            average_ = averageList.get( 0 );
        }

        //if this is the first time
        if( average_ == null ){
            average_ = this.average;
        }

        //update the model
        CountEntry<Double> instance = null;
        for( TridentTuple tridentTuple : list ){
            instance = (CountEntry<Double>) tridentTuple.get( 0 );
            //re-set the data's type.do random work ..|..
            instance.setInputDataType( new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)] );
            //do the job!
            if( instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC ){
                average_.update( instance.getItem());
            }else if( instance.getInputDataType() == InputDataType.FREQUENCY_QUERY ){
                /**
                 * query the average.
                 * remove the around code if you want to build your own application with
                 * this program.this codes just for testing the code can work in this way.
                 */
                System.out.println("<Average query>:" + average_.average());
                //emit the average value
                tridentCollector.emit(new Values(average_.average()));
            }
        }

        //save the new model
        iAverageMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.averageModelName ) ),
                Arrays.asList( average_ ));

    }

    public String getAverageModelName() {
        return averageModelName;
    }

    public void setAverageModelName(String averageModelName) {
        this.averageModelName = averageModelName;
    }

    public IAverage getAverage() {
        return average;
    }

    public void setAverage(IAverage average) {
        this.average = average;
    }
}
