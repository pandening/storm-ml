package com.hujian.trident.ml.average;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by hujian on 2017/3/9.
 *
 * an implementation of sample moving average.
 *
 * @keywords average algorithm, moving average
 *
 * search on google with the key words,you can find more
 * information about this algorithm( very sample )
 */
public class MovingAverage implements IAverage {

    private static final long serialVersionUID = -70199001L;

    /**
     * the windows.
     */
    private Queue<Double> windows;

    /**
     * the windows size
     */
    private int windowSize;

    /**
     * the sum till now.
     */
    private Double sum;

    /**
     * you should set the windows size
     * @param size
     */
    public MovingAverage(int size){
        // size must bigger than 0
        assert size > 0 : "check the input size: must bigger than 0";
        this.windowSize = size;
        this.sum = 0.0;
        this.windows = new LinkedList<Double>();
    }


    @Override
    public void update(Double value) {
        //update the sum
        this.sum += value;
        this.windows.add( value );
        //if full
        if( this.windows.size() > this.windowSize ){
            sum -= this.windows.remove();
        }
    }

    @Override
    public Double average() {
        if( this.sum == 0 ){
            return 0.0;
        }
        return this.sum / (double)this.windowSize;
    }

    @Override
    public void reset() {
        this.sum = 0.0;
        this.windows.clear();
    }

    public Queue<Double> getWindows() {
        return windows;
    }

    public void setWindows(Queue<Double> windows) {
        this.windows = windows;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
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
