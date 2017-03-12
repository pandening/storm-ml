package com.hujian.trident.ml.cardinality;

/**
 * Created by hujian on 2017/3/9.
 */
public class TestLinearCountingCardinality {
    public static  void main(String[] args){

        ICardinality<Double> cardinality = new LinearCountingCardinality<Double>();
        ICardinality<Double> cardinality1 = new LinearCountingCardinality<Double>();
        ICardinality<Double> cardinalityMerge = null;

        for( int i = 0 ; i < 20000; i ++ ){
            Double item =Math.random() * 100;
            cardinality.update( item );
            item = Math.random() * 100;
            cardinality1.update( item );
        }

        cardinalityMerge = cardinality.merge(cardinality1);

        System.out.println("cardinality-1:"+cardinality.cardinality());
        System.out.println("cardinality-2:"+cardinality1.cardinality());
        System.out.println("cardinality-merge:"+cardinalityMerge.cardinality());

    }
}
