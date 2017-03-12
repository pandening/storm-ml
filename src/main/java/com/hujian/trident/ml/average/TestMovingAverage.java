package com.hujian.trident.ml.average;

/**
 * Created by hujian on 2017/3/9.
 */
public class TestMovingAverage {

    public static  void main(String[] args){

        IAverage average = new MovingAverage(10);

        for( int i = 0 ; i < 100; i ++ ){
            average.update( Math.random() * 100 );
        }

        System.out.println("average:"+average.average());

    }

}
