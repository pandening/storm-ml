package com.hujian.trident.ml.average;

/**
 * Created by hujian on 2017/3/9.
 *
 * @link http://blog.csdn.net/x_i_y_u_e/article/details/44194761
 *
 * thanks baidu & github.
 */
public class EWMAAverage implements IAverage {

    private static final long serialVersionUID = -701990012131L;

    /**
     * the decay param (0,1)
     */
    private double lamda;

    /**
     * the old average,EWMA(i-1)
     * EWMA(i) = lamda * V + (1 - lamda ) * EWMA( i - 1 )
     */
    private double average;

    /**
     * the constructor
     * @param lamda
     */
    public EWMAAverage( double lamda ){
        this.lamda = lamda;
        this.average = 0.0;
    }

    @Override
    public void update(Double value) {
        //first time or reset
        if( this.average == 0.0 ){
            this.average = value;
        }else{
            //EWMA(i) = lamda * V + (1 - lamda ) * EWMA( i - 1 )
            this.average = this.lamda * value + ( 1 - lamda ) * this.average;
        }
    }

    @Override
    public Double average() {
        return this.average;
    }

    @Override
    public void reset() {
        this.average = 0.0;
    }

    public double getLamda() {
        return lamda;
    }

    public void setLamda(double lamda) {
        this.lamda = lamda;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
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
