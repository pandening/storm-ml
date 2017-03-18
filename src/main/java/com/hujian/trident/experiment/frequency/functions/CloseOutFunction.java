package com.hujian.trident.experiment.frequency.functions;

import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/14.
 */
public class CloseOutFunction<T> extends BaseFunction {

    /**
     * the storage
     */
    private IStore<T,Long> statisticStore = null;
    private double sum = 0.0;
    private Long reachCount = 0L;

    public CloseOutFunction(){
        //get the store
        this.statisticStore = SampleStored.getInstance();
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

        if( tridentTuple == null ){
            return;
        }

        //get the id,data,frequency
        Long instanceID = tridentTuple.getLong( 0 );

        T data = (T)tridentTuple.get( 1 );
        Long frequency = tridentTuple.getLong( 2 );
        Long correct = this.statisticStore.get( data );
        Long deviation = Math.abs( frequency - correct );
        double x = (deviation / (correct * 1.0)) * 100.0;

        if( sum == 0.0 ){
            reachCount = 1L;
            sum += x;
        }else{
            if( x <= sum / reachCount ){
                reachCount ++;
                sum += x;
                System.out.println("["+instanceID+"] @"+data+
                        " ("+frequency+"/"+correct+") ["+deviation+"]"+ x + "%");
            }
        }
    }
}
