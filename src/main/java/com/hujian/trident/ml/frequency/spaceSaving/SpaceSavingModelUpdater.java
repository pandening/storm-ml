package com.hujian.trident.ml.frequency.spaceSaving;

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
public class SpaceSavingModelUpdater<T extends Comparable>  extends BaseStateUpdater<MapState<SpaceSavingIml<T>>>{

    private static final long serialVersionUID = 290994862536L;

    private String spaceSavingModelName = null;
    private SpaceSavingIml<T> spaceSavingIml;
    private Random random = null;

    /**
     * the constructor
     * @param spaceSavingModelName
     * @param spaceSavingIml
     */
    public SpaceSavingModelUpdater(String spaceSavingModelName,SpaceSavingIml spaceSavingIml){
        this.spaceSavingModelName = spaceSavingModelName;
        this.spaceSavingIml = spaceSavingIml;
        random = new Random();
    }


    @Override
    public void updateState(MapState<SpaceSavingIml<T>> spaceSavingImlMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old frequency model
        List<SpaceSavingIml<T>> spaceSavingImlList =
                spaceSavingImlMapState.multiGet(Arrays.asList( Arrays.asList( (Object) this.spaceSavingModelName) ));
        SpaceSavingIml<T> spaceSavingIml = null;
        if( spaceSavingImlList != null && spaceSavingImlList.size() != 0 ){
            spaceSavingIml = spaceSavingImlList.get( 0 );
        }
        //init the frequency model
        if( spaceSavingIml == null ){
            spaceSavingIml = this.spaceSavingIml;
        }
        //update the frequency count model
        CountEntry<T> instance = null;
        for( TridentTuple tridentTuple: list ){
            instance = ( CountEntry<T> ) tridentTuple.get( 0 );
        }

        if( spaceSavingIml.getReachTimes() == 0 ){
            instance.setInputDataType( InputDataType.FREQUENCY_STATISTIC );
        }else{
            instance.setInputDataType( new InputDataType[]{InputDataType.FREQUENCY_QUERY,
                    InputDataType.FREQUENCY_STATISTIC}[random.nextInt(2)] );
        }
        //just statistic
        if( instance.getInputDataType() == InputDataType.FREQUENCY_STATISTIC ){
            spaceSavingIml.add( instance.getItem(),instance.getFrequency() );
        }else{
            instance.setFrequency( spaceSavingIml.frequency( instance.getItem() ) );
            System.out.println("query time:["+instance.getItem()+"] = "+instance.getFrequency());
        }

        //saving the frequency model
        spaceSavingImlMapState.multiPut(Arrays.asList(Arrays.asList((Object) this.spaceSavingModelName)),
                Arrays.asList( spaceSavingIml ));
    }

    public String getSpaceSavingModelName() {
        return spaceSavingModelName;
    }

    public void setSpaceSavingModelName(String spaceSavingModelName) {
        this.spaceSavingModelName = spaceSavingModelName;
    }

    public SpaceSavingIml<T> getSpaceSavingIml() {
        return spaceSavingIml;
    }

    public void setSpaceSavingIml(SpaceSavingIml<T> spaceSavingIml) {
        this.spaceSavingIml = spaceSavingIml;
    }
}
