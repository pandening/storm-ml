package com.hujian.trident.experiment.speed;

import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.hujian.trident.experiment.core.Instance;
import com.hujian.trident.ml.cardinality.ICardinality;
import com.hujian.trident.ml.core.InputDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hujian on 2017/3/15.
 */
public class LogLogModelUpdateForSpeedTest<T> extends BaseStateUpdater<MapState<ICardinality<T>>> {
    private static final long serialVersionUID = -701990012131119L;

    private String modelName = null;
    private ICardinality<T> cardinality = null;

    private  Long start = 0L;
    private  Long end = 0L;

    private StringBuilder sb = null;

    /**
     * constructor
     * @param modelName
     * @param cardinality
     */
    public LogLogModelUpdateForSpeedTest(String modelName,ICardinality<T> cardinality){
        this.modelName = modelName;
        this.cardinality = cardinality;
        this.start = System.currentTimeMillis();

        this.sb = new StringBuilder();
    }

    @Override
    public void updateState(MapState<ICardinality<T>> iCardinalityMapState, List<TridentTuple> list, TridentCollector tridentCollector) {
        //get the old model
        //List<ICardinality<T>> cardinalityList = iCardinalityMapState.multiGet(Arrays.asList( Arrays.asList( (Object) this.modelName ) ));

        //ICardinality<T> cardinality_ = null;

        //if( cardinalityList != null && cardinalityList.size() != 0 ){
         //   cardinality_ = cardinalityList.get( 0 );
        //}

        //if first time
        //if( cardinality_ == null ){
        //    cardinality_ = this.cardinality;
        //}

        //update the model
        Instance<T> instance = null;

        for( TridentTuple tridentTuple : list ) {
            instance = (Instance<T>) tridentTuple.get(0);
            //do the job!
            cardinality.update(instance.getData());
            if (instance.getInstanceId() == 0) {
                this.start = System.currentTimeMillis();
            }
            synchronized (LogLogModelUpdateForSpeedTest.class) {
                /**
                 * 1000000 instance takes time
                 */
                if (instance.getInstanceId() % 100000 == 0 && instance.getInstanceId() != 0) {
                    this.end = System.currentTimeMillis();
                    this.sb.append("\n" + instance.getInstanceId() + "," + (this.end - this.start));

                    System.out.println(instance.getInstanceId() + " instance takes : " + (end - start) + " ms");

                    if (instance.getInstanceId() == 10000000) {
                        System.out.println("==============\n" + this.sb.toString());
                        System.exit(0);
                    }
                }
            }
        }
        //stored the new model
        //iCardinalityMapState.multiPut(Arrays.asList( Arrays.asList( (Object)this.modelName ) ),
         //      Arrays.asList( cardinality_ ));
    }
}
