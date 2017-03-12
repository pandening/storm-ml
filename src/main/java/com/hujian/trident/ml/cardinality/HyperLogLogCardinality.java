package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.hash.MurmurHash;

import java.util.Arrays;

/**
 * Created by hujian on 2017/3/8.
 *
 * @link http://blog.codinglabs.org/tag.html#
 * @link http://blog.csdn.net/keshixi/article/details/46730231
 * @link http://blog.csdn.net/yunlong34574/article/details/48494663
 * @link http://blog.csdn.net/heiyeshuwu/article/details/41248379
 * @link http://w ww.feellin.com/hyperloglogde-he-xin-si-xiang-yuan-li/
 * @link https://chenjiehua.me/database/hyperloglog-bigdata.html
 *
 * @paper http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf
 *
 * @keywords hyper LogLog algorithm,LogLog algorithm
 *
 * thanks github.
 */
public class HyperLogLogCardinality<T> implements ICardinality<T> {

    private static final long serialVersionUID = -7029L;

    private byte[] buckets;

    /**
     * how many bits can express the bucket id
     */
    private int bucketBitesSize;
    /**
     * the bucket count size
     */
    private int bucketCount;
    private double correction;

    /**
     * the constructor
     * @param bucketBitesSize value of [2,16]
     */
    public HyperLogLogCardinality( int bucketBitesSize ){
        assert bucketBitesSize >= 2 && bucketBitesSize <= 16 : "the size must in range [2,16]";

        this.bucketBitesSize = bucketBitesSize;
        this.bucketCount = 1 << bucketBitesSize;

        this.buckets = new byte[this.bucketCount];

        //init the buckets
        for( int i = 0 ;i < this.bucketCount ; i ++ ){
            this.buckets[i] = 0 ;
        }

        //get the correction value according to the bucket count
        switch (this.bucketCount){
            case 16:{
                this.correction =  0.673 * Math.pow( this.bucketCount,2 );
                break;
            }
            case 32:{
                this.correction =  0.697 * Math.pow( this.bucketCount,2 );
                break;
            }
            case 64:{
                this.correction =  0.709 * Math.pow( this.bucketCount,2 );
                break;
            }
            default:{
                this.correction =  (0.7213 / ( 1.0 + 1.079 / this.bucketCount ))
                        * Math.pow( this.bucketCount,2 );
                break;
            }
        }
    }

    @Override
    public Long cardinality() {
        int emptyBucketCount = 0;
        double v = 0.0;
        for( int i = 0; i < this.bucketCount; i ++ ){
            v += ( 1.0 / ( 1 << this.buckets [i]) );
            if( this.buckets[i] == 0){
                emptyBucketCount ++;
            }
        }
        v = 1.0 / v;
        double E = this.correction * v;
        //compute the cardinality
        if( E <= 2.5 * this.bucketCount ){
            if( emptyBucketCount == 0 ){
                return Math.round( E );
            }else{
                return Math.round(this.bucketCount
                        * Math.log(this.bucketCount / Double.valueOf(emptyBucketCount)));
            }
        }else{
            return Math.round( E );
        }
    }

    @Override
    public void update(T item) {
        //get the hash code for this item

        /**
         * you can choose which hash function to compute the hash code
         * different hash algorithm have different effect to the model.
         * you should adjust the hash function according to your data
         * structure and the real time cardinality result.
         */

        //int hashCode = MurmurHash3.MurmurHash3_x64_32(item.toString().getBytes(),1);
        int hashCode = MurmurHash.getInstance().hash( item );
        int position = (hashCode >>> ( Integer.SIZE - this.bucketBitesSize ));
        int data = (( hashCode << this.bucketBitesSize ) | ( 1 << (this.bucketBitesSize -1 )));
        int firstIndex = Integer.numberOfLeadingZeros( data ) + 1;
        //update the bucket's value to update the model
        if( this.buckets [ position ] < (byte) firstIndex){
            this.buckets[position] = (byte)firstIndex;
        }
    }

    /**
     * i need to notice you : this function will return an "Cumulative" object model
     * this is an example to you ===>
     *
     * if you have two ICardinality model run in the end.
     *
     * ICardinality_obj1 and ICardinality_obj2
     *
     *  after merge this two model ,let
     *  ICardinality_obj_merge = ICardinality_obj1.merge( ICardinality_obj2 )
     *
     *
     * @param cardinalityList
     * @return the Cumulative cardinality model
     */
    @Override
    public ICardinality<T> merge(ICardinality<T>[] cardinalityList) {
        //null?return this
        if( cardinalityList == null || cardinalityList.length == 0 ){
            return this;
        }
        byte[] mergeArray = Arrays.copyOf( this.buckets,this.buckets.length );

        //update the model
        for( ICardinality<T> cardinality : cardinalityList ){
            if( cardinality.bucketSize() != this.bucketBitesSize ){
                //the bucket's size is not same,ignore it
                continue;
            }
            HyperLogLogCardinality<T> hyperLogLogCardinality = (HyperLogLogCardinality<T>) cardinality;
            for( int i = 0; i < mergeArray.length; i ++ ){
                mergeArray[ i ] = (byte)Math.max(mergeArray[i],
                        hyperLogLogCardinality.getBuckets()[i]);
                //mergeArray [ i ] += hyperLogLogCardinality.getBuckets()[ i ];
            }
        }
        this.buckets = Arrays.copyOf( mergeArray,mergeArray.length );
        //return the new model
        return this;
    }

    @Override
    public int bucketSize() {
        return this.bucketCount;
    }

    public double getCorrection() {
        return correction;
    }

    public void setCorrection(double correction) {
        this.correction = correction;
    }

    public int getBucketCount() {
        return bucketCount;
    }

    public void setBucketCount(int bucketCount) {
        this.bucketCount = bucketCount;
    }

    public int getBucketBitesSize() {
        return bucketBitesSize;
    }

    public void setBucketBitesSize(int bucketBitesSize) {
        this.bucketBitesSize = bucketBitesSize;
    }

    public byte[] getBuckets() {
        return buckets;
    }

    public void setBuckets(byte[] buckets) {
        this.buckets = buckets;
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
