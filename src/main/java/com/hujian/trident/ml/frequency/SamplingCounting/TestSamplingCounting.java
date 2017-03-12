package com.hujian.trident.ml.frequency.SamplingCounting;

import com.hujian.trident.ml.frequency.CountEntry;
import com.hujian.trident.ml.frequency.IRichFrequency;

import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class TestSamplingCounting {

    public static void main(String[] args){
        Random r = new Random();
        IRichFrequency<Integer> counter = new StickySamplingCounting<Integer>(0.1,0.1,0.01);
        for (int i=0 ;i<15; i++) {
            Integer item = r.nextInt(10);
            counter.add( Math.abs(item), 1);
        }

        // get the top items
        List<CountEntry<Integer>> topK = counter.peek(5);

        for (CountEntry<Integer> item : topK) {
            System.out.println(item);
        }

        //get someone item's frequency
        int item = 2;
        long freq = counter.estimate(item);
        System.out.println(item + ": " + freq);

    }
}
