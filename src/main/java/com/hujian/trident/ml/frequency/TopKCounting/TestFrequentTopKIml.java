package com.hujian.trident.ml.frequency.TopKCounting;

import com.hujian.trident.ml.frequency.CountEntry;

import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class TestFrequentTopKIml {
    public static  void main(String[] args){
        Random r = new Random();
        ITopK<Integer> counter = new FrequentTopKIml<Integer>(0.1);
        for (int i=0 ;i<1000; i++) {
            Integer item = r.nextInt(1000);
            counter.add( Math.abs(item), 1);
        }
        // get the top items
        List<CountEntry<Integer>> topK = counter.topK(10);

        for (CountEntry<Integer> item : topK) {
            System.out.println(item);
        }
    }
}
