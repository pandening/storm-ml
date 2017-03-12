package com.hujian.trident.ml.frequency.CountSketch;

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
 * Created by hujian on 2017/3/7.
 */
public class CountSketchModelUpdate<T extends Comparable>  extends BaseStateUpdater<MapState<ICountSketch<T>>> {

    private static final long serialVersionUID = -9087L;

    private String countSketchModelName = null;
    private ICountSketch<T>  countSketch = null;

    private Random random = null;

    /**
     * the constructor
     * @param countSketchModelName
     * @param sketchIml
     */
    public CountSketchModelUpdate( String countSketchModelName,CountSketchIml sketchIml ){
        this.countSketchModelName = countSketchModelName;
        this.countSketch = sketchIml;
        this.random = new Random();
    }


    @Override
    public void updateState(MapState<ICountSketch<T>> iCountSketchMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old frequency model
        List<ICountSketch<T>> countSketchList =
                iCountSketchMapState.multiGet(Arrays.asList( Arrays.asList( (Object) this.countSketchModelName) ));
        ICountSketch<T> countSketch_ = null;
        if( countSketchList != null && countSketchList.size() != 0 ){
            countSketch_ = countSketchList.get( 0 );
        }
        //init the frequency model
        if( countSketch_ == null ){
            countSketch_ = this.countSketch;
        }
        //update the frequency count model
        CountEntry<T> instance = null;
        for( TridentTuple tridentTuple: list ) {
            instance = (CountEntry<T>) tridentTuple.get(0);
            instance.setInputDataType(new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)]);
            //just statistic
            if (instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC) {
                countSketch_.add(instance.getItem(), instance.getFrequency());
            } else {
                instance.setFrequency(countSketch_.frequency(instance.getItem()));
                System.out.println("query time:[" + instance.getItem() + "] = " + instance.getFrequency());
            }
        }

        //saving the frequency model
        iCountSketchMapState.multiPut(Arrays.asList(Arrays.asList((Object) this.countSketchModelName)),
                Arrays.asList( countSketch_ ));
    }

    public String getCountSketchModelName() {
        return countSketchModelName;
    }

    public void setCountSketchModelName(String countSketchModelName) {
        this.countSketchModelName = countSketchModelName;
    }

    public ICountSketch<T> getCountSketch() {
        return countSketch;
    }

    public void setCountSketch(ICountSketch<T> countSketch) {
        this.countSketch = countSketch;
    }
}
