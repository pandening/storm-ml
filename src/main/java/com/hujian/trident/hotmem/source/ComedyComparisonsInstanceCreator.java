package com.hujian.hotmem.source;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/23.
 */
public class ComedyComparisonsInstanceCreator extends BaseFunction implements Serializable {

    private static final long serialVersionUID = 2310111120639L;

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        if( tridentTuple == null ){
            return;
        }
        Long instanceID = tridentTuple.getLong( 0 );
        String comedyLeft = tridentTuple.getString( 1 );
        String comedyRight = tridentTuple.getString( 2 );
        String comedyVote = tridentTuple.getString( 3);

        //create an instance
        ComedyComparisonsInstance comparisonsInstance = new
                ComedyComparisonsInstance(comedyLeft,comedyRight,comedyVote,instanceID);
        //emit the instance to next bolt
        tridentCollector.emit( new Values( comparisonsInstance ));
    }
}
