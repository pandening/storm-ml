package com.hujian.trident.experiment.frequency.functions;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.hujian.trident.experiment.core.Instance;
import com.hujian.trident.ml.core.InputDataType;
import com.hujian.trident.ml.frequency.IRichFrequency;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/14.
 */
public class StickySamplingCountingUpdate<T>  extends BaseStateUpdater<MapState<IRichFrequency<T>>> {

    private static final long serialVersionUID = 14230240729L;

    private String frequencyModelName = null;
    private IRichFrequency<T> richFrequency = null;

    /**
     * constructor
     * @param frequencyModelName
     * @param richFrequency
     */
    public StickySamplingCountingUpdate(String frequencyModelName,IRichFrequency<T> richFrequency){
        this.frequencyModelName = frequencyModelName;
        this.richFrequency = richFrequency;
    }

    @Override
    public void updateState(MapState<IRichFrequency<T>> iRichFrequencyMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old model,if not existed, just assign the init model
        List<IRichFrequency<T>> richFrequencyList = new ArrayList<IRichFrequency<T>>();
        richFrequencyList = iRichFrequencyMapState.multiGet(Arrays.asList(
                Arrays.asList( (Object) this.frequencyModelName ) ));
        IRichFrequency<T> richFrequency_ = null;
        //get a frequency model instance
        if( richFrequencyList != null && richFrequencyList.size() != 0 ){
            richFrequency_ = richFrequencyList.get( 0 );
        }

        //if this is the first call this update , just init the frequency model
        if( richFrequency_ == null ){
            richFrequency_ = this.richFrequency;
        }

        //call the update function
        Instance<T> instance = null;
        for(TridentTuple tridentTuple : list){
            instance = (Instance<T>) tridentTuple.get( 0 );
            //do the job!
            if( instance.getDataType() == InputDataType.FREQUENCY_STATISTIC ){
                //just update the model
                richFrequency_.add(instance.getData(),1);
            }else if( instance.getDataType() == InputDataType.FREQUENCY_QUERY ){
                Long frequency = richFrequency_.estimate( instance.getData() );
                frequency = frequency == 0 ? 0L :frequency;


                if( frequency == null ){
                    System.out.println("fuck1");
                }
                if( instance == null ){
                    System.out.println("fuck2");
                }

                if( tridentCollector == null ){
                    System.out.println("fuck3");
                }

                //or,you can query topK here,you just need use another "only" data type
                //then judge here,and get the result,and do something you like
                //
                //emit the data to next function
                //id,data,frequency
                tridentCollector.emit(new Values(instance.getInstanceId(),instance.getData(),frequency));
            }
        }
        //save the frequency model
        iRichFrequencyMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.frequencyModelName ) ),
                Arrays.asList( richFrequency_ ));
    }
}
