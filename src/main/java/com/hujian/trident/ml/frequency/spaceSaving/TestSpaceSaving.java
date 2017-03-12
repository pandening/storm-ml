package com.hujian.trident.ml.frequency.spaceSaving;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/6.
 */
public class TestSpaceSaving {
    public static void main(String[] args){
        ISpaceSaving<Integer> counter = new SpaceSavingIml<Integer>(100);

        Random random = new Random();
        for (int i=0 ;i<100000; i++) {
            counter.add(random.nextInt(100), 1L);
        }

        //get top k
        int K = 10;

        List<Map.Entry<Integer,Long>> topK = counter.peek(K);

        for( Map.Entry<Integer,Long> entry : topK ){
            System.out.println(entry.getKey()+" \t" + entry.getValue());
        }

        // get some one 's count
        Integer item = 66;

        Long count = counter.frequency( item );

        System.out.println("item "+item+" :" + count);

    }
}
