package com.hujian.hotmem.functions;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/24.
 */
public class TailFunction extends BaseFunction implements Serializable {

    private static final long serialVersionUID = - 1333124134211498L;

    private Long instanceID = null;
    private String comedyName = null;
    private int vote = 0;

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        //get the id , comedy , vote
        if( tridentTuple == null ){
            return;
        }

        this.instanceID = tridentTuple.getLong( 0 );
        this.comedyName = tridentTuple.getString( 1 );
        this.vote = tridentTuple.getInteger( 2 );

        if( instanceID != 0 && instanceID % 1000 == 0 ){
            System.out.println("["+TailFunction.class.getName()+"] ["+instanceID+"] ["+comedyName+"] [ "+ vote + "]" );
        }
    }
}
