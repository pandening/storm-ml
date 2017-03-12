package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.hash.MurmurHash3;

import java.util.Arrays;

/**
 * Created by hujian on 2017/3/9.
 *
 * @link http://blog.codinglabs.org/articles/algorithms-for-cardinality-estimation-part-ii.html
 * @paper A Linear-Time Probabilistic Counting Algorithm for Database Applications
 *
 * @kwywords linear counting algorithm. LC algorithm etc.
 *
 * for more information about this algorithm,you can search the key words by google
 */
public class LinearCountingCardinality<T> implements ICardinality<T> {

    private static final long serialVersionUID = -712029L;

    /**
     * this is the biggest bitmap size,it's no problem to handler
     * the data set that over 1000000000 instances.
     *
     * if you want to set your bitmap size,please reference the paper's
     * author's suggestion of the bitmap size.i think you can find
     * the main idea and you can get the correct size of the bitmap.
     *
     * NOTICE: IF THE BITMAP'S SIZE IS TOO SMALL FOR YOUR DATA SET,THE
     * BITMAP WILL BE FILL BY THE DATA AFTER HASHING,THEN THE CARDINALITY
     * RESULT WILL BE INF...SO,IF YOU GET STRANGE CARDINALITY RESULT,YOU SHOULD
     * CHECK THE BITMAP'S SIZE,I THIN YOU SHOULD GET AN BIG LENGTH FOR THE
     * BITMAP TO AVOID THE ERROR.
     */
    private final int bitmapSize = 1024 * 1024;

    /**
     * this is the bitmap
     */
    private byte[] bitmap;

    private int length;
    private int zeroBitsLength;

    /**
     * you want to given the bitmap a size by yourself.
     * @param size bigger,bigger,bigger...
     */
    public LinearCountingCardinality( int size ){

        assert  size > 8 : "please let the bitmap's size bigger than 8";

        this.length = 8 * size;
        // allocate and  init the bitmap
        this.bitmap = new byte[ size ];
        for( int i = 0; i < size ; i ++ ){
            this.bitmap [ i ] = 0;
        }
        this.zeroBitsLength = this.length;
    }

    /**
     * the default constructor,i think you should get an new
     * instance by this constructor
     */
    public LinearCountingCardinality(){
        this.length = 8 * bitmapSize;
        // allocate and  init the bitmap
        this.bitmap = new byte[ bitmapSize ];
        for( int i = 0; i < bitmapSize ; i ++ ){
            this.bitmap [ i ] = 0;
        }
        this.zeroBitsLength = this.length;
    }

    /**
     * for merge some cardinality instances.
     * @param map
     */
    private LinearCountingCardinality(byte[] map){
        this.bitmap = map;
        this.length = map.length * 8;
        //get the zero bites length
        int count_ = 0 ;
        for( byte bit: map ){
            count_ += Integer.bitCount( bit & 0xFF );
        }
        this.zeroBitsLength = this.length - count_;
    }


    @Override
    public Long cardinality() {
        /**
         *  cardinality = ( -1 ) * m * ln u / m
         *
         *  m is the total length of bitmap
         *  u is the zero bites length
         *
         *  for more information,you can see the details in paper.
         */

        return ( -1 ) * Math.round( this.length *
                Math.log( this.zeroBitsLength / (double)this.length ) );
    }

    @Override
    public void update(T item) {
        //get the hash code for this item
        long hashCode = MurmurHash3.getInstance().hash( item );
        //get the bucket id
        int bucketId = (int)( (hashCode & 0xFFFFFFFFL) % (long)this.length );
        //get the bits
        byte bit = this.bitmap[ bucketId / 8 ];
        //update the bitmap.
        if( (bit & ( (byte)(1 << ( bucketId % 8 ) ))) == 0){
            this.bitmap [ bucketId / 8 ] = (byte)( bit | (byte) ( 1 << ( bucketId % 8 )) );
            this.zeroBitsLength --;
        }
    }

    @Override
    public ICardinality<T> merge(ICardinality<T>[] cardinalityList) {
        //if the list is null,just return this object
        if( cardinalityList == null || cardinalityList.length == 0 ){
            return this;
        }

        byte[] mergeBytesArray = Arrays.copyOf( this.bitmap,this.bitmap.length );

        for( ICardinality<T> card : cardinalityList ){

            if( card.bucketSize() != this.length ){
                continue;
            }

            LinearCountingCardinality<T> linearCountingCardinality =
                    (LinearCountingCardinality<T>) card;
            //merge the bytes
            for( int i = 0; i < linearCountingCardinality.bitmap.length; i ++ ){
                mergeBytesArray[ i ] |= linearCountingCardinality.bitmap[i];
            }
        }
        return new LinearCountingCardinality<T>(mergeBytesArray);
    }

    @Override
    public int bucketSize() {
        return this.length;
    }

    public int getBitmapSize() {
        return bitmapSize;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getZeroBitsLength() {
        return zeroBitsLength;
    }

    public void setZeroBitsLength(int zeroBitsLength) {
        this.zeroBitsLength = zeroBitsLength;
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
