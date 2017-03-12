package com.hujian.trident.ml.cardinality;

import com.hujian.trident.ml.hash.MurmurHash3;

import java.util.Arrays;

/**
 * Created by hujian on 2017/3/8.
 *
 * @link http://blog.csdn.net/keshixi/article/details/46730231
 * @keywords LogLog algorithm,cardinality algorithm..
 *
 */
public class LogLogCardinality<T> implements ICardinality<T> {

    private static final long serialVersionUID = -7019L;

    /**
     * for support the result,let the result more corrector.
     */
    protected static final double[] support = {
            0, 0.44567926005415, 1.2480639342271, 2.8391255240079, 6.0165231584811,
            12.369319965552, 25.073991603109, 50.482891762521, 101.30047482549,
            202.93553337953, 406.20559693552, 812.74569741657, 1625.8258887309,
            3251.9862249084, 6504.3071471860, 13008.949929672, 26018.222470181, 52036.684135280,
            104073.41696276, 208139.24771523, 416265.57100022, 832478.53851627, 1669443.2499579, 3356902.8702907,
            6863377.8429508, 11978069.823687, 31333767.455026, 52114301.457757,
            72080129.928986, 68945006.880409, 31538957.552704, 3299942.4347441
    };

    protected int k;
    protected int m;
    protected byte[] maxArray;
    protected int maxSum;
    protected double supportValue;

    /**
     * constructor
     * @param k
     */
    public LogLogCardinality( int k ){
        assert k <= 32 : "k must in range (0,32]";

        this.k = k ;
        this.supportValue = support[this.k];
        this.m = 1 << k;
        this.maxArray = new byte[m];
        this.maxSum = 0;
    }

    @Override
    public Long cardinality() {
        //just get the ave max values,and return
        double ave = this.maxSum / (double) this.m;
        /**
         * the support is very important.
         */
        return (long)(this.supportValue * Math.pow( 2.0,ave ));
    }

    @Override
    public void update(T item) {
        //get the hash value
        int hashCode =  MurmurHash3.getInstance().hash(item);

        int position = hashCode >>> (Integer.SIZE - k);
        //find the max position
        byte values = (byte)(Integer.numberOfLeadingZeros(
                (hashCode << this.k) | ( 1 << ( this.k - 1 ) )) + 1);
        //change the bucket's value
        if( this.maxArray[position] < values){
            this.maxSum += (values - this.maxArray[position]) ;
            this.maxArray[position] = values;
        }
    }

    /**
     * i need to notice you : this function will return an "Cumulative" object
     * this is an example to you=>
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
        if( cardinalityList == null || cardinalityList.length == 0){
            return this;
        }
        byte[] mergeArray = Arrays.copyOf( this.maxArray,this.maxArray.length );

        //update the model
        for( ICardinality<T> cardinality : cardinalityList ){
            if( cardinality.bucketSize() != this.m ){
                //the bucket's size is not same,ignore it
                continue;
            }

            LogLogCardinality<T> logLogCardinality = (LogLogCardinality<T>) cardinality;

            for( int i = 0; i < mergeArray.length; i ++ ){
                mergeArray[ i ] = (byte)Math.max(mergeArray[i],
                        logLogCardinality.getMaxArray()[i]);
                /**
                 * i think we should add it.why just choose the bigger as the new value ?
                 */
                //mergeArray [ i ] += logLogCardinality.getMaxArray()[i];
            }
        }
        this.maxArray = Arrays.copyOf( mergeArray,mergeArray.length );
        //return the new model
        return this;
    }

    @Override
    public int bucketSize() {
        return this.m;
    }


    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public byte[] getMaxArray() {
        return maxArray;
    }

    public void setMaxArray(byte[] maxArray) {
        this.maxArray = maxArray;
    }

    public int getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(int maxSum) {
        this.maxSum = maxSum;
    }

    public double getSupportValue() {
        return supportValue;
    }

    public void setSupportValue(double supportValue) {
        this.supportValue = supportValue;
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
