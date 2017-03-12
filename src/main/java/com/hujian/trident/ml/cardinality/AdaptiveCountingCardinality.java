package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.hash.Lookup3Hash;

/**
 * Created by hujian on 2017/3/9.
 *
 * @link http://blog.codinglabs.org/articles/algorithms-for-cardinality-estimation-part-iv.html
 * @paper Fast and Accurate Traffic Matrix Measurement Using Adaptive Cardinality Counting
 *
 *  thanks github & google & baidu
 */
public class AdaptiveCountingCardinality<T> extends LogLogCardinality<T> {

    private static final long serialVersionUID = -7120212319L;

    /**
     * you can see the reason why use  0.051 to switch LC or LogLog
     * at @link http://blog.codinglabs.org/articles/algorithms-for-cardinality-estimation-part-iv.html
     */
    private final double switchRatio = 0.051;

    /**
     * the count of empty bucket
     */
    private int emptyBucket;

    /**
     * constructor
     *
     * @param k
     */
    public AdaptiveCountingCardinality(int k) {
        super(k);
        this.emptyBucket = super.m;
    }

    @Override
    public void update(T item){
        //get hash code
        long hashCode = Lookup3Hash.lookup3ycs64( item.toString() );
        int position = (int)(hashCode >>> (Long.SIZE - k));
        //find the max position
        byte values = (byte)(Long.numberOfLeadingZeros(
                (hashCode << this.k) | ( 1 << ( this.k - 1 ) )) + 1);
        if( this.maxArray[ position ] < values){
            this.maxSum += (values - this.maxArray[position]) ;
            //if empty now?
            if( this.maxArray[position] == 0 ){
                this.emptyBucket --;
            }
            this.maxArray[position] = values;
        }
    }

    @Override
    public Long cardinality(){
        //switch LC or LogLog
        double bias = this.emptyBucket / (double) this.m;
        if( bias >= this.switchRatio ){
            //use LC cardinality
            return (-1) * Math.round( this.m * Math.log( bias ) );
        }else{
            //use LogLog cardinality
            return super.cardinality();
        }
    }

    @Override
    public ICardinality<T> merge( ICardinality<T>[] cardinalityList ){
        //use super's merge method is ok!
        LogLogCardinality<T> mergeCard = (LogLogCardinality<T>) super.merge( cardinalityList );
        //get the new merge result
        //update the empty buckets
        for( byte b : mergeCard.maxArray ){
            if( b == 0 ){
                this.emptyBucket ++;
            }
        }
        return this;
    }

    public int getEmptyBucket() {
        return emptyBucket;
    }

    public void setEmptyBucket(int emptyBucket) {
        this.emptyBucket = emptyBucket;
    }

    public double getSwitchRatio() {
        return switchRatio;
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
