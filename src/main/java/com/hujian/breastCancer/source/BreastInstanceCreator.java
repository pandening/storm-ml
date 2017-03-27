package com.hujian.breastCancer.source;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/27.
 */
public class BreastInstanceCreator extends BaseFunction {
    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        //get the instance
        if( tridentTuple == null ){
            return;
        }

        BreastInstance breastInstance = null;

        double[] features = new double[9];

        for( int i =0 ;i < 9; i ++ ){
            features[i] = tridentTuple.getDouble( i );
        }

        breastInstance = new BreastInstance(features,tridentTuple.getInteger( 9 ));

        if( breastInstance == null ){
            System.out.println("--get an empty breast instance--");
            return;
        }

        tridentCollector.emit( new Values( breastInstance ));
    }
}
