package com.hujian.trident.ml.frequency.TopKCounting;

import com.hujian.trident.ml.frequency.BaseFrequency;
import com.hujian.trident.ml.frequency.CountEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujian on 2017/3/7.
 * the sample top k implementation. just keep monitoring k items.
 *
 * for more information about top-k algorithm, check out google
 * with the key words "top-k counting algorithm"
 */
public class SampleTopKCounting<T> extends BaseFrequency<T> {

    private static final long serialVersionUID = 90810L;

    private Long K;
    /**
     * the database
     */
    private Map<T,Long> database;

    private Long reachElementCount;

    /**
     * you must offer the k
     * @param k
     */
    public SampleTopKCounting( Long k ){
        this.K = k;
        this.database = new ConcurrentHashMap<T, Long>();
        this.reachElementCount = 0L;
    }

    @Override
    public long estimate(T item) {
        if( this.database.containsKey( item ) ){
            return this.database.get( item );
        }
        return 0L;
    }

    @Override
    public boolean contains(T item) {
        return this.database.containsKey( item );
    }

    @Override
    public Set<T> keySet() {
        return this.database.keySet();
    }

    @Override
    public List<CountEntry<T>> getFrequencyItemsList(double supportValue) {
        List<CountEntry<T>> result = new ArrayList<CountEntry<T>>();
        for (Map.Entry<T, Long> entry : this.database.entrySet()) {
            result.add(new CountEntry<T>(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Override
    public boolean add(T item, long increment) {
        boolean result = true;
       //existed item
        if( this.contains( item ) ){
            result = false;
            //just increment the count
            this.database.put( item, this.database.get( item ) + increment );
        }else if( this.database.size() >= this.K){
            //no so many space.remove an item from the database
            Long minCount = Long.MAX_VALUE;
            T removeItem = null;
            for( T item_ : this.database.keySet() ){
                if( removeItem == null ){
                    removeItem = item_;
                    minCount = this.database.get( item_ );
                }else{
                    if( this.database.get( item_ ) < minCount){
                        minCount = this.database.get(item_);
                        removeItem = item_;
                    }
                }
            }
            //remove the item_,and put the new item
            this.database.remove( removeItem );
            this.database.put( item, minCount + increment);
        }else{
            //just add the new item into the database
            this.database.put( item , increment );
        }

        this.reachElementCount ++;
        return result;
    }

    @Override
    public long size() {
        return this.reachElementCount;
    }


    public Long getK() {
        return K;
    }

    public void setK(Long k) {
        K = k;
    }

    public Long getReachElementCount() {
        return reachElementCount;
    }

    public void setReachElementCount(Long reachElementCount) {
        this.reachElementCount = reachElementCount;
    }

    public Map<T, Long> getDatabase() {
        return database;
    }

    public void setDatabase(Map<T, Long> database) {
        this.database = database;
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public String help(String function) {
        return null;
    }

    @Override
    public String help(Object type, String var) {
        return null;
    }

}
