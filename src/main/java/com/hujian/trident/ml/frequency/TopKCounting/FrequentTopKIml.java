package com.hujian.trident.ml.frequency.TopKCounting;

import com.hujian.trident.ml.frequency.CountEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by hujian on 2017/3/7.
 *
 * @keyWords Frequent,Top K,github,algorithm
 */
public class FrequentTopKIml<T> implements ITopK<T> {

    private static final long serialVersionUID = 90811L;

    private int K;
    private Long reachItemCount;
    private double maxError;
    /**
     * the database,for concurrent env
     */
    private Map<T,AtomicLong> database;

    /**
     * the constructor
     * @param error
     */
    public FrequentTopKIml(double error){
        this.maxError = error;
        this.reachItemCount = 0L;
        this.K = (int) Math.ceil(1.0 / this.maxError);
        this.database = new ConcurrentHashMap<T, AtomicLong>(this.K - 1);
    }

    @Override
    public boolean add(T item) {
        return this.add(item,1);
    }

    @Override
    public boolean add(T item, long increment) {
        boolean result = true;
        if( this.database.containsKey( item ) ){
            //increment is ok.
            this.database.get( item ).addAndGet( increment );
            result = false;
        }else if( this.database.size() >= this.K ){
            //no so much space.
            List<T> removes = new ArrayList<T>();
            long frequency = 0L;
            while( removes.size() == 0 ){
                for( Map.Entry<T,AtomicLong> entry: this.database.entrySet() ){
                    frequency = entry.getValue().decrementAndGet();
                    if( frequency == 0 ){
                        removes.add( entry.getKey() );
                    }
                }
            }
            //remove the items.
            for( T itm : removes ){
                this.database.remove( itm );
            }
            //add
            this.database.put( item, new AtomicLong( increment ) );
        }else{
            //just insert into the database
            this.database.put( item, new AtomicLong( increment ) );
        }

        this.reachItemCount ++;
        return result;
    }

    @Override
    public long size() {
        return this.reachItemCount;
    }

    @Override
    public List<CountEntry<T>> topK(int k) {
        List<CountEntry<T>> list = new ArrayList<CountEntry<T>>();
        for (Map.Entry<T, AtomicLong> entry : this.database.entrySet()) {
            list.add(new CountEntry<T>(entry.getKey(), entry.getValue().get()));
        }
        Collections.sort(list);
        return list;
    }

    public int getK() {
        return K;
    }

    public void setK(int k) {
        K = k;
    }

    public Long getReachItemCount() {
        return reachItemCount;
    }

    public void setReachItemCount(Long reachItemCount) {
        this.reachItemCount = reachItemCount;
    }

    public Map<T, AtomicLong> getDatabase() {
        return database;
    }

    public void setDatabase(Map<T, AtomicLong> database) {
        this.database = database;
    }

    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

}
