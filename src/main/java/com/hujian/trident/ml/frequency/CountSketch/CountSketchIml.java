package com.hujian.trident.ml.frequency.CountSketch;

import com.hujian.trident.ml.core.HashFunctionSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hujian on 2017/3/7.
 *
 * @link http://jingpin.jikexueyuan.com/article/47011.html
 * @link https://github.com/tiepologian/CountSketch/
 * @paper http://dimacs.rutgers.edu/~graham/pubs/papers/freqvldbj.pdf
 */
public class CountSketchIml<T extends Comparable> implements ICountSketch<T> {

    private static final long serialVersionUID = 8087L;

    //use default value is very good choice
    private double epsilon = 0.01;
    private double gamma = 0.001;
    private Integer t;
    private Integer k;
    private List<List<Integer>> CArray = null;
    private List<Integer> seeds;
    private List<Integer> signSeeds;

    private Long reachTimes = 0L;

    /**
     * you want to use the default value
     */
    public CountSketchIml(){
        this.t = (int) Math.log( 4.0 / this.gamma );
        this.k = (int) (1.0 / Math.pow( this.epsilon , 2.0 ));
        this.CArray = new ArrayList<List<Integer>>();
        this.seeds = new ArrayList<Integer>();
        this.signSeeds = new ArrayList<Integer>();

        Random random = new Random();

        for( int i = 0 ;i < t; i ++ ){
            this.seeds.add( random.nextInt() );
            this.signSeeds.add( random.nextInt() );
            this.CArray.add(i,new ArrayList<Integer>(k));
            for( int j = 0; j < k; j ++ ){
                this.CArray.get(i).add(0);
            }
        }
    }

    /**
     *
     * @param epsilon
     * @param gamma
     */
    public  CountSketchIml(double epsilon,double gamma){
        this.epsilon = epsilon;
        this.gamma = gamma;
        this.t = (int) Math.log( 4.0 / this.gamma );
        this.k = (int) (1.0 / Math.pow( this.epsilon , 2.0 ));
        this.CArray = new ArrayList<List<Integer>>(t);
        this.seeds = new ArrayList<Integer>();
        this.signSeeds = new ArrayList<Integer>();

        Random random = new Random();

        for( int i = 0 ;i < t; i ++ ){
            this.seeds.add( random.nextInt() );
            this.signSeeds.add( random.nextInt() );
            this.CArray.add(i,new ArrayList<Integer>(k));
            for( int j = 0; j < k; j ++ ){
                this.CArray.get(i).add(0);
            }
        }
    }

    @Override
    public boolean add(T item, Long increment) {
        //every type will be regard as string to hash..
        StringBuilder stringBuilder = new StringBuilder();
        byte[] array = stringBuilder.append( item ).toString().getBytes();
        stringBuilder.append( item );
        for( int i = 0 ; i < this.t; i ++ ){
            long p = Math.abs(HashFunctionSet.hash64(array,array.length,this.seeds.get( i )) % this.k);
            long sign = HashFunctionSet.hash64(array,array.length,this.signSeeds.get( i )) % 2;
            //update the C array by the follow algorithm
            // C = C + update
            int update = 2 * ((int)sign * 2 - 1);
            CArray.get( i ).set( (int)p ,update + CArray.get(i).get((int)p));
        }
        this.reachTimes ++;
        return true;
    }

    @Override
    public long frequency(T item) {
        double[] values = new double[this.t];
        StringBuilder stringBuilder = new StringBuilder();
        byte[] array = stringBuilder.append( item ).toString().getBytes();
        stringBuilder.append( item );
        for( int i = 0; i < this.t; i ++ ){
            long p = Math.abs(HashFunctionSet.hash64(array,array.length,this.seeds.get( i )) % this.k);
            long sign = HashFunctionSet.hash64(array,array.length,this.signSeeds.get( i )) % 2;
            values[i] = (sign * 2 - 1) * CArray.get(i).get( (int)p );
        }
        //get the frequency,sort it by the 2TH
        double midValue = values[ this.t / 2 ];
        double[] values_ = new double[this.t];
        int index = 0;
        //less than the mid value
        for( int i = 0 ; i < this.t ; i ++ ){
            if( values[i] < midValue){
                values_[index++] = values[i];
            }
        }
        //the mid value
        for( int i = 0 ; i < this.t ; i ++ ){
            if( values[i] == midValue){
                values_[index++] = values[i];
            }
        }
        //bigger than the mid value
        for( int i = 0 ; i < this.t ; i ++ ){
            if( values[i] > midValue){
                values_[index++] = values[i];
            }
        }
        //get the frequency of the item
        return (int)values_[ this.t / 2 ];
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public List<List<Integer>> getCArray() {
        return CArray;
    }

    public void setCArray(List<List<Integer>> CArray) {
        this.CArray = CArray;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public List<Integer> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Integer> seeds) {
        this.seeds = seeds;
    }

    public List<Integer> getSignSeeds() {
        return signSeeds;
    }

    public void setSignSeeds(List<Integer> signSeeds) {
        this.signSeeds = signSeeds;
    }

    public Long getReachTimes() {
        return reachTimes;
    }

    public void setReachTimes(Long reachTimes) {
        this.reachTimes = reachTimes;
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
