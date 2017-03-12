package com.hujian.trident.ml.core;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/6.
 */
public class CountEntryInstanceCreator<T> extends BaseFunction{

    private static final long serialVersionUID = 224237602441072L;

    /**
     * create an count instance
     * @param tridentTuple
     * @return
     */
    private CountEntry<T> createInstance(TridentTuple tridentTuple){
        // item frequency dataType
        T item = (T) tridentTuple.get( 0 );
        long frequency = tridentTuple.getLong( 1 );
        InputDataType inputDataType = (InputDataType) tridentTuple.get( 2 );
        CountEntry<T> countEntry = new CountEntry<T>(item,frequency,inputDataType);
        return countEntry;
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if( tridentTuple == null ){
            return;
        }
        CountEntry<T> countEntry = this.createInstance(tridentTuple);
        //emit the instance
        tridentCollector.emit( new Values( countEntry ) );
    }

}
