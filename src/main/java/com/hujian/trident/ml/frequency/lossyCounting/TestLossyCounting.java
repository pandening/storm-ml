package com.hujian.trident.ml.frequency.lossyCounting;

import com.hujian.trident.ml.frequency.CountEntry;
import com.hujian.trident.ml.frequency.IRichFrequency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class TestLossyCounting {

    public static void main(String[] args){
        Random r = new Random();
        IRichFrequency<Integer> counter = new LossyCounting<Integer>(0.1);
        Map<Integer,Integer> hash = new HashMap<Integer, Integer>();
        for (int i=0 ;i<150; i++) {
            Integer item = r.nextInt(10);
            counter.add( Math.abs(item), 1);
            if( hash.get( item ) == null ){
                hash.put( item , 1 );
            }else{
                hash.put( item , hash.get( item ) + 1 );
            }
        }

        int k = 10;

        List<CountEntry<Integer>> topK = counter.peek(k);

        for (CountEntry<Integer> item : topK) {
            System.out.println(item);
        }

        int item = 2;
        long freq = counter.estimate(item);
        System.out.println("Query:" + item + ":" + freq);

        System.out.println("The correct statistic hash:");
        for( Map.Entry<Integer,Integer> entry : hash.entrySet() ){
            System.out.println(entry.getKey()+":"+entry.getValue());
            if( -- k == 0 ){
                break;
            }
        }
    }

}
