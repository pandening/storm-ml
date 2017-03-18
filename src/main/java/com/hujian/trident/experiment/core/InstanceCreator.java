package com.hujian.trident.experiment.core;

import backtype.storm.tuple.Values;
import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import com.hujian.trident.ml.core.InputDataType;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/14.
 */
public class InstanceCreator<L> extends BaseFunction {

    private static final long serialVersionUID = 237602441072120639L;

    /**
     * 0 means map,1 means set,-1 means do not get any store
     */
    private Integer storeType = 0;

    /**
     * the store
     */
    //private IStore<L,Long> statisticStore = null;

    public InstanceCreator(){
        //this.statisticStore = SampleStored.getInstance();
    }

    /**
     * constructor with store type
     * @param storeType
     */
    public InstanceCreator(int storeType){
        //this.statisticStore = SampleStored.getInstance();
        this.storeType = storeType;
    }

    /**
     * create an instance from trident tuple.
     * @param tridentTuple
     * @return
     */
    protected Instance<L> createInstance(TridentTuple tridentTuple){
        Instance<L> instance = null;
        //get the id
        Long instanceID = tridentTuple.getLong( 0 );
        //get the data
        L data = (L) tridentTuple.get( 1 );
        /*
        if( this.storeType == 0 ){
            //update the statistic(correct)
            if( this.statisticStore.mapStates().get( data ) == null ){
                this.statisticStore.mapStates().put( data,1L );
            }else{
                this.statisticStore.mapStates().put( data,this.statisticStore.get( data ) + 1L );
            }
        }else if(this.storeType == 1){
            this.statisticStore.getSet().add( data );
        }*/

        //set the instance
        if( instanceID % 10000 == 0 ){
            instance = new Instance<>(data,instanceID,InputDataType.FREQUENCY_QUERY);
        }else{
            instance = new Instance<>(data,instanceID,InputDataType.FREQUENCY_STATISTIC);
        }

        //create an instance
        return instance;
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if( tridentTuple == null ){
            return;
        }
        Instance<L> instance = this.createInstance( tridentTuple );

        Values values = new Values( instance );

        tridentCollector.emit( values);
    }
}
