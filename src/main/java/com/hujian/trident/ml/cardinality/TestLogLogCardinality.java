package com.hujian.trident.ml.cardinality;

/**
 * Created by hujian on 2017/3/8.
 */
public class TestLogLogCardinality  {

    public static  void main(String[] args){

        ICardinality<Double> cardinality = new LogLogCardinality<Double>(10);
        ICardinality<Double> cardinality1 = new LogLogCardinality<Double>(10);
        ICardinality<Double> cardinalityMerge = new LogLogCardinality<Double>(10);

        for( int i = 0 ; i < 200000; i ++ ){
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
