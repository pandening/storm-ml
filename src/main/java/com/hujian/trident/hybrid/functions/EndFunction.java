package com.hujian.trident.hybrid.functions;

import com.hujian.trident.hybrid.classifier.IClassifierFactory;
import com.hujian.trident.hybrid.classifier.SamplesClassifierFactory;
import com.hujian.trident.hybrid.data.Instance;
import com.hujian.trident.hybrid.store.IStore;
import com.hujian.trident.hybrid.store.SampleStored;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.List;

/**
 * Created by hujian on 2017/3/12.
 */
public class EndFunction extends BaseFunction {

    private static final long serialVersionUID = 237602111L;

    /**
     * the store
     */
    private IStore<String,List<Integer>> store = null;
    private IClassifierFactory<Integer> classifierFactory = null;

    /**
     * the constructor
     */
    public EndFunction(){
        store = SampleStored.getInstance();
        classifierFactory = SamplesClassifierFactory.getInstance();
    }

    /**
     * just show the instance
     * @param tridentTuple
     * @param tridentCollector
     */
    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        Instance<Integer> instance = (Instance<Integer>) tridentTuple.get( 0 );
        System.out.println("[the end] instance id=>"+instance.getInstanceId());
        //show the classify result
        System.out.println("right=>"+this.classifierFactory.rightCount()+"\nerror:"+
        this.classifierFactory.errorCount());

        /**
         * for some reason,you should remove this useless entry
         */
        this.store.mapStates().remove( "classification_"+instance.getInstanceId() );

        //clear done map
        this.store.doneMap().clear();

        System.out.println("-----classifier weight vector-----");
        //weight
        for( double d: this.classifierFactory.getWeight() ){
            System.out.print(d+" ");
        }
        System.out.print("\n");
    }
}
