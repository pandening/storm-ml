package com.hujian.trident.ml.frequency.SamplingCounting;

import com.hujian.trident.ml.frequency.BaseFrequency;
import com.hujian.trident.ml.frequency.CountEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujian on 2017/3/7.
 *
 * @link http://blog.csdn.net/dm_ustc/article/details/46051091
 * @paper Approximate Frequency Counts over Data Streams
 *
 * more information for this algorithm,search on google or github
 * with the key words "sampling counting algorithm"
 */
public class StickySamplingCounting<T> extends BaseFrequency<T> {

    private static final long serialVersionUID = -210720630L;

    private double maxError ;
    private long   samplingRate;
    private double t;
    private double support;
    private Long reachItemsCount;
    private long windowCount;
    private long windowLength;
    /**
     * the database
     */
    private Map<T,CountEntry<T>> database;

    /**
     * the constructor , the algorithm can be seen at the paper and
     * the link i offer.
     * @param maxError (0,1)
     * @param support  (0,1)
     * @param epsilon  (0,1)
     */
    public StickySamplingCounting(double maxError,double support,double epsilon){
        //some assert
        assert  maxError > 0 && maxError < 1 && support > 0 && support < 1
                && epsilon > 0 && epsilon < 1 :"P must in range (0,1)";
        this.support = support;
        this.maxError = maxError;
        this.samplingRate = 1;//in the beginning
        this.windowCount = 0;
        this.reachItemsCount = 0L;
        this.database = new ConcurrentHashMap<T, CountEntry<T>>();

        this.t = (1.0 / this.maxError ) * Math.log( (1.0) / (support * epsilon) );
        this.windowLength = (long)(2 * t);
    }

    @Override
    public long estimate(T item) {
        //easy to check the database to find the frequency
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
        double threshold = (this.support - this.maxError) * this.reachItemsCount;
        for( CountEntry<T> countEntry : this.database.values() ){
            if( countEntry.getFrequency() >= threshold ){
                result.add( countEntry );
            }
        }
        return result;
    }

    /**
     * for sampling
     * @return
     */
    private boolean isSamplingItem(){
        return Math.random() <= 1.0 / (double) this.samplingRate;
    }


    @Override
    public boolean add(T item, long increment) {
        boolean result = true;//default means this is an new item
        if( this.database.containsKey( item ) ){
            result = false;
            //just increment the count
            this.database.get( item ).setFrequency(
                    this.database.get( item ).getFrequency() +increment );
            this.reachItemsCount ++;
        }else{//this is an new item,if insert?
            if( this.isSamplingItem() ){
                //insert the item
                this.database.put( item , new CountEntry<T>(item,increment));
                this.reachItemsCount ++;
            }
        }

        this.windowCount ++;

        if( this.windowCount == this.windowLength ){
            this.windowCount = 0;
            this.samplingRate *= 2;
            this.windowLength = (long) (this.samplingRate * this.t);
            //update the database
            for( T item_: this.database.keySet() ){
                while( Math.random() < 0.5 ){
                    this.database.get( item_ ).setFrequency(
                            this.database.get( item_ ).getFrequency() -1);
                    if( this.database.get( item_ ).getFrequency() == 0 ){
                        //remove this item.
                        this.database.remove( item_ );
                        //done.
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public long size() {
        return this.reachItemsCount;
    }


    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

    public long getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(long samplingRate) {
        this.samplingRate = samplingRate;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public Long getReachItemsCount() {
        return reachItemsCount;
    }

    public void setReachItemsCount(Long reachItemsCount) {
        this.reachItemsCount = reachItemsCount;
    }

    public long getWindowCount() {
        return windowCount;
    }

    public void setWindowCount(long windowCount) {
        this.windowCount = windowCount;
    }

    public long getWindowLength() {
        return windowLength;
    }

    public void setWindowLength(long windowLength) {
        this.windowLength = windowLength;
    }

    public Map<T, CountEntry<T>> getDatabase() {
        return database;
    }

    public void setDatabase(Map<T, CountEntry<T>> database) {
        this.database = database;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double support) {
        this.support = support;
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
