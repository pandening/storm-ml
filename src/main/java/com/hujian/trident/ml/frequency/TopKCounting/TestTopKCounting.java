package com.hujian.trident.ml.frequency.TopKCounting;

import com.hujian.trident.ml.frequency.CountEntry;
import com.hujian.trident.ml.frequency.IRichFrequency;
import com.hujian.trident.ml.frequency.SamplingCounting.StickySamplingCounting;

import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class TestTopKCounting {

    public static  void main(String[] args){
        Random r = new Random();
        IRichFrequency<Integer> counter = new SampleTopKCounting<Integer>(10L);
        for (int i=0 ;i<150; i++) {
            Integer item = r.nextInt(10);
            counter.add( Math.abs(item), 1);
        }

        // get the top items
        List<CountEntry<Integer>> topK = counter.peek(10);

        for (CountEntry<Integer> item : topK) {
            System.out.println(item);
        }

        //get someone item's frequency
        int item = 2;
        long freq = counter.estimate(item);
        System.out.println(item + ": " + freq);

    }

}
