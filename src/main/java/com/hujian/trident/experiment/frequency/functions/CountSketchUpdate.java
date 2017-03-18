package com.hujian.trident.experiment.frequency.functions;

import backtype.storm.tuple.Values;
import com.hujian.trident.experiment.core.Instance;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.frequency.CountSketch.ICountSketch;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/14.
 */
public class CountSketchUpdate<T extends Comparable> extends BaseStateUpdater<MapState<ICountSketch<T>>> {

    private static final long serialVersionUID = -9084447L;

    private String countSketchModelName = null;
    private ICountSketch<T>  countSketch = null;

    /**
     * constructor
     * @param countSketchModelName
     * @param countSketch
     */
    public CountSketchUpdate(String countSketchModelName,ICountSketch<T> countSketch){
        this.countSketchModelName = countSketchModelName;
        this.countSketch = countSketch;
    }

    @Override
    public void updateState(MapState<ICountSketch<T>> iCountSketchMapState, List<TridentTuple> list,
                            TridentCollector tridentCollector) {
        //get old model
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
        Instance<T> instance = null;
        for( TridentTuple tridentTuple: list ) {
            instance = (Instance<T>) tridentTuple.get(0);
            //just statistic
            if (instance.getDataType() == InputDataType.FREQUENCY_STATISTIC) {
                //update the frequency model
                countSketch_.add(instance.getData(), 1L);
            } else {
                Long frequency = countSketch_.frequency( instance.getData() );
                //emit the result to next function
                //instanceID,data,Frequency
                tridentCollector.emit( new Values(instance.getInstanceId(),instance.getData(),
                        frequency));
            }
        }
        //saving the frequency model
        iCountSketchMapState.multiPut(Arrays.asList(Arrays.asList((Object) this.countSketchModelName)),
                Arrays.asList( countSketch_ ));
    }
}
