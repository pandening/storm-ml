package com.hujian.trident.ml.frequency;

import java.util.Collections;
import java.util.List;

/**
 * Created by hujian on 2017/3/6.
 *
 * @link http://chuansong.me/n/2035207
 */
public abstract class BaseFrequency<T>  implements IRichFrequency<T>{

    private static final long serialVersionUID = 80872656L;

    protected double supportValue = 0.1;

    /**
     * the default constructor
     */
    public  BaseFrequency(){}

    /**
     *
     * @param supportValue
     */
    public BaseFrequency(double supportValue){
       this.supportValue = supportValue;
    }

    @Override
    public boolean add( T item ){
        return this.add(item,1);
    }

    /**
     * peek
     * @param k
     * @return
     */
    public List<CountEntry<T>> peek( int k ){
        return peek(k,this.supportValue);
    }

    /**
     *
     * @param k
     * @param support
     * @return
     */
    public  List<CountEntry<T>> peek( int k,double support ){
        //sort this peek result.
        List<CountEntry<T>> resultList = getFrequencyItemsList(support);

        Collections.sort(resultList);
        if( resultList.size() > k ){
            return resultList.subList(0,k);
        }
        return resultList;
    }

    /**
     *
     * @return
     */
    public List<CountEntry<T>> getFrequencyItemsList(){
        return this.getFrequencyItemsList(this.supportValue);
    }


    public double getSupportValue() {
        return supportValue;
    }

    public void setSupportValue(double supportValue) {
        this.supportValue = supportValue;
    }
}
