package com.hujian.trident.ml.frequency.CountSketch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 */
public class TestCountSketch {

    public static void main(String[] args){

        Random random = new Random();
        ICountSketch<Integer> counter = new CountSketchIml<Integer>();

        Map<Integer,Integer> hash = new HashMap<Integer, Integer>();

        //add item
        for( int i = 0; i < 100000; i ++ ){
            Integer item_ =  random.nextInt(10);
            counter.add(item_,1L);
            if( hash.get( item_ ) == null ){
                hash.put( item_,1 );
            }else{
                hash.put( item_,hash.get( item_ ) +1 );
            }
        }

        //get the item's count
        Integer item_1 = 3;
        Integer item_2 = 5;

        System.out.println(item_1 + " : " + counter.frequency( item_1 ));
        System.out.println(item_2 + " : " + counter.frequency( item_2 ));

        System.out.println("correct count hash:");
        for( Map.Entry<Integer,Integer> entry: hash.entrySet() ){
           System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }

}
