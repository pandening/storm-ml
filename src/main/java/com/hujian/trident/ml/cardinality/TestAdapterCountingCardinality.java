package com.hujian.trident.ml.cardinality;

import java.util.Random;

/**
 * Created by hujian on 2017/3/9.
 */
public class TestAdapterCountingCardinality {
    public static  void main(String[] args){

        ICardinality<Integer> cardinality = new AdaptiveCountingCardinality<Integer>(2);
        ICardinality<Integer> cardinality1 = new AdaptiveCountingCardinality<Integer>(2);
        ICardinality<Integer> cardinalityMerge = null;
        Random random = new Random();

        for( int i = 0 ; i < 1000; i ++ ){
            Integer item =random.nextInt(1000);
            cardinality.update( item );
            item = random.nextInt(1000);
            cardinality1.update( item );
        }

        cardinalityMerge = cardinality.merge(cardinality1);

        System.out.println("cardinality-1:"+cardinality.cardinality());
        System.out.println("cardinality-2:"+cardinality1.cardinality());
        System.out.println("cardinality-merge:"+cardinalityMerge.cardinality());

    }
}
