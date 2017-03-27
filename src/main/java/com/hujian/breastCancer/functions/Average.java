package com.hujian.breastCancer.functions;

import java.io.Serializable;

/**
 * Created by hujian on 2017/3/27.
 */
public class Average implements Serializable{

    private Double average = 0.0;
    private Double sum = 0.0;
    private Long count  = 0L;

    /**
     *
     * @param average
     * @param sum
     * @param count
     */
    public Average(double average,double sum,long count){
       this.average = average;
       this.sum = sum;
       this.count = count;
    }

    public Average(){}

    /**
     * update the model
     * @param data
     */
    public void update( double data ){
        if( this.count == 0 ){
            this.count ++;
            this.sum = data;
            this.average = data;
        }else{
            this.count ++;
            this.sum += data;
            this.average = this.sum / this.count;
        }
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
