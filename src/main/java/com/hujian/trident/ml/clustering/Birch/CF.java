package com.hujian.trident.ml.clustering.Birch;

import java.io.Serializable;

/**
 * Created by hujian on 2017/2/25.
 */
public class CF implements Serializable{

    private static final long serialVersionUID = -128976549001238907L;

    /**
     * the cf(n,ls,ss)
     */
    private Integer N ;
    private double[] LS;
    private double[] SS;

    /**
     * the features' dimen
     */
    private Integer dimen;

    /**
     * constructor
     * @param dimen
     */
    public CF(Integer dimen){
        this.dimen = dimen;
        N = 0;
        LS = new double[ dimen ];
        SS = new double[ dimen ];
    }

    /**
     * constructor
     * @param features
     */
    public CF(double[] features){
        this.N  = 1;
        this.LS = features;
        this.SS = new double[ features.length ];
        for(  int  i = 0 ; i < features.length; i ++ ){
            this.SS[ i ] = Math.pow(features[ i ], 2);
        }
    }

    /**
     * copy-constructor
     * @param cf
     */
    public CF( CF cf ){
        this.N = cf.getN();
        int length = cf.getLS().length;
        this.LS = new double[ length ];
        this.SS = new double[ length ];
        for( int i = 0; i < length; i ++ ){
            this.LS[ i ] = cf.getLS()[i];
            this.SS[ i ] = cf.getSS()[i];
        }
    }

    /**
     * get the distance to another CF
     * @param cf
     * @return
     */
    public double getDistanceTo( CF cf ){
        double distance = 0 ;
        int length = cf.getLS().length;

        for( int i = 0; i < length; i ++ ){
            distance += this.SS[i] / this.N + cf.getSS()[i]/cf.getN() -
                    2 * this.LS[i] * cf.getLS()[i] / ( this.N * cf.getN() );
        }
        return Math.sqrt( distance );
    }

    /**
     * add/sub another cf.
     *
     * @param cf
     * @param isNegativeOperator true means + , false means -
     * @return
     */
    public CF addAnotherCF( CF cf, boolean isNegativeOperator ){
        int isNegative = isNegativeOperator == false ? -1 : 1;
        this.N += cf.getN() * isNegative;
        int length  = this.LS.length;
        for( int i = 0; i < length; i ++ ){
            this.LS[ i ] += cf.getLS()[i] * isNegative;
            this.SS[ i ] += cf.getSS()[i] * isNegative;
        }
        //return new CF for follow compute
        return this;
    }

    public Integer getN() {
        return N;
    }

    public void setN(Integer n) {
        N = n;
    }

    public double[] getLS() {
        return LS;
    }

    public void setLS(double[] LS) {
        this.LS = LS;
    }

    public double[] getSS() {
        return SS;
    }

    public void setSS(double[] SS) {
        this.SS = SS;
    }

    public Integer getDimen() {
        return dimen;
    }

    public void setDimen(Integer dimen) {
        this.dimen = dimen;
    }
}
