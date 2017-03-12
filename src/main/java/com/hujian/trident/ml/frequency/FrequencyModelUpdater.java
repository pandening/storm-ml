package com.hujian.trident.ml.frequency;

import backtype.storm.utils.Utils;
import com.hujian.trident.ml.core.*;
import com.hujian.trident.ml.core.CountEntry;
import com.hujian.trident.ml.frequency.lossyCounting.LossyCounting;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class FrequencyModelUpdater<T>  extends BaseStateUpdater<MapState<IRichFrequency<T>>>{

    private static final long serialVersionUID = 14230240729L;

    private String frequencyModelName = null;
    private IRichFrequency<T> richFrequency = null;
    private Random random = null;

    /**
     * the constructor
     * @param frequencyModelName
     * @param iRichFrequency
     */
    public FrequencyModelUpdater( String frequencyModelName , IRichFrequency<T> iRichFrequency ){
        this.frequencyModelName = frequencyModelName;
        this.richFrequency  = iRichFrequency;
        this.random = new Random();
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
        CountEntry<T> instance = null;
        for(TridentTuple tridentTuple : list){
            instance = (CountEntry<T>) tridentTuple.get( 0 );
            //re-set the data's type.do random work ..|..
            instance.setInputDataType( new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)] );
            //do the job!
            if( instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC ){

                richFrequency_.add(instance.getItem(),1);

            }else if( instance.getInputDataType() == InputDataType.FREQUENCY_QUERY ){
                instance.setFrequency( richFrequency_.estimate( instance.getItem() ) );
                System.out.println("<Frequency query>:"+ instance.item + " -> "
                        + richFrequency_.estimate(instance.getItem()));
                //or,you can query topK here,you just need use another "only" data type
                //then judge here,and get the result,and do something you like
                //
            }
        }
        //save the frequency model
        iRichFrequencyMapState.multiPut(Arrays.asList( Arrays.asList( (Object) this.frequencyModelName ) ),
                Arrays.asList( richFrequency_ ));
    }

    public String getFrequencyModelName() {
        return frequencyModelName;
    }

    public void setFrequencyModelName(String frequencyModelName) {
        this.frequencyModelName = frequencyModelName;
    }

    public IRichFrequency<T> getRichFrequency() {
        return richFrequency;
    }

    public void setRichFrequency(IRichFrequency<T> richFrequency) {
        this.richFrequency = richFrequency;
    }
}
