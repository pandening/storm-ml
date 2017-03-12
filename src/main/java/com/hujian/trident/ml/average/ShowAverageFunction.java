package com.hujian.trident.ml.average;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by hujian on 2017/3/11.
 */
public class ShowAverageFunction extends BaseFunction {
    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        Double average = tridentTuple.getDouble( 0 );
        System.out.println("<Average>"+average);
        tridentCollector.emit(new Values( average ));
    }
}
