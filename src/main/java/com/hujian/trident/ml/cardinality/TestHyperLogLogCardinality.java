package com.hujian.trident.ml.cardinality;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/8.
 */
public class TestHyperLogLogCardinality {
    public static  void main(String[] args){

        ICardinality<Long> cardinality = new HyperLogLogCardinality<Long>(16);
        ICardinality<Long> cardinality1 = new HyperLogLogCardinality<Long>(16);
        ICardinality<Long> cardinalityMerge = new HyperLogLogCardinality<Long>(16);
        Map<Long,Boolean> hash = new HashMap<Long, Boolean>();
        Map<Long,Boolean> hash1 = new HashMap<Long, Boolean>();
        Map<Long,Boolean> hashMerge = new HashMap<Long, Boolean>();
        Random random = new Random();
        for( int i = 0 ; i < 40000; i ++ ){
            Long item =random.nextLong();
            cardinality.update( item );
            hash.put( item , true );
            hashMerge.put(item,true);
            item = random.nextLong();
            hash1.put( item , true);
            cardinality1.update( item );
            hashMerge.put(item,true);
        }

        //merge two model
        cardinalityMerge = cardinality.merge( cardinality1 );


        System.out.println("cardinality-1:"+cardinality.cardinality() + " \tactual-1:"+hash.size());
        System.out.println("cardinality-2:"+cardinality.cardinality() + " \tactual-2:"+hash.size());
        System.out.println("cardinality-merge:"+cardinalityMerge.cardinality() + "\tactual-merge:"+hashMerge.size());

    }
}
