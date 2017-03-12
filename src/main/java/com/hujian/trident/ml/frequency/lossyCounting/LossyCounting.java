package com.hujian.trident.ml.frequency.lossyCounting;

import com.hujian.trident.ml.frequency.BaseFrequency;
import com.hujian.trident.ml.frequency.CountEntry;
import com.hujian.trident.ml.frequency.CountEntryWithMaxError;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujian on 2017/3/7.
 * @paper Approximate Frequency Counts over Data Streams
 * @link http://blog.csdn.net/dm_ustc/article/details/46051091
 *
 * more information will be finded at google.
 */
public class LossyCounting<T> extends BaseFrequency<T>{

    private static final long serialVersionUID = 210720639L;

    /**
     * the windows size, do not change.
     */
    private Integer windowSize;

    /**
     * the current windows
     */
    private Integer currentWindow;

    private double maxError;
    private Long reachElementsCount;

    /**
     * the database will stored the total information from stream.
     */
    private Map<T,CountEntryWithMaxError<T>> database;

    /**
     * the constructor
     * @param maxError
     */
    public  LossyCounting( double maxError ){
        assert  maxError > 0 && maxError < 1 : "P max at (0,1)";

        this.maxError = maxError;
        this.windowSize = (int) Math.ceil( 1.0 / maxError );
        this.reachElementsCount = 0L;
        this.database = new ConcurrentHashMap<T, CountEntryWithMaxError<T>>();

        //get the current windows
        this.currentWindow = (int)
                Math.ceil( this.reachElementsCount / (double) this.windowSize );
    }


    @Override
    public long estimate(T item) {
       if( this.database.containsKey( item ) ){
           return this.database.get( item ).getFrequency();
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
        for (T element : this.database.keySet()) {
            CountEntry<T> entry = this.database.get(element);
            if (entry.getFrequency() >= (supportValue - this.maxError) * this.reachElementsCount) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public boolean add(T item, long increment) {
        boolean returnV = true;
        if( this.database.containsKey( item ) ){
            returnV = false;
            this.database.get( item ).setFrequency(
                    this.database.get( item ).getFrequency() + increment );
        }else{
            this.database.put( item , new CountEntryWithMaxError<T>
                    (item,increment,this.currentWindow - 1) );

        }
        this.reachElementsCount ++;
        //update the current windows
        this.currentWindow = (int)
                Math.ceil( this.reachElementsCount / (double) this.windowSize );
        //the windows is full,means the next windows need the space,so
        //please handle the current windows to model
        if( this.reachElementsCount % windowSize == 0){
            Collection<T> removes = new ArrayList<T>();
            for( T item_ : this.database.keySet() ){
                CountEntryWithMaxError<T> countEntryWithMaxError =
                        this.database.get( item_ );
                //need to be removed
                if( ( countEntryWithMaxError.getFrequency() +
                        countEntryWithMaxError.maxError ) < this.currentWindow ){
                    removes.add( item_ );
                }
            }

            //remove the removes-item
            for( T item_ : removes ){
                this.database.remove( item_ );
            }
        }
        return returnV;
    }

    @Override
    public long size() {
        return this.reachElementsCount;
    }


    public Integer getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Integer getCurrentWindow() {
        return currentWindow;
    }

    public void setCurrentWindow(Integer currentWindow) {
        this.currentWindow = currentWindow;
    }

    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

    public Map<T, CountEntryWithMaxError<T>> getDatabase() {
        return database;
    }

    public void setDatabase(Map<T, CountEntryWithMaxError<T>> database) {
        this.database = database;
    }

    public Long getReachElementsCount() {
        return reachElementsCount;
    }

    public void setReachElementsCount(Long reachElementsCount) {
        this.reachElementsCount = reachElementsCount;
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
